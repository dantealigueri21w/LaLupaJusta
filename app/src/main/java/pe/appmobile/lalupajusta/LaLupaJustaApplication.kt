package pe.appmobile.lalupajusta

import android.app.Application
import androidx.room.Room
import pe.appmobile.lalupajusta.data.AppDatabase
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository

class LaLupaJustaApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: LupaJustaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "lalupajusta.db").build()
        repository = LupaJustaRepository(database)
    }
}
