package pe.appmobile.lalupajusta.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.entity.InsigniaEntity
import pe.appmobile.lalupajusta.data.entity.MuestraArmadaEntity
import pe.appmobile.lalupajusta.data.entity.PersonajePoblacionEntity
import pe.appmobile.lalupajusta.data.entity.RachaEntity
import pe.appmobile.lalupajusta.data.entity.RepasoPendienteEntity
import pe.appmobile.lalupajusta.data.entity.ResultadoCasoEntity

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDatabaseTest {
    private lateinit var db: AppDatabase

    @Before
    fun crearDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun cerrarDb() {
        db.close()
    }

    @Test
    fun `base de datos recien creada no tiene personajes`() = runTest {
        assertTrue(db.personajePoblacionDao().obtenerPorCaso("juego_favorito").isEmpty())
    }

    @Test
    fun `insertar y leer personajes de un caso los devuelve completos`() = runTest {
        db.casoDao().insertarTodos(listOf(CasoEntity("juego_favorito", "El Juego Favorito", "¿Pregunta?", 6, 1)))
        db.personajePoblacionDao().insertarTodos(listOf(
            PersonajePoblacionEntity("juego_favorito_ana_0", "juego_favorito", "Ana", "amigos_patio", "patio", "saltar_soga"),
        ))
        val personajes = db.personajePoblacionDao().obtenerPorCaso("juego_favorito")
        assertEquals(1, personajes.size)
        assertEquals("Ana", personajes.first().nombre)
    }

    @Test
    fun `los personajes solo devuelven los de su propio caso`() = runTest {
        db.casoDao().insertarTodos(listOf(
            CasoEntity("juego_favorito", "El Juego Favorito", "¿Pregunta?", 6, 1),
            CasoEntity("sabor_helado", "El Sabor de Helado", "¿Pregunta?", 6, 3),
        ))
        db.personajePoblacionDao().insertarTodos(listOf(
            PersonajePoblacionEntity("juego_favorito_ana_0", "juego_favorito", "Ana", "amigos_patio", "patio", "saltar_soga"),
            PersonajePoblacionEntity("sabor_helado_luis_0", "sabor_helado", "Luis", "mercado", "puesto_1", "fresa"),
        ))
        assertEquals(1, db.personajePoblacionDao().obtenerPorCaso("juego_favorito").size)
    }

    @Test
    fun `borrar un caso borra en cascada su poblacion`() = runTest {
        db.casoDao().insertarTodos(listOf(CasoEntity("juego_favorito", "El Juego Favorito", "¿Pregunta?", 6, 1)))
        db.personajePoblacionDao().insertarTodos(listOf(PersonajePoblacionEntity("juego_favorito_ana_0", "juego_favorito", "Ana", "amigos_patio", "patio", "saltar_soga")))
        db.casoDao().eliminar("juego_favorito")
        assertTrue(db.personajePoblacionDao().obtenerPorCaso("juego_favorito").isEmpty())
    }

    @Test
    fun `una muestra armada insertada queda en el historial`() = runTest {
        db.casoDao().insertarTodos(listOf(CasoEntity("juego_favorito", "El Juego Favorito", "¿Pregunta?", 6, 1)))
        db.muestraArmadaDao().insertar(MuestraArmadaEntity(casoId = "juego_favorito", personajesElegidosCsv = "a,b,c", fecha = 1000L))
        assertEquals(1, db.muestraArmadaDao().obtenerTodas().size)
    }

    @Test
    fun `un resultado de caso insertado guarda prediccion, valor real y tipo de sesgo`() = runTest {
        db.casoDao().insertarTodos(listOf(CasoEntity("juego_favorito", "El Juego Favorito", "¿Pregunta?", 6, 1)))
        db.resultadoCasoDao().insertar(
            ResultadoCasoEntity(casoId = "juego_favorito", fecha = 1000L, prediccionMuestra = "saltar_soga", valorRealPoblacion = "futbol", acerto = false, tipoSesgo = "grupo"),
        )
        val resultado = db.resultadoCasoDao().obtenerTodos().first()
        assertEquals("grupo", resultado.tipoSesgo)
    }

    @Test
    fun `marcar una insignia como obtenida la refleja en los ids ganados`() = runTest {
        db.insigniaDao().insertarTodas(listOf(InsigniaEntity("primer_punado", "Primer Puñado", "Resolver el primer caso", fechaObtenida = null)))
        assertTrue(db.insigniaDao().obtenerIdsGanadas().isEmpty())
        db.insigniaDao().marcarObtenida("primer_punado", fecha = 5000L)
        assertEquals(listOf("primer_punado"), db.insigniaDao().obtenerIdsGanadas())
    }

    @Test
    fun `guardar la racha dos veces reemplaza el valor anterior`() = runTest {
        db.rachaDao().guardar(RachaEntity(diasConsecutivos = 1, ultimaFechaActividad = 1000L))
        db.rachaDao().guardar(RachaEntity(diasConsecutivos = 2, ultimaFechaActividad = 2000L))
        assertEquals(2, db.rachaDao().obtener()?.diasConsecutivos)
    }

    @Test
    fun `sin racha guardada todavia obtener devuelve null`() = runTest {
        assertNull(db.rachaDao().obtener())
    }

    @Test
    fun `un item de repaso solo aparece pendiente para hoy cuando su fecha ya llego`() = runTest {
        val unDia = 24L * 60 * 60 * 1000
        db.repasoPendienteDao().guardar(RepasoPendienteEntity("juego_favorito", fechaUltimoFallo = 0L, intervaloDias = 1, proximaRevision = unDia))
        assertTrue(db.repasoPendienteDao().obtenerPendientesParaHoy(hoy = unDia - 1000).isEmpty())
        assertEquals(1, db.repasoPendienteDao().obtenerPendientesParaHoy(hoy = unDia).size)
    }

    @Test
    fun `un caso insertado se lee con su tamano de muestra maximo real`() = runTest {
        db.casoDao().insertarTodos(listOf(CasoEntity("caso_final", "El Caso Final", "¿Pregunta compuesta?", tamanoMuestraMaximo = 8, orden = 8)))
        assertEquals(8, db.casoDao().obtenerTodos().first().tamanoMuestraMaximo)
    }
}
