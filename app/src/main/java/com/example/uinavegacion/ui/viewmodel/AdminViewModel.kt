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

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _allBookings = MutableStateFlow<List<BookingEntity>>(emptyList())
    val allBookings: StateFlow<List<BookingEntity>> = _allBookings.asStateFlow()

    private val _allFields = MutableStateFlow<List<FieldEntity>>(emptyList())
    val allFields: StateFlow<List<FieldEntity>> = _allFields.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    // Cargar todas las reservas
    fun loadAllBookings() {
        viewModelScope.launch {
            _allBookings.value = withContext(Dispatchers.IO) {
                userRepository.getAllBookings()
            }
        }
    }

    // Eliminar reserva
    fun deleteBooking(bookingId: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userRepository.deleteBooking(bookingId)
                }
                // Actualiza la lista localmente para que desaparezca inmediatamente
                _allBookings.update { it.filter { b -> b.id != bookingId } }
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    // Cargar canchas existentes
    fun loadFields() {
        viewModelScope.launch {
            _allFields.value = withContext(Dispatchers.IO) {
                userRepository.getAllFields()
            }
        }
    }

    // Agregar nueva cancha
    fun addField(name: String, type: String,
                 location: String,
                 pricePerHour: Double,
                 imageUrl: String = "") {
        viewModelScope.launch {
            try {
                val newField = FieldEntity(name = name,type = type,
                    location = location,
                    pricePerHour = pricePerHour,
                    imageUrl = imageUrl)
                withContext(Dispatchers.IO) {
                    userRepository.addField(newField)
                }
                loadFields()
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }
}
