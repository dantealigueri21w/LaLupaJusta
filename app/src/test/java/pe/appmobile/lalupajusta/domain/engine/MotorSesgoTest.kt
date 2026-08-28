package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MotorSesgoTest {

    private fun personaje(id: String, grupo: String, zona: String, rasgo: String = "x") =
        PersonajePoblacion(id, grupo, zona, rasgo)

    // Población base para varios tests: 8 personajes, grupo 50/50, zona 50/50, ambos ejes
    // cruzados (cada grupo tiene gente en las dos zonas) para poder aislar cada tipo de sesgo.
    private val poblacionBalanceada = listOf(
        personaje("p1", grupo = "A", zona = "norte"),
        personaje("p2", grupo = "A", zona = "sur"),
        personaje("p3", grupo = "B", zona = "norte"),
        personaje("p4", grupo = "B", zona = "sur"),
        personaje("p5", grupo = "A", zona = "norte"),
        personaje("p6", grupo = "A", zona = "sur"),
        personaje("p7", grupo = "B", zona = "norte"),
        personaje("p8", grupo = "B", zona = "sur"),
    )

    @Test
    fun `muestra proporcional a la poblacion en grupo y zona no tiene sesgo`() {
        val muestra = listOf(
            poblacionBalanceada[0], // p1 A norte
            poblacionBalanceada[1], // p2 A sur
            poblacionBalanceada[2], // p3 B norte
            poblacionBalanceada[3], // p4 B sur
        )
        assertNull(MotorSesgo.detectarSesgo(muestra, poblacionBalanceada))
    }

    @Test
    fun `muestra que sobre-representa un grupo frente a su proporcion real detecta sesgo de grupo`() {
        val muestra = listOf(
            poblacionBalanceada[0], // p1 A norte
            poblacionBalanceada[1], // p2 A sur
            poblacionBalanceada[4], // p5 A norte
            poblacionBalanceada[5], // p6 A sur
        ) // 100% grupo A (zona sigue 50/50 -- aisla el sesgo a "grupo")
        assertEquals("grupo", MotorSesgo.detectarSesgo(muestra, poblacionBalanceada))
    }

    @Test
    fun `muestra con la misma persona elegida dos veces detecta sesgo por repeticion`() {
        val p1 = poblacionBalanceada[0]
        val muestra = listOf(p1, p1)
        assertEquals("repeticion", MotorSesgo.detectarSesgo(muestra, poblacionBalanceada))
    }

    @Test
    fun `muestra muy chica que se mantiene dentro de la proporcion real no es un falso positivo`() {
        // Poblacion muy sesgada de por si (80% grupo A y zona norte a la vez): tocar 1 solo
        // personaje de ese 80% mayoritario da una diferencia de 20 puntos, dentro del umbral.
        val poblacion = (1..8).map { personaje("a$it", grupo = "A", zona = "norte") } +
            (1..2).map { personaje("b$it", grupo = "B", zona = "sur") }
        val muestra = listOf(poblacion[0])
        assertNull(MotorSesgo.detectarSesgo(muestra, poblacion))
    }

    @Test
    fun `poblacion de un solo grupo y una sola zona hace imposible cualquier sesgo de grupo o cercania`() {
        val poblacion = (1..6).map { personaje("u$it", grupo = "unico", zona = "unica") }
        val muestra = listOf(poblacion[0], poblacion[1], poblacion[2])
        assertNull(MotorSesgo.detectarSesgo(muestra, poblacion))
    }

    @Test
    fun `muestra que sobre-representa una zona sin sobre-representar el grupo detecta sesgo de cercania`() {
        val muestra = listOf(
            poblacionBalanceada[0], // p1 A norte
            poblacionBalanceada[2], // p3 B norte
        ) // grupo 50/50 (matches poblacion), zona 100% norte (poblacion es 50/50) -- aisla a "cercania"
        assertEquals("cercania", MotorSesgo.detectarSesgo(muestra, poblacionBalanceada))
    }

    @Test
    fun `cuando hay sesgo de grupo y de cercania a la vez se reporta primero el de grupo`() {
        val muestra = listOf(
            poblacionBalanceada[0], // p1 A norte
            poblacionBalanceada[4], // p5 A norte
        ) // 100% grupo A Y 100% zona norte a la vez: ambos superan el umbral
        assertEquals("grupo", MotorSesgo.detectarSesgo(muestra, poblacionBalanceada))
    }

    @Test
    fun `una diferencia moderada por debajo del umbral no marca sesgo pero superarlo si`() {
        // NOTA para quien implemente: se evita a propósito una diferencia de exactamente 30
        // puntos (0.3f) contra el umbral -- comparar un resultado de resta en Float justo
        // contra el límite es frágil por redondeo binario (0.3 no es exactamente representable
        // en punto flotante). Se usan en cambio márgenes de al menos 5 puntos a cada lado.

        // Poblacion 75% grupo A / 25% grupo B (misma zona para todos, aisla el eje a "grupo"):
        // una muestra 100% grupo A da una diferencia de 25 puntos, por debajo del umbral de 30.
        val poblacionDebajo = (1..3).map { personaje("a$it", grupo = "A", zona = "z") } +
            personaje("b1", grupo = "B", zona = "z")
        val muestraDebajo = listOf(poblacionDebajo[0])
        assertNull(MotorSesgo.detectarSesgo(muestraDebajo, poblacionDebajo))

        // Poblacion 60% grupo A / 40% grupo B: la misma muestra de 1 persona de grupo A ahora
        // da una diferencia de 40 puntos, por encima del umbral -- si debe marcar sesgo.
        val poblacionEncima = (1..3).map { personaje("a$it", grupo = "A", zona = "z") } +
            (1..2).map { personaje("b$it", grupo = "B", zona = "z") }
        val muestraEncima = listOf(poblacionEncima[0])
        assertEquals("grupo", MotorSesgo.detectarSesgo(muestraEncima, poblacionEncima))
    }
}
