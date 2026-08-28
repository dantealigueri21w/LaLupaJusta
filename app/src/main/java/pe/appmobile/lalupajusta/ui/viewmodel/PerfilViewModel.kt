package pe.appmobile.lalupajusta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.appmobile.lalupajusta.data.repository.LupaJustaRepository

private const val ALIAS_POR_DEFECTO = "Reportero Curioso"
private const val AVATAR_POR_DEFECTO = 1

data class PerfilUiState(
    val alias: String = ALIAS_POR_DEFECTO,
    val avatarId: Int = AVATAR_POR_DEFECTO,
    val guardado: Boolean = false,
    val cargando: Boolean = true,
)

class PerfilViewModel(private val repository: LupaJustaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val perfil = repository.obtenerPerfil()
            _uiState.value = if (perfil != null) {
                PerfilUiState(alias = perfil.alias, avatarId = perfil.avatarId, cargando = false)
            } else {
                PerfilUiState(cargando = false)
            }
        }
    }

    fun cambiarAlias(nuevoAlias: String) {
        _uiState.value = _uiState.value.copy(alias = nuevoAlias, guardado = false)
    }

    fun elegirAvatar(avatarId: Int) {
        _uiState.value = _uiState.value.copy(avatarId = avatarId, guardado = false)
    }

    fun guardar() {
        val estado = _uiState.value
        val aliasFinal = estado.alias.ifBlank { ALIAS_POR_DEFECTO }
        viewModelScope.launch {
            repository.guardarPerfil(alias = aliasFinal, avatarId = estado.avatarId)
            _uiState.value = _uiState.value.copy(alias = aliasFinal, guardado = true)
        }
    }

    class Factory(private val repository: LupaJustaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = PerfilViewModel(repository) as T
    }
}
