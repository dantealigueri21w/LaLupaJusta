package pe.appmobile.lalupajusta.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pe.appmobile.lalupajusta.data.AppDatabase

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LupaJustaRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: LupaJustaRepository

    @Before
    fun crearDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        repository = LupaJustaRepository(db)
    }

    @After
    fun cerrarDb() {
        db.close()
    }

    @Test
    fun `sembrar en una base de datos vacia inserta los 8 casos y sus poblaciones reales`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        assertEquals(8, repository.obtenerCasos().size)
        assertEquals(32, repository.obtenerPoblacionDeCaso("juego_favorito").size)
        assertEquals(48, repository.obtenerPoblacionDeCaso("caso_final").size)
    }

    @Test
    fun `sembrar dos veces no duplica la poblacion`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        repository.sembrarSiEsPrimerLanzamiento()
        assertEquals(32, repository.obtenerPoblacionDeCaso("juego_favorito").size)
    }

    @Test
    fun `resolver el caso con la muestra del grupo trampa predice mal y detecta sesgo de grupo`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val amigosPatio = repository.obtenerPoblacionDeCaso("juego_favorito").filter { it.grupo == "amigos_patio" }
        val resultado = repository.resolverCaso("juego_favorito", amigosPatio.map { it.id })
        assertFalse(resultado.acerto)
        assertEquals("grupo", resultado.tipoSesgo)
        assertEquals("saltar_soga", resultado.prediccionMuestra)
        assertEquals("futbol", resultado.valorRealPoblacion)
    }

    @Test
    fun `resolver el caso con una muestra proporcional de varios grupos acierta y no detecta sesgo`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val poblacion = repository.obtenerPoblacionDeCaso("juego_favorito")
        val muestraProporcional = poblacion.filter { it.grupo == "salon_a" }.take(2) +
            poblacion.filter { it.grupo == "salon_b" }.take(2)
        val resultado = repository.resolverCaso("juego_favorito", muestraProporcional.map { it.id })
        assertTrue(resultado.acerto)
        assertNull(resultado.tipoSesgo)
    }

    @Test
    fun `resolver un caso guarda el resultado y la muestra armada`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val poblacion = repository.obtenerPoblacionDeCaso("juego_favorito")
        repository.resolverCaso("juego_favorito", poblacion.take(3).map { it.id })
        assertEquals(1, db.resultadoCasoDao().obtenerTodos().size)
        assertEquals(1, db.muestraArmadaDao().obtenerTodas().size)
    }

    @Test
    fun `resolver el primer caso otorga la insignia Primer Punado`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val poblacion = repository.obtenerPoblacionDeCaso("juego_favorito")
        repository.resolverCaso("juego_favorito", poblacion.take(3).map { it.id })
        assertTrue("primer_punado" in db.insigniaDao().obtenerIdsGanadas())
    }

    @Test
    fun `resolver los 8 casos otorga Pueblo Completo`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        repository.obtenerCasos().forEach { caso ->
            val poblacion = repository.obtenerPoblacionDeCaso(caso.id)
            repository.resolverCaso(caso.id, poblacion.take(3).map { it.id })
        }
        assertTrue("pueblo_completo" in db.insigniaDao().obtenerIdsGanadas())
    }

    @Test
    fun `un caso resuelto con prediccion equivocada queda pendiente de repaso`() = runTest {
        repository.sembrarSiEsPrimerLanzamiento()
        val amigosPatio = repository.obtenerPoblacionDeCaso("juego_favorito").filter { it.grupo == "amigos_patio" }
        repository.resolverCaso("juego_favorito", amigosPatio.map { it.id })
        val dosDiasDespues = System.currentTimeMillis() + 2 * 24L * 60 * 60 * 1000
        assertEquals(1, repository.obtenerPendientesDeRepasoHoy(hoy = dosDiasDespues).size)
    }
}
