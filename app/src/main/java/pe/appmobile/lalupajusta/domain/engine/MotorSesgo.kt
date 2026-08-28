package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion

object MotorSesgo {
    private const val UMBRAL_SESGO = 0.3f

    fun detectarSesgo(
        muestra: List<PersonajePoblacion>,
        poblacionCompleta: List<PersonajePoblacion>,
    ): String? {
        val idsElegidos = muestra.map { it.id }
        if (idsElegidos.size != idsElegidos.toSet().size) return "repeticion"

        if (haySesgoPorAtributo(muestra, poblacionCompleta) { it.grupo }) return "grupo"
        if (haySesgoPorAtributo(muestra, poblacionCompleta) { it.zona }) return "cercania"

        return null
    }

    private fun haySesgoPorAtributo(
        muestra: List<PersonajePoblacion>,
        poblacionCompleta: List<PersonajePoblacion>,
        atributo: (PersonajePoblacion) -> String,
    ): Boolean {
        if (muestra.isEmpty() || poblacionCompleta.isEmpty()) return false

        val proporcionPoblacion = poblacionCompleta.groupingBy(atributo).eachCount()
            .mapValues { it.value.toFloat() / poblacionCompleta.size }
        val proporcionMuestra = muestra.groupingBy(atributo).eachCount()
            .mapValues { it.value.toFloat() / muestra.size }

        return proporcionMuestra.any { (valor, propioEnMuestra) ->
            val propioEnPoblacion = proporcionPoblacion[valor] ?: 0f
            kotlin.math.abs(propioEnMuestra - propioEnPoblacion) > UMBRAL_SESGO
        }
    }
}
