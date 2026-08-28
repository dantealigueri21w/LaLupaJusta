package pe.appmobile.lalupajusta.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.AppDatabase

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LupaJustaRepositoryHomeTest {
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
    fun `elegir a la misma persona dos veces detecta sesgo de repeticion a traves del repositorio`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val alguien = repository.obtenerPoblacionDeCaso("juego_favorito").first()
        val resultado = repository.resolverCaso("juego_favorito", listOf(alguien.id, alguien.id))
        assertEquals("repeticion", resultado.tipoSesgo)
    }

    @Test
    fun `sin resultados los 8 casos aparecen pendientes`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val casos = repository.obtenerCasosConEstado()
        assertEquals(8, casos.size)
        assertTrue(casos.none { it.resuelto })
    }

    @Test
    fun `un caso con un resultado registrado aparece resuelto`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val poblacion = repository.obtenerPoblacionDeCaso("juego_favorito")
        repository.resolverCaso("juego_favorito", poblacion.take(3).map { it.id })
        val actualizado = repository.obtenerCasosConEstado().first { it.caso.id == "juego_favorito" }
        assertTrue(actualizado.resuelto)
    }
}
