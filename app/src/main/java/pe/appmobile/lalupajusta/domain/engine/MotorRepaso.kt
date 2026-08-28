package pe.appmobile.lalupajusta.domain.engine

import pe.appmobile.lalupajusta.domain.model.RepasoPendiente
import java.util.concurrent.TimeUnit

object MotorRepaso {
    fun calcularProximoIntervalo(intervaloAnteriorDias: Int, acerto: Boolean): Int =
        if (acerto) intervaloAnteriorDias * 2 else 1

    fun calcularProximaRevision(fechaFallo: Long, intervaloDias: Int): Long =
        fechaFallo + TimeUnit.DAYS.toMillis(intervaloDias.toLong())

    fun itemsPendientesParaHoy(pendientes: List<RepasoPendiente>, hoy: Long): List<RepasoPendiente> =
        pendientes.filter { it.proximaRevision <= hoy }
}
