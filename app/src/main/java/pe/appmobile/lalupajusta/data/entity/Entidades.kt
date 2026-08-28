package pe.appmobile.lalupajusta.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val id: Int = 1,
    val alias: String,
    val avatarId: Int,
)

@Entity(tableName = "caso")
data class CasoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val pregunta: String,
    val tamanoMuestraMaximo: Int,
    val orden: Int,
)

@Entity(
    tableName = "personaje_poblacion",
    foreignKeys = [
        ForeignKey(entity = CasoEntity::class, parentColumns = ["id"], childColumns = ["casoId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("casoId")],
)
data class PersonajePoblacionEntity(
    @PrimaryKey val id: String,
    val casoId: String,
    val nombre: String,
    val grupo: String,
    val zona: String,
    val rasgo: String,
)

@Entity(
    tableName = "muestra_armada",
    foreignKeys = [
        ForeignKey(entity = CasoEntity::class, parentColumns = ["id"], childColumns = ["casoId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("casoId")],
)
data class MuestraArmadaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val casoId: String,
    val personajesElegidosCsv: String,
    val fecha: Long,
)

@Entity(
    tableName = "resultado_caso",
    foreignKeys = [
        ForeignKey(entity = CasoEntity::class, parentColumns = ["id"], childColumns = ["casoId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("casoId")],
)
data class ResultadoCasoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val casoId: String,
    val fecha: Long,
    val prediccionMuestra: String?,
    val valorRealPoblacion: String?,
    val acerto: Boolean,
    val tipoSesgo: String?,
)

@Entity(tableName = "insignia")
data class InsigniaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val fechaObtenida: Long?,
)

@Entity(tableName = "racha")
data class RachaEntity(
    @PrimaryKey val id: Int = 1,
    val diasConsecutivos: Int,
    val ultimaFechaActividad: Long,
)

@Entity(tableName = "repaso_pendiente")
data class RepasoPendienteEntity(
    @PrimaryKey val itemId: String,
    val fechaUltimoFallo: Long,
    val intervaloDias: Int,
    val proximaRevision: Long,
)
