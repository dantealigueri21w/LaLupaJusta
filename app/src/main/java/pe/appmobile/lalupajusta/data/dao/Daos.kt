package pe.appmobile.lalupajusta.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.entity.InsigniaEntity
import pe.appmobile.lalupajusta.data.entity.MuestraArmadaEntity
import pe.appmobile.lalupajusta.data.entity.PerfilEntity
import pe.appmobile.lalupajusta.data.entity.PersonajePoblacionEntity
import pe.appmobile.lalupajusta.data.entity.RachaEntity
import pe.appmobile.lalupajusta.data.entity.RepasoPendienteEntity
import pe.appmobile.lalupajusta.data.entity.ResultadoCasoEntity

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfil WHERE id = 1")
    suspend fun obtener(): PerfilEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(perfil: PerfilEntity)
}

@Dao
interface CasoDao {
    @Query("SELECT * FROM caso ORDER BY orden")
    suspend fun obtenerTodos(): List<CasoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(casos: List<CasoEntity>)

    @Query("DELETE FROM caso WHERE id = :id")
    suspend fun eliminar(id: String)
}

@Dao
interface PersonajePoblacionDao {
    @Query("SELECT * FROM personaje_poblacion WHERE casoId = :casoId")
    suspend fun obtenerPorCaso(casoId: String): List<PersonajePoblacionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(personajes: List<PersonajePoblacionEntity>)
}

@Dao
interface MuestraArmadaDao {
    @Insert
    suspend fun insertar(muestra: MuestraArmadaEntity): Long

    @Query("SELECT * FROM muestra_armada ORDER BY fecha")
    suspend fun obtenerTodas(): List<MuestraArmadaEntity>
}

@Dao
interface ResultadoCasoDao {
    @Insert
    suspend fun insertar(resultado: ResultadoCasoEntity): Long

    @Query("SELECT * FROM resultado_caso ORDER BY fecha")
    suspend fun obtenerTodos(): List<ResultadoCasoEntity>
}

@Dao
interface InsigniaDao {
    @Query("SELECT * FROM insignia")
    suspend fun obtenerTodas(): List<InsigniaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(insignias: List<InsigniaEntity>)

    @Query("UPDATE insignia SET fechaObtenida = :fecha WHERE id = :insigniaId")
    suspend fun marcarObtenida(insigniaId: String, fecha: Long)

    @Query("SELECT id FROM insignia WHERE fechaObtenida IS NOT NULL")
    suspend fun obtenerIdsGanadas(): List<String>
}

@Dao
interface RachaDao {
    @Query("SELECT * FROM racha WHERE id = 1")
    suspend fun obtener(): RachaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(racha: RachaEntity)
}

@Dao
interface RepasoPendienteDao {
    @Query("SELECT * FROM repaso_pendiente WHERE proximaRevision <= :hoy")
    suspend fun obtenerPendientesParaHoy(hoy: Long): List<RepasoPendienteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: RepasoPendienteEntity)

    @Query("SELECT * FROM repaso_pendiente WHERE itemId = :itemId")
    suspend fun obtenerPorId(itemId: String): RepasoPendienteEntity?
}
