package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MotorPoblacionRealTest {

    private fun personaje(id: String, rasgo: String, grupo: String) =
        PersonajePoblacion(id, grupo, "zona_unica", rasgo)

    @Test
    fun `poblacion vacia no tiene valor real`() {
        assertNull(MotorPoblacionReal.calcularValorReal(emptyList()))
    }

    @Test
    fun `poblacion con mayoria clara calcula el valor real correcto`() {
        val poblacion = listOf(
            personaje("p1", "futbol", "g1"), personaje("p2", "futbol", "g2"),
            personaje("p3", "futbol", "g1"), personaje("p4", "voley", "g2"),
        )
        assertEquals("futbol", MotorPoblacionReal.calcularValorReal(poblacion))
    }

    @Test
    fun `poblacion con empate resuelve el valor real de forma deterministica`() {
        val poblacion = listOf(personaje("p1", "voley", "g1"), personaje("p2", "futbol", "g2"))
        assertEquals("futbol", MotorPoblacionReal.calcularValorReal(poblacion))
    }

    @Test
    fun `composicion por grupo devuelve proporciones que suman 1`() {
        val poblacion = listOf(
            personaje("p1", "x", "amigos_patio"), personaje("p2", "x", "amigos_patio"),
            personaje("p3", "x", "salon_2"), personaje("p4", "x", "salon_2"),
        )
        val composicion = MotorPoblacionReal.calcularComposicionPorGrupo(poblacion)
        assertEquals(0.5f, composicion["amigos_patio"] ?: 0f, 0.001f)
        assertEquals(0.5f, composicion["salon_2"] ?: 0f, 0.001f)
        assertEquals(1.0f, composicion.values.sum(), 0.001f)
    }

    @Test
    fun `composicion por grupo de poblacion vacia es un mapa vacio`() {
        assertTrue(MotorPoblacionReal.calcularComposicionPorGrupo(emptyList()).isEmpty())
    }
}
