package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.repository.UserApiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LoginState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val canSubmit: Boolean = true,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val user: LoginResponse? = null,
    val errorMsg: String? = null
)
data class RegisterState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val canSubmit: Boolean = true,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

class LoginViewModel(
    private val repository: UserApiRepository = UserApiRepository(),
    private val prefs: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState : StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        // Cargar usuario si ya hay id guardado
        viewModelScope.launch {
            val savedId = prefs.userId.firstOrNull()
            if (savedId != null) {
                loadUserById(savedId)
            }
        }
    }

    fun onEmailChange(value: String) {
        _loginState.update { it.copy(email = value, emailError = null) }
    }

    fun onPassChange(value: String) {
        _loginState.update { it.copy(pass = value, passError = null) }
    }

    fun loginUser() {
        val email = _loginState.value.email
        val pass  = _loginState.value.pass

        if (email.isBlank()) {
            _loginState.update { it.copy(emailError = "Campo obligatorio") }
            return
        }
        if (pass.isBlank()) {
            _loginState.update { it.copy(passError = "Campo obligatorio") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isSubmitting = true, errorMsg = null) }

            val result = repository.login(email, pass) // Result<LoginResponse>

            result.fold(
                onSuccess = { response ->
                    _loginState.update {
                        it.copy(
                            user = response,      // <-- LoginResponse aquí
                            success = true,
                            isSubmitting = false
                        )
                    }
                },
                onFailure = { e ->
                    _loginState.update {
                        it.copy(
                            errorMsg = e.message ?: "Credenciales incorrectas",
                            isSubmitting = false,
                            success = false
                        )
                    }
                }
            )
        }
    }
    fun loadUserById(userId: Long) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }
            val result = repository.getUserById(userId)
            _loginState.update {
                it.copy(
                    user = result,
                    isLoading = false
                )
            }
        }
    }


    fun resetLogin() {
        _loginState.value = LoginState()
    }
    private val _register = MutableStateFlow(RegisterState())
    val register: StateFlow<RegisterState> = _register.asStateFlow()

    fun onNameChange(value: String) {
        _register.update { it.copy(name = value, nameError = null) }
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = null) }
    }

    fun onPhoneChange(value: String) {
        _register.update { it.copy(phone = value, phoneError = null) }
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = null) }
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = null) }
    }

    fun submitRegister() {
        val state = _register.value

        // Validaciones simples
        if (state.name.isBlank()) {
            _register.update { it.copy(nameError = "Nombre obligatorio") }
            return
        }
        if (state.email.isBlank()) {
            _register.update { it.copy(emailError = "Email obligatorio") }
            return
        }
        if (state.pass != state.confirm) {
            _register.update { it.copy(confirmError = "Contraseñas no coinciden") }
            return
        }

        // Simula registro exitoso
        _register.update { it.copy(isSubmitting = true, errorMsg = null) }

        viewModelScope.launch {
            delay(1000) // simula llamada a API
            _register.update { it.copy(isSubmitting = false, success = true) }
        }
    }

    fun clearRegisterResult() {
        _register.value = RegisterState()
    }

}
