package pe.appmobile.lalupajusta.data.repository

import pe.appmobile.lalupajusta.data.AppDatabase
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.entity.MuestraArmadaEntity
import pe.appmobile.lalupajusta.data.entity.PerfilEntity
import pe.appmobile.lalupajusta.data.entity.RachaEntity
import pe.appmobile.lalupajusta.data.entity.RepasoPendienteEntity
import pe.appmobile.lalupajusta.data.entity.ResultadoCasoEntity
import pe.appmobile.lalupajusta.data.seed.SeedData
import pe.appmobile.lalupajusta.domain.engine.MotorMuestra
import pe.appmobile.lalupajusta.domain.engine.MotorPoblacionReal
import pe.appmobile.lalupajusta.domain.engine.MotorProgreso
import pe.appmobile.lalupajusta.domain.engine.MotorRepaso
import pe.appmobile.lalupajusta.domain.engine.MotorSesgo
import pe.appmobile.lalupajusta.domain.model.CasoRegistrado
import pe.appmobile.lalupajusta.domain.model.Perfil
import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import pe.appmobile.lalupajusta.domain.model.RepasoPendiente

data class ResultadoCasoResumen(
    val prediccionMuestra: String?,
    val valorRealPoblacion: String?,
    val acerto: Boolean,
    val tipoSesgo: String?,
)

data class CasoConEstado(val caso: CasoEntity, val resuelto: Boolean)

class LupaJustaRepository(private val db: AppDatabase) {

    suspend fun sembrarSiEsPrimerLanzamiento() {
        if (db.casoDao().obtenerTodos().isNotEmpty()) return
        db.casoDao().insertarTodos(SeedData.casos)
        db.personajePoblacionDao().insertarTodos(SeedData.poblaciones)
        db.insigniaDao().insertarTodas(SeedData.insignias)
    }

    suspend fun obtenerPerfil(): Perfil? =
        db.perfilDao().obtener()?.let { Perfil(alias = it.alias, avatarId = it.avatarId) }

    suspend fun guardarPerfil(alias: String, avatarId: Int) {
        db.perfilDao().guardar(PerfilEntity(alias = alias, avatarId = avatarId))
    }

    suspend fun obtenerCasos(): List<CasoEntity> = db.casoDao().obtenerTodos()

    suspend fun obtenerPoblacionDeCaso(casoId: String): List<PersonajePoblacion> =
        db.personajePoblacionDao().obtenerPorCaso(casoId).map {
            PersonajePoblacion(id = it.id, grupo = it.grupo, zona = it.zona, rasgo = it.rasgo, nombre = it.nombre)
        }

    suspend fun resolverCaso(casoId: String, personajesElegidosIds: List<String>): ResultadoCasoResumen {
        val poblacion = obtenerPoblacionDeCaso(casoId)
        // Mapea la lista de ids elegidos (preserva repeticiones y orden) en vez de filtrar la
        // poblacion ya unica por pertenencia -- filtrar eliminaba cualquier repeticion antes de
        // que MotorSesgo pudiera detectarla, y el sesgo por "repeticion" nunca se disparaba
        // jugando de verdad (ver handoffs/HANDOFF-68-LA-LUPA-JUSTA.md, seccion 0.2).
        val muestra = personajesElegidosIds.mapNotNull { id -> poblacion.firstOrNull { it.id == id } }

        val prediccion = MotorMuestra.calcularPrediccion(muestra)
        val valorReal = MotorPoblacionReal.calcularValorReal(poblacion)
        val tipoSesgo = MotorSesgo.detectarSesgo(muestra, poblacion)
        val acerto = prediccion != null && prediccion == valorReal
        val ahora = System.currentTimeMillis()

        db.muestraArmadaDao().insertar(
            MuestraArmadaEntity(casoId = casoId, personajesElegidosCsv = personajesElegidosIds.joinToString(","), fecha = ahora),
        )
        db.resultadoCasoDao().insertar(
            ResultadoCasoEntity(
                casoId = casoId,
                fecha = ahora,
                prediccionMuestra = prediccion,
                valorRealPoblacion = valorReal,
                acerto = acerto,
                tipoSesgo = tipoSesgo,
            ),
        )

        actualizarProgreso()
        if (!acerto) {
            registrarIntentoFallido(casoId, ahora)
        }

        return ResultadoCasoResumen(prediccion, valorReal, acerto, tipoSesgo)
    }

    suspend fun obtenerCasosConEstado(): List<CasoConEstado> {
        val resueltos = db.resultadoCasoDao().obtenerTodos().map { it.casoId }.toSet()
        return db.casoDao().obtenerTodos().sortedBy { it.orden }.map { caso ->
            CasoConEstado(caso, caso.id in resueltos)
        }
    }

    suspend fun obtenerResultados(): List<ResultadoCasoEntity> = db.resultadoCasoDao().obtenerTodos()

    suspend fun obtenerPendientesDeRepasoHoy(hoy: Long): List<RepasoPendiente> =
        db.repasoPendienteDao().obtenerPendientesParaHoy(hoy).map {
            RepasoPendiente(it.itemId, it.fechaUltimoFallo, it.intervaloDias, it.proximaRevision)
        }

    private suspend fun registrarIntentoFallido(casoId: String, ahora: Long) {
        val existente = db.repasoPendienteDao().obtenerPorId(casoId)
        val nuevoIntervalo = MotorRepaso.calcularProximoIntervalo(existente?.intervaloDias ?: 1, acerto = false)
        db.repasoPendienteDao().guardar(
            RepasoPendienteEntity(
                itemId = casoId,
                fechaUltimoFallo = ahora,
                intervaloDias = nuevoIntervalo,
                proximaRevision = MotorRepaso.calcularProximaRevision(ahora, nuevoIntervalo),
            ),
        )
    }

    private suspend fun actualizarProgreso() {
        val historial = db.resultadoCasoDao().obtenerTodos().map {
            CasoRegistrado(casoId = it.casoId, fecha = it.fecha, acerto = it.acerto)
        }
        val yaGanadas = db.insigniaDao().obtenerIdsGanadas().toSet()
        val nuevas = MotorProgreso.calcularNuevasInsignias(historial, yaGanadas)
        val ahora = System.currentTimeMillis()
        nuevas.forEach { db.insigniaDao().marcarObtenida(it, ahora) }

        val racha = MotorProgreso.calcularRacha(historial, hoy = ahora)
        db.rachaDao().guardar(RachaEntity(diasConsecutivos = racha, ultimaFechaActividad = ahora))
    }
}
