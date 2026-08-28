package pe.appmobile.lalupajusta.domain.model

/**
 * Un habitante de la población de un caso. [grupo] es el grupo social real al que pertenece
 * (ej. "amigos_patio", "salon_2") y [zona] es el lugar físico donde está parado dentro del
 * mapa del caso (ej. "zona_norte", "puesto_1") — son dos ejes de sesgo distintos: elegir
 * siempre del mismo grupo social no es lo mismo que elegir siempre a quien está más cerca.
 */
data class PersonajePoblacion(
    val id: String,
    val grupo: String,
    val zona: String,
    val rasgo: String,
    val nombre: String = "",
)

data class Caso(
    val id: String,
    val nombre: String,
    val pregunta: String,
    val poblacion: List<PersonajePoblacion>,
    val tamanoMuestraMaximo: Int,
)

data class MuestraArmada(
    val casoId: String,
    val personajesElegidosIds: List<String>,
)

data class CasoRegistrado(
    val casoId: String,
    val fecha: Long,
    val acerto: Boolean,
)
