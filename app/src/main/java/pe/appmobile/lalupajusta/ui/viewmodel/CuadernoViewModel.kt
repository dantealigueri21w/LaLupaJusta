package pe.appmobile.lalupajusta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository

data class EntradaCuaderno(
    val nombreCaso: String,
    val prediccion: String?,
    val valorReal: String?,
    val acerto: Boolean,
    val tipoSesgo: String?,
)

data class CuadernoUiState(
    val entradas: List<EntradaCuaderno> = emptyList(),
    val cargando: Boolean = true,
)

class CuadernoViewModel(private val repository: LupaJustaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CuadernoUiState())
    val uiState: StateFlow<CuadernoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val casos = repository.obtenerCasos().associateBy { it.id }
            val resultados = repository.obtenerResultados()
            val entradas = resultados.map { resultado ->
                EntradaCuaderno(
                    nombreCaso = casos[resultado.casoId]?.nombre ?: resultado.casoId,
                    prediccion = resultado.prediccionMuestra,
                    valorReal = resultado.valorRealPoblacion,
                    acerto = resultado.acerto,
                    tipoSesgo = resultado.tipoSesgo,
                )
            }
            _uiState.value = CuadernoUiState(entradas = entradas, cargando = false)
        }
    }

    class Factory(private val repository: LupaJustaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CuadernoViewModel(repository) as T
    }
}
