package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.BookingDto
import com.example.uinavegacion.data.repository.BookingApiRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import kotlin.Long

data class BookingUiState(
    val fieldId: Long? = null,
    val bookingDate: String? = null,
    val startTime: String = "",
    val endTime: String = "",
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val canSubmit: Boolean = false
)

class BookingViewModel(
    private val repository: BookingApiRepository = BookingApiRepository()
) : ViewModel() {

    var uiState by mutableStateOf(BookingUiState())
        private set

    // --- Seleccionar cancha ---
    fun onFieldSelected(id: Long) {
        uiState = uiState.copy(fieldId = id)
        validate()
    }

    // --- Seleccionar fecha ---
    fun onDateSelected(date: LocalDate) {
        uiState = uiState.copy(bookingDate = date.toString())
        validate()
    }

    // --- Seleccionar hora ---
    fun onTimeSelected(start: String) {
        uiState = uiState.copy(startTime = start)
        validate()
    }

    // --- Validar formulario ---
    private fun validate() {
        val ok =
            uiState.fieldId != null &&
                    uiState.bookingDate != null &&
                    uiState.startTime.isNotBlank()

        uiState = uiState.copy(canSubmit = ok)
    }

    // --- Enviar reserva ---
    fun submitBooking(userId: Long, onSuccess: () -> Unit) {
        if (!uiState.canSubmit) return
        if (uiState.bookingDate == null) {
            uiState = uiState.copy(errorMsg = "Debes seleccionar una fecha")
            return
        }

        uiState = uiState.copy(isSubmitting = true)



        viewModelScope.launch {
            val dto = BookingDto(
                id = null,
                userId = userId,
                fieldId = uiState.fieldId!!.toInt(),
                date = uiState.bookingDate!!.toString(),
                startTime = uiState.startTime,
                status = "pendiente"
            )

            val result = repository.create(dto)

            uiState = result.fold(
                onSuccess = {
                    uiState.copy(isSubmitting = false, success = true)
                        .also { onSuccess() }
                },
                onFailure = { e ->
                    uiState.copy(isSubmitting = false, errorMsg = e.message ?: "Error inesperado")
                }
            )
        }
    }



    // --- Limpiar resultado ---
    fun clearResult() {
        uiState = uiState.copy(success = false)
    }

    }






