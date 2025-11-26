package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.LoginRequest
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.remote.dto.UsuariosDto
import com.example.uinavegacion.data.repository.UserApiRepository
import kotlinx.coroutines.launch

data class UsuariosUiState(
    val isLoading: Boolean = false,      // Indica si la app está cargando datos.
    val usuarios: List<UsuariosDto> = emptyList(), // Lista actual de posts mostrados en pantalla.
    val error: String? = null,           // Mensaje de error, si ocurre alguno.
    val lastActionMessage: String? = null // Mensaje de retroalimentación al realizar CRUD.
)

data class loginUiState(
    val isLoading: Boolean = false,
    val user: LoginResponse? = null,
    val error: String? = null
)

class UsuariosViewModel(
    private val repository: UserApiRepository = UserApiRepository()
) : ViewModel() {
    var uiState by mutableStateOf(UsuariosUiState())
        private set

    var loginUiState by mutableStateOf(loginUiState())
        private set

    fun loadUsuarios() {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.fetchUsuarios()
            uiState = result.fold(
                onSuccess = { data ->
                    uiState.copy(isLoading = false, usuarios = data)
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            )
        }
    }

    fun getUserById(id: Int) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.fetchuUsuarioById(id)
            uiState = result.fold(
                onSuccess = { post ->
                    uiState.copy(
                        isLoading = false,
                        usuarios = listOf(post),
                        lastActionMessage = "Cargado id=$id"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            )
        }
    }

    fun createUsuario(sample: UsuariosDto) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.create(sample)
            uiState = result.fold(
                onSuccess = { created ->
                    val updatedList = uiState.usuarios + created
                    uiState.copy(
                        isLoading = false,
                        usuarios = updatedList,
                        lastActionMessage = "Creado id=${created.id}"
                    )
                },
                onFailure = { e ->
                    uiState.copy(isLoading = false, error = e.message ?: "Error al crear")
                }
            )
        }
    }
}
