package pe.appmobile.lalupajusta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.appmobile.lalupajusta.data.entity.CasoEntity
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository
import pe.appmobile.lalupajusta.data.repository.ResultadoCasoResumen
import pe.appmobile.lalupajusta.domain.engine.MotorMuestra
import pe.appmobile.lalupajusta.domain.model.PersonajePoblacion

data class CasoUiState(
    val caso: CasoEntity? = null,
    val poblacion: List<PersonajePoblacion> = emptyList(),
    val idsElegidos: List<String> = emptyList(),
    val prediccionEnVivo: String? = null,
    val resultado: ResultadoCasoResumen? = null,
    val mostrarAyuda: Boolean = false,
    val cargando: Boolean = true,
)

class CasoViewModel(
    private val repository: LupaJustaRepository,
    private val casoId: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CasoUiState())
    val uiState: StateFlow<CasoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sembrarSiEsPrimerLanzamiento()
            val caso = repository.obtenerCasos().first { it.id == casoId }
            val poblacion = repository.obtenerPoblacionDeCaso(casoId)
            _uiState.value = CasoUiState(caso = caso, poblacion = poblacion, cargando = false)
        }
    }

    /**
     * Cada toque SE SUMA a la lista, incluso si ya se habia tocado a esa persona -- repetir es
     * una accion real posible (ver "Antes de empezar" del plan de pantallas: sin esto, "Sin
     * Trampas" no significaria nada y el sesgo por repeticion nunca se podria jugar de verdad).
     */
    fun tocarPersonaje(personajeId: String) {
        val estado = _uiState.value
        val caso = estado.caso ?: return
        if (estado.resultado != null) return
        if (estado.idsElegidos.size >= caso.tamanoMuestraMaximo) return

        val nuevosIds = estado.idsElegidos + personajeId
        _uiState.value = estado.copy(idsElegidos = nuevosIds, prediccionEnVivo = calcularPrediccion(nuevosIds))
    }

    /**
     * Deshacer solo corrige un toque accidental: deja de estar disponible una vez confirmada la
     * muestra (estado.resultado != null), asi que no es un reintento infinito para tantear hasta
     * que la prediccion en vivo coincida con lo que el nino ya sabe o sospecha del resultado.
     */
    fun deshacerUltimoToque() {
        val estado = _uiState.value
        if (estado.idsElegidos.isEmpty() || estado.resultado != null) return
        val nuevosIds = estado.idsElegidos.dropLast(1)
        _uiState.value = estado.copy(idsElegidos = nuevosIds, prediccionEnVivo = calcularPrediccion(nuevosIds))
    }

    fun alternarAyuda() {
        _uiState.value = _uiState.value.copy(mostrarAyuda = !_uiState.value.mostrarAyuda)
    }

    private fun calcularPrediccion(ids: List<String>): String? {
        val poblacion = _uiState.value.poblacion
        val muestra = ids.mapNotNull { id -> poblacion.firstOrNull { it.id == id } }
        return MotorMuestra.calcularPrediccion(muestra)
    }

    fun confirmarMuestra() {
        val estado = _uiState.value
        if (estado.idsElegidos.isEmpty()) return
        viewModelScope.launch {
            val resultado = repository.resolverCaso(casoId, estado.idsElegidos)
            _uiState.value = _uiState.value.copy(resultado = resultado)
        }
    }

    class Factory(
        private val repository: LupaJustaRepository,
        private val casoId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CasoViewModel(repository, casoId) as T
    }
}
