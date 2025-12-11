package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.remote.dto.UserUpdateRequestDto
import com.example.uinavegacion.data.repository.UserApiRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class EditProfileViewModel(
    private val repo: UserApiRepository,
    private val userId: Long
) : ViewModel() {

    var uiState by mutableStateOf(EditProfileState())
        private set

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repo.getUserById(userId)
            uiState = uiState.copy(
                name = user.name ?: "",
                email = user.email ?: "",
                phone = user.phone ?: ""
            )
        }
    }

    fun onNameChange(v: String) {
        uiState = uiState.copy(name = v, nameError = null)
    }

    fun onEmailChange(v: String) {
        uiState = uiState.copy(email = v, emailError = null)
    }

    fun onPhoneChange(v: String) {
        uiState = uiState.copy(phone = v, phoneError = null)
    }

    fun onPasswordChange(v: String) {
        uiState = uiState.copy(password = v, passwordError = null)
    }

    // ---------------- VALIDACIONES ------------------

    fun validateAndSubmit(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val s = uiState

        if (s.name.length < 2) {
            uiState = uiState.copy(nameError = "Debe tener mínimo 2 caracteres")
            return
        }

        if (!s.email.endsWith("@duocuc.cl")) {
            uiState = uiState.copy(emailError = "El email debe ser @duocuc.cl")
            return
        }

        if (s.phone.isBlank() || !s.phone.matches(Regex("\\+569\\d{8}"))) {
            uiState = uiState.copy(phoneError = "Teléfono debe ser +569 seguido de 8 dígitos")
            return
        }

        if (s.password.isNotBlank()) {
            if (s.password.length < 6) {
                uiState = uiState.copy(passwordError = "Contraseña mínimo 6 caracteres")
                return
            }
            if (!s.password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).*$"))) {
                uiState = uiState.copy(passwordError = "Debe incluir una mayúscula y un número")
                return
            }
        }

        updateUser(onSuccess, onError)
    }

    // ---------------- UPDATE ------------------

    private fun updateUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                val body = UserUpdateRequestDto(
                    name = uiState.name,
                    email = uiState.email,
                    phone = uiState.phone,
                    password = uiState.password.ifBlank { "" }
                )

                repo.updateUser(userId, body)

                uiState = uiState.copy(isLoading = false)

                onSuccess()

            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
                onError(e.message ?: "Error desconocido")
            }
        }
    }
}




data class EditProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)