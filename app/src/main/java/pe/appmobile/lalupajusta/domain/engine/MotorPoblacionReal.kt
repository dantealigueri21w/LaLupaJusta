package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion

object MotorPoblacionReal {
    fun calcularValorReal(poblacionCompleta: List<PersonajePoblacion>): String? =
        MotorMuestra.calcularPrediccion(poblacionCompleta)

    fun calcularComposicionPorGrupo(poblacionCompleta: List<PersonajePoblacion>): Map<String, Float> {
        if (poblacionCompleta.isEmpty()) return emptyMap()
        return poblacionCompleta.groupingBy { it.grupo }.eachCount()
            .mapValues { it.value.toFloat() / poblacionCompleta.size }
    }
}
