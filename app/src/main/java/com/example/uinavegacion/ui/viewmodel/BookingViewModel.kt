package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.booking.BookingEntity
import com.example.uinavegacion.data.local.field.FieldEntity
import com.example.uinavegacion.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// booking iu state
data class BookingUiState(
    val userId: Long? = null,           // ID del usuario que está reservando
    val fieldId: Int? = null,           // Cancha seleccionada
    val bookingDate: String = "",       // Fecha (ej: "2025-11-01")
    val startTime: String = "",         // Hora (ej: "18:00")
    val status: String = "pendiente",   // Estado de la reserva

    val fieldError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,

    val isSubmitting: Boolean = false,  // Mientras se guarda la reserva
    val canSubmit: Boolean = false,     // Si todos los campos están llenos
    val success: Boolean = false,       // Si la reserva fue creada
    val errorMsg: String? = null        // Mensaje de error global
)
class BookingViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // Estado de la UI de Booking
    private val _booking = MutableStateFlow(BookingUiState())
    val booking: StateFlow<BookingUiState> = _booking

    // Lista de canchas
    private val _fields = MutableStateFlow<List<FieldEntity>>(emptyList())
    val fields: StateFlow<List<FieldEntity>> = _fields

    init {
        loadFields()
    }

    // Carga las canchas desde la DB
    private fun loadFields() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getAllFields()
            }
            _fields.value = result
        }
    }

    // Handlers para UI
    fun onFieldSelected(fieldId: Int) {
        _booking.update { it.copy(fieldId = fieldId, fieldError = null) }
        recomputeCanSubmit()
    }

    fun onDateSelected(date: String) {
        _booking.update { it.copy(bookingDate = date) }
        recomputeCanSubmit()
    }

    fun onTimeSelected(time: String) {
        _booking.update { it.copy(startTime = time) }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val state = _booking.value
        val can = state.fieldId != null &&
                state.bookingDate.isNotBlank() &&
                state.startTime.isNotBlank()
        _booking.update { it.copy(canSubmit = can) }
    }

    // Inserta reserva en DB en background
    fun submitBooking(userId: Long) {
        val state = _booking.value
        if (!state.canSubmit || state.isSubmitting) return

        viewModelScope.launch {
            _booking.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                val booking = BookingEntity(
                    userId = userId,
                    fieldId = state.fieldId!!,
                    bookingDate = state.bookingDate,
                    startTime = state.startTime,
                    status = "pendiente"
                )

                withContext(Dispatchers.IO) {
                    repository.insertBooking(booking)
                }

                _booking.update { it.copy(isSubmitting = false, success = true) }
            } catch (e: Exception) {
                _booking.update { it.copy(isSubmitting = false, errorMsg = e.message) }
            }
        }
    }

    // Limpia resultado después de reservar
    fun clearBookingResult() {
        _booking.update { it.copy(success = false, errorMsg = null) }
    }
}
