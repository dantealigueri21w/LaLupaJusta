package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion

object MotorMuestra {
    fun calcularPrediccion(muestra: List<PersonajePoblacion>): String? {
        if (muestra.isEmpty()) return null
        val conteos = muestra.groupingBy { it.rasgo }.eachCount()
        val maximo = conteos.values.max()
        return conteos.filterValues { it == maximo }.keys.sorted().first()
    }
}
