package pe.appmobile.lalupajusta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.appmobile.lalupajusta.data.repository.CasoConEstado
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository

data class HomeUiState(
    val casos: List<CasoConEstado> = emptyList(),
    val alias: String? = null,
    val avatarId: Int = 1,
    val cargando: Boolean = true,
)

class HomeViewModel(private val repository: LupaJustaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sembrarSiEsPrimerLanzamiento()
            cargar()
        }
    }

    fun recargar() {
        viewModelScope.launch { cargar() }
    }

    private suspend fun cargar() {
        val perfil = repository.obtenerPerfil()
        _uiState.value = HomeUiState(
            casos = repository.obtenerCasosConEstado(),
            alias = perfil?.alias,
            avatarId = perfil?.avatarId ?: 1,
            cargando = false,
        )
    }

    class Factory(private val repository: LupaJustaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
    }
}
