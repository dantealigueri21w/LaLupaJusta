package pe.appmobile.lalupajusta.data

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.appmobile.lalupajusta.data.dao.CasoDao
import pe.appmobile.lalupajusta.data.dao.InsigniaDao
import pe.appmobile.lalupajusta.data.dao.MuestraArmadaDao
import pe.appmobile.lalupajusta.data.dao.PerfilDao
import pe.appmobile.lalupajusta.data.dao.PersonajePoblacionDao
import pe.appmobile.lalupajusta.data.dao.RachaDao
import pe.appmobile.lalupajusta.data.dao.RepasoPendienteDao
import pe.appmobile.lalupajusta.data.dao.ResultadoCasoDao
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.entity.InsigniaEntity
import pe.appmobile.lalupajusta.data.entity.MuestraArmadaEntity
import pe.appmobile.lalupajusta.data.entity.PerfilEntity
import pe.appmobile.lalupajusta.data.entity.PersonajePoblacionEntity
import pe.appmobile.lalupajusta.data.entity.RachaEntity
import pe.appmobile.lalupajusta.data.entity.RepasoPendienteEntity
import pe.appmobile.lalupajusta.data.entity.ResultadoCasoEntity

@Database(
    entities = [
        PerfilEntity::class,
        CasoEntity::class,
        PersonajePoblacionEntity::class,
        MuestraArmadaEntity::class,
        ResultadoCasoEntity::class,
        InsigniaEntity::class,
        RachaEntity::class,
        RepasoPendienteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
    abstract fun casoDao(): CasoDao
    abstract fun personajePoblacionDao(): PersonajePoblacionDao
    abstract fun muestraArmadaDao(): MuestraArmadaDao
    abstract fun resultadoCasoDao(): ResultadoCasoDao
    abstract fun insigniaDao(): InsigniaDao
    abstract fun rachaDao(): RachaDao
    abstract fun repasoPendienteDao(): RepasoPendienteDao
}
