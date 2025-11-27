package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.LoginRequest
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.remote.dto.UserRequestDto
import com.example.uinavegacion.data.remote.dto.UserResponseDto
import com.example.uinavegacion.data.remote.dto.UsuariosDto
import com.example.uinavegacion.data.repository.UserApiRepository
import kotlinx.coroutines.launch

data class UsuariosUiState(
    val isLoading: Boolean = false,      // Indica si la app está cargando datos.
    val usuarios: List<UserResponseDto> = emptyList(), // Lista actual de posts mostrados en pantalla.
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





    fun createUsuario(sample: UserRequestDto) {
        uiState = uiState.copy(isLoading = true, error = null, lastActionMessage = null)
        viewModelScope.launch {
            val result = repository.registerUser(sample)
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
