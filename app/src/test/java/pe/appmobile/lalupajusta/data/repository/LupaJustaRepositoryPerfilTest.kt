package pe.appmobile.lalupajusta.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.AppDatabase

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LupaJustaRepositoryPerfilTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: LupaJustaRepository

    @Before
    fun crearDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries().build()
        repository = LupaJustaRepository(db)
    }

    @After
    fun cerrarDb() {
        db.close()
    }

    @Test
    fun `sin perfil guardado todavia obtenerPerfil devuelve null`() = runTest {
        assertNull(repository.obtenerPerfil())
    }

    @Test
    fun `guardar un perfil lo deja disponible para leer de vuelta`() = runTest {
        repository.guardarPerfil(alias = "Reportero Curioso", avatarId = 4)
        val perfil = repository.obtenerPerfil()
        assertEquals("Reportero Curioso", perfil?.alias)
        assertEquals(4, perfil?.avatarId)
    }

    @Test
    fun `guardar el perfil una segunda vez reemplaza el alias y avatar anteriores`() = runTest {
        repository.guardarPerfil(alias = "Reportero Curioso", avatarId = 4)
        repository.guardarPerfil(alias = "Lupa Veloz", avatarId = 9)
        val perfil = repository.obtenerPerfil()
        assertEquals("Lupa Veloz", perfil?.alias)
        assertEquals(9, perfil?.avatarId)
    }
}
