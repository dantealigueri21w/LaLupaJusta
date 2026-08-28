package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.CasoRegistrado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorProgresoTest {

    private fun caso(casoId: String, acerto: Boolean = true, fecha: Long = 0L) =
        CasoRegistrado(casoId = casoId, fecha = fecha, acerto = acerto)

    @Test
    fun `sin casos registrados no hay insignias nuevas`() {
        val nuevas = MotorProgreso.calcularNuevasInsignias(emptyList(), emptySet())
        assertTrue(nuevas.isEmpty())
    }

    @Test
    fun `el primer caso resuelto otorga Primer Punado`() {
        val nuevas = MotorProgreso.calcularNuevasInsignias(listOf(caso("caso1")), emptySet())
        assertTrue("primer_punado" in nuevas)
    }

    @Test
    fun `Primer Punado no se repite si ya estaba ganada`() {
        val nuevas = MotorProgreso.calcularNuevasInsignias(listOf(caso("caso1")), setOf("primer_punado"))
        assertFalse("primer_punado" in nuevas)
    }

    @Test
    fun `8 casos distintos resueltos otorgan Pueblo Completo`() {
        val historial = (1..8).map { caso("caso$it") }
        val nuevas = MotorProgreso.calcularNuevasInsignias(historial, setOf("primer_punado"))
        assertTrue("pueblo_completo" in nuevas)
    }

    @Test
    fun `7 casos distintos resueltos NO otorgan Pueblo Completo todavia`() {
        val historial = (1..7).map { caso("caso$it") }
        val nuevas = MotorProgreso.calcularNuevasInsignias(historial, setOf("primer_punado"))
        assertFalse("pueblo_completo" in nuevas)
    }

    @Test
    fun `calcularRacha cuenta dias consecutivos con al menos un caso resuelto`() {
        val unDiaEnMillis = 24L * 60 * 60 * 1000
        val historial = listOf(
            caso("caso1", fecha = 0L),
            caso("caso2", fecha = unDiaEnMillis),
            caso("caso3", fecha = unDiaEnMillis * 2),
        )
        assertEquals(3, MotorProgreso.calcularRacha(historial, hoy = unDiaEnMillis * 2))
    }

    @Test
    fun `sin progreso hay exactamente 3 casos desbloqueados`() {
        val desbloqueados = (1..8).count { orden -> MotorProgreso.estaDesbloqueado(orden, casosCompletados = 0) }
        assertEquals(3, desbloqueados)
    }

    @Test
    fun `con 2 completados hay 5 casos desbloqueados`() {
        val desbloqueados = (1..8).count { orden -> MotorProgreso.estaDesbloqueado(orden, casosCompletados = 2) }
        assertEquals(5, desbloqueados)
    }

    @Test
    fun `el ultimo caso se abre con 5 completados`() {
        assertTrue(MotorProgreso.estaDesbloqueado(orden = 8, casosCompletados = 5))
        assertFalse(MotorProgreso.estaDesbloqueado(orden = 8, casosCompletados = 4))
    }
}
