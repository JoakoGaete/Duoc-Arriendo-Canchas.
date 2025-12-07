package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.Storage.UserPreferences
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.remote.dto.UserRequestDto
import com.example.uinavegacion.data.remote.dto.UsuariosDto
import com.example.uinavegacion.data.repository.UserApiRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException

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
        viewModelScope.launch {
            val savedId = prefs.userId.firstOrNull()
            if (savedId != null && savedId != 0L) {
                try {
                    loadUserById(savedId)
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        // Usuario no encontrado, limpiar credenciales
                        prefs.logout()
                    } else {
                        _loginState.value = _loginState.value.copy(
                            errorMsg = "Error: ${e.message}"
                        )
                    }
                }
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

        if (state.name.length < 4) {
            _register.update { it.copy(nameError = "Debe tener mínimo 4 caracteres") }
            return
        }

        if (!state.email.endsWith("@duocuc.cl")) {
            _register.update { it.copy(emailError = "El email debe ser @duocuc.cl") }
            return
        }

        if (state.phone.isBlank() || !state.phone.matches(Regex("\\+569\\d{8}"))) {
            _register.update { it.copy(phoneError = "Teléfono debe ser +569 seguido de 8 dígitos") }
            return
        }

        if (state.pass.length < 6) {
            _register.update { it.copy(passError = "Contraseña mínimo 6 caracteres") }
            return
        }

        if (!state.pass.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).*$"))) {
            _register.update { it.copy(passError = "Debe incluir una mayúscula y un número") }
            return
        }


        // --- REGISTRO ---
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null) }

            val userDto = UserRequestDto(
                name = state.name,
                email = state.email,
                phone = state.phone,
                password = state.pass
            )

            val result = repository.registerUser(userDto)

            result.fold(
                onSuccess = {
                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = true
                        )
                    }
                },
                onFailure = { e ->
                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = e.message ?: "Error al registrar"
                        )
                    }
                }
            )
        }
    }

    fun clearRegisterResult() {
        _register.value = RegisterState()
    }

}
