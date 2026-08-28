package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.CasoRegistrado
import java.util.concurrent.TimeUnit

object MotorProgreso {
    fun calcularNuevasInsignias(
        historial: List<CasoRegistrado>,
        insigniasYaGanadas: Set<String>,
    ): Set<String> {
        val nuevas = mutableSetOf<String>()

        if (historial.isNotEmpty() && "primer_punado" !in insigniasYaGanadas) {
            nuevas += "primer_punado"
        }

        val casosDistintos = historial.map { it.casoId }.toSet()
        if (casosDistintos.size >= 8 && "pueblo_completo" !in insigniasYaGanadas) {
            nuevas += "pueblo_completo"
        }

        return nuevas
    }

    fun calcularRacha(historial: List<CasoRegistrado>, hoy: Long): Int {
        if (historial.isEmpty()) return 0
        val diasConActividad = historial
            .map { TimeUnit.MILLISECONDS.toDays(it.fecha) }
            .toSortedSet()

        var racha = 0
        var diaActual = TimeUnit.MILLISECONDS.toDays(hoy)
        while (diaActual in diasConActividad) {
            racha++
            diaActual--
        }
        return racha
    }

    /**
     * Un caso se desbloquea si es uno de los 3 primeros del orden semilla, o si ya se
     * completaron suficientes casos anteriores (sección 5.1 v13 del maestro: al menos 3
     * abiertos desde el primer minuto, el resto por progreso real, alcanzable en 1 sesión).
     */
    fun estaDesbloqueado(orden: Int, casosCompletados: Int): Boolean =
        orden <= 3 || casosCompletados >= orden - 3
}
