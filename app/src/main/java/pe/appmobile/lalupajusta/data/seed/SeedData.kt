package pe.appmobile.lalupajusta.data.seed

import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.entity.InsigniaEntity
import pe.appmobile.lalupajusta.data.entity.PersonajePoblacionEntity

private data class ClusterPersonajes(val grupo: String, val zona: String, val rasgo: String, val cantidad: Int)

private val NOMBRES = listOf(
    "Ana", "Luis", "Marco", "Rosa", "Juan", "Flor", "Pedro", "Lucia", "Diego", "Carmen",
    "Jose", "Elena", "Mario", "Sofia", "Victor", "Nora", "Hugo", "Paola", "Raul", "Ines",
    "Cesar", "Mayra", "Omar", "Lorena", "Tito", "Vilma", "Saul", "Erika", "Franco", "Nelly",
    "Hector", "Rocio", "Ivan", "Yolanda", "Bruno", "Gladys", "Nestor", "Diana", "Walter", "Silvia",
    "Adrian", "Karen", "Felix", "Milagros", "Ruben", "Betty", "Gino", "Susan",
)

/**
 * Genera una poblacion determinista para un caso a partir de clusteres reales (grupo, zona,
 * rasgo, cuantos). Los nombres se ciclan de [NOMBRES] en orden, asi que la misma lista de
 * clusteres siempre produce exactamente la misma poblacion -- nada de aleatorio.
 */
private fun generarPoblacion(casoId: String, clusters: List<ClusterPersonajes>): List<PersonajePoblacionEntity> {
    val personajes = mutableListOf<PersonajePoblacionEntity>()
    var indice = 0
    clusters.forEach { cluster ->
        repeat(cluster.cantidad) {
            val nombre = NOMBRES[indice % NOMBRES.size]
            personajes += PersonajePoblacionEntity(
                id = "${casoId}_${nombre.lowercase()}_$indice",
                casoId = casoId,
                nombre = nombre,
                grupo = cluster.grupo,
                zona = cluster.zona,
                rasgo = cluster.rasgo,
            )
            indice++
        }
    }
    return personajes
}

object SeedData {

    val casos = listOf(
        CasoEntity("juego_favorito", "El Juego Favorito", "¿Cuál es el juego favorito de todo el patio de recreo?", tamanoMuestraMaximo = 6, orden = 1),
        CasoEntity("delegado_salon", "El Delegado del Salón", "¿Quién gana la elección de todo el salón de clase?", tamanoMuestraMaximo = 6, orden = 2),
        CasoEntity("sabor_helado", "El Sabor de Helado", "¿Cuál sabor se vende más en todo el mercado?", tamanoMuestraMaximo = 6, orden = 3),
        CasoEntity("equipo_favorito", "El Equipo Favorito", "¿Qué equipo prefiere la mayoría de toda la cancha?", tamanoMuestraMaximo = 6, orden = 4),
        CasoEntity("hora_dormir", "La Hora de Dormir", "¿A qué hora se duerme la mayoría de todo el barrio?", tamanoMuestraMaximo = 6, orden = 5),
        CasoEntity("mascota_preferida", "La Mascota Preferida", "¿Qué mascota prefiere la mayoría de toda la plaza?", tamanoMuestraMaximo = 6, orden = 6),
        CasoEntity("transporte_colegio", "El Transporte al Colegio", "¿Cómo llega la mayoría de todo el pueblo al colegio?", tamanoMuestraMaximo = 6, orden = 7),
        CasoEntity("caso_final", "El Caso Final", "Pregunta compuesta: ¿qué predicción es la real, combinando dos posibles sesgos a la vez?", tamanoMuestraMaximo = 8, orden = 8),
    )

