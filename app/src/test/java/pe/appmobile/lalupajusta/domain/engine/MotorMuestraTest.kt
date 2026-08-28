package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MotorMuestraTest {

    private fun personaje(id: String, rasgo: String, grupo: String = "g", zona: String = "z") =
        PersonajePoblacion(id, grupo, zona, rasgo)

    @Test
    fun `muestra vacia no produce prediccion`() {
        assertNull(MotorMuestra.calcularPrediccion(emptyList()))
    }

    @Test
    fun `muestra de un solo personaje predice su propio rasgo`() {
        val muestra = listOf(personaje("p1", "futbol"))
        assertEquals("futbol", MotorMuestra.calcularPrediccion(muestra))
    }

    @Test
    fun `muestra con mayoria clara predice el rasgo mayoritario`() {
        val muestra = listOf(
            personaje("p1", "futbol"), personaje("p2", "futbol"), personaje("p3", "saltar_soga"),
        )
        assertEquals("futbol", MotorMuestra.calcularPrediccion(muestra))
    }

    @Test
    fun `empate entre dos rasgos se resuelve por orden alfabetico`() {
        val muestra = listOf(personaje("p1", "saltar_soga"), personaje("p2", "futbol"))
        assertEquals("futbol", MotorMuestra.calcularPrediccion(muestra))
    }

    @Test
    fun `muestra con todos los rasgos distintos predice el primero en orden alfabetico`() {
        val muestra = listOf(personaje("p1", "voley"), personaje("p2", "futbol"), personaje("p3", "canicas"))
        assertEquals("canicas", MotorMuestra.calcularPrediccion(muestra))
    }

    @Test
    fun `caso El Juego Favorito -- una muestra del mismo grupo de amigos predice lo que juegan ellos`() {
        // El primer caso de la ficha: Chihua pregunta solo a los vecinos mas cercanos, los tres
        // del mismo grupo de amigos, y los tres juegan lo mismo.
        val muestra = listOf(
            personaje("vecino1", "saltar_soga", grupo = "amigos_patio"),
            personaje("vecino2", "saltar_soga", grupo = "amigos_patio"),
            personaje("vecino3", "saltar_soga", grupo = "amigos_patio"),
        )
        assertEquals("saltar_soga", MotorMuestra.calcularPrediccion(muestra))
    }

    @Test
    fun `muestra grande con distribucion realista predice el rasgo con mas votos`() {
        val muestra = listOf(
            personaje("p1", "futbol"), personaje("p2", "futbol"), personaje("p3", "futbol"),
            personaje("p4", "futbol"), personaje("p5", "voley"), personaje("p6", "voley"),
            personaje("p7", "voley"), personaje("p8", "canicas"), personaje("p9", "canicas"),
            personaje("p10", "saltar_soga"),
        )
        assertEquals("futbol", MotorMuestra.calcularPrediccion(muestra))
    }
}
