package pe.appmobile.lalupajusta.domain.model

data class RepasoPendiente(
    val itemId: String,
    val fechaUltimoFallo: Long,
    val intervaloDias: Int,
    val proximaRevision: Long,
)