    val poblaciones: List<PersonajePoblacionEntity> =
        generarPoblacion("juego_favorito", listOf(
            ClusterPersonajes("amigos_patio", "patio", "saltar_soga", 6),
            ClusterPersonajes("salon_a", "patio", "futbol", 10),
            ClusterPersonajes("salon_b", "patio", "futbol", 8),
            ClusterPersonajes("salon_c", "patio", "voley", 5),
            ClusterPersonajes("salon_d", "patio", "canicas", 3),
        )) +
        generarPoblacion("delegado_salon", listOf(
            ClusterPersonajes("salon", "fila_cercana", "candidato_ana", 6),
            ClusterPersonajes("salon", "fila_media", "candidato_luis", 15),
            ClusterPersonajes("salon", "fila_lejana", "candidato_luis", 5),
            ClusterPersonajes("salon", "fila_lejana", "candidato_ana", 4),
        )) +
        generarPoblacion("sabor_helado", listOf(
            ClusterPersonajes("mercado", "puesto_1", "fresa", 8),
            ClusterPersonajes("mercado", "puesto_2", "chocolate", 10),
            ClusterPersonajes("mercado", "puesto_3", "chocolate", 9),
            ClusterPersonajes("mercado", "puesto_4", "vainilla", 5),
        )) +
        generarPoblacion("equipo_favorito", listOf(
            ClusterPersonajes("con_camiseta_local", "cancha", "equipo_local", 7),
            ClusterPersonajes("sin_camiseta", "cancha", "equipo_visita", 12),
            ClusterPersonajes("sin_camiseta", "cancha", "equipo_local", 8),
            ClusterPersonajes("con_camiseta_visita", "cancha", "equipo_visita", 5),
        )) +
        generarPoblacion("hora_dormir", listOf(
            ClusterPersonajes("barrio", "afuera_noche", "tarde", 6),
            ClusterPersonajes("barrio", "en_casa_temprano", "temprano", 14),
            ClusterPersonajes("barrio", "en_casa_tarde", "temprano", 12),
        )) +
        generarPoblacion("mascota_preferida", listOf(
            ClusterPersonajes("tiene_mascota", "plaza", "gato", 8),
            ClusterPersonajes("sin_mascota", "plaza", "perro", 17),
            ClusterPersonajes("sin_mascota", "plaza", "gato", 7),
        )) +
        generarPoblacion("transporte_colegio", listOf(
            ClusterPersonajes("pueblo", "cerca_paradero", "bus", 6),
            ClusterPersonajes("pueblo", "lejos_paradero_norte", "caminando", 15),
            ClusterPersonajes("pueblo", "lejos_paradero_sur", "caminando", 11),
        )) +
        generarPoblacion("caso_final", listOf(
            ClusterPersonajes("amigos_cercanos", "zona_norte", "prediccion_a", 8),
            ClusterPersonajes("grupo_b", "zona_norte", "prediccion_b", 10),
            ClusterPersonajes("grupo_b", "zona_sur", "prediccion_b", 10),
            ClusterPersonajes("grupo_c", "zona_sur", "prediccion_b", 12),
            ClusterPersonajes("grupo_c", "zona_este", "prediccion_b", 8),
        ))

    val insignias = listOf(
        InsigniaEntity("primer_punado", "Primer Puñado", "Resolver el primer caso", null),
        InsigniaEntity("ojo_disperso", "Ojo Disperso", "Armar una muestra con personajes de al menos 3 grupos distintos, 5 veces", null),
        InsigniaEntity("prediccion_exacta", "Predicción Exacta", "Acertar la predicción con un margen de error mínimo, 3 veces", null),
        InsigniaEntity("sesgo_detectado", "Sesgo Detectado", "Identificar correctamente por qué una muestra salió sesgada, 5 veces", null),
        InsigniaEntity("pueblo_completo", "Pueblo Completo", "Resolver los 8 casos", null),
        InsigniaEntity("muestra_minima_justa", "Muestra Mínima Justa", "Acertar con la muestra más chica permitida en un caso", null),
        InsigniaEntity("ayuda_a_chihua", "Ayuda a Chihua", "Corregir a Chihua cuando quiere preguntar solo a los cercanos, 5 veces", null),
        InsigniaEntity("cuaderno_lleno", "Cuaderno Lleno", "8 casos registrados con su análisis completo", null),
        InsigniaEntity("sin_trampas", "Sin Trampas", "Resolver un caso sin repetir la misma persona dos veces en la muestra", null),
        InsigniaEntity("comparacion_real", "Comparación Real", "Consultar el dato real de la población completa en cada uno de los 8 casos", null),
        InsigniaEntity("racha_de_reportero", "Racha de Reportero", "5 días seguidos con al menos un caso avanzado", null),
    )
}
