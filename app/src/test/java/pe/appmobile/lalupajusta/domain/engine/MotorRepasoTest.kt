package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.RepasoPendiente
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class MotorRepasoTest {
    @Test
    fun `un acierto duplica el intervalo anterior`() {
        assertEquals(8, MotorRepaso.calcularProximoIntervalo(intervaloAnteriorDias = 4, acerto = true))
    }

    @Test
    fun `un fallo reinicia el intervalo a 1 sin importar cuan grande era antes`() {
        assertEquals(1, MotorRepaso.calcularProximoIntervalo(intervaloAnteriorDias = 16, acerto = false))
    }

    @Test
    fun `un intervalo que empieza en 1 y acierta pasa a 2, no se queda en 1`() {
        assertEquals(2, MotorRepaso.calcularProximoIntervalo(intervaloAnteriorDias = 1, acerto = true))
    }

    @Test
    fun `la proxima revision suma los dias de intervalo en milisegundos a la fecha del fallo`() {
        val unDia = TimeUnit.DAYS.toMillis(1)
        assertEquals(unDia * 3, MotorRepaso.calcularProximaRevision(fechaFallo = 0L, intervaloDias = 3))
    }

    @Test
    fun `un item cuya revision ya llego aparece como pendiente para hoy`() {
        val pendiente = RepasoPendiente("juego_favorito", fechaUltimoFallo = 0L, intervaloDias = 1, proximaRevision = 1000L)
        assertTrue(MotorRepaso.itemsPendientesParaHoy(listOf(pendiente), hoy = 1000L).isNotEmpty())
    }

    @Test
    fun `un item cuya revision es en el futuro no aparece pendiente todavia`() {
        val pendiente = RepasoPendiente("juego_favorito", fechaUltimoFallo = 0L, intervaloDias = 1, proximaRevision = 5000L)
        assertTrue(MotorRepaso.itemsPendientesParaHoy(listOf(pendiente), hoy = 1000L).isEmpty())
    }
}
