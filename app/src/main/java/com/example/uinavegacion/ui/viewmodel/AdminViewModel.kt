package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.BookingDto
import com.example.uinavegacion.data.remote.dto.CanchasDto
import com.example.uinavegacion.data.repository.BookingApiRepository
import com.example.uinavegacion.data.repository.CanchasApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val bookingRepository: BookingApiRepository,
    private val fieldRepository: CanchasApiRepository
) : ViewModel() {

    private val _allBookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val allBookings: StateFlow<List<BookingDto>> = _allBookings.asStateFlow()

    private val _allFields = MutableStateFlow<List<CanchasDto>>(emptyList())
    val allFields: StateFlow<List<CanchasDto>> = _allFields.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    fun loadAllBookings() {
        viewModelScope.launch {
            try {
                val result = bookingRepository.fetchBookings()
                _allBookings.value = result.getOrThrow()
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    fun deleteBooking(bookingId: Long) {
        viewModelScope.launch {
            try {
                bookingRepository.delete(bookingId.toInt())
                _allBookings.update { it.filter { b -> b.id != bookingId } }
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }


    fun loadFields() {
        viewModelScope.launch {
            try {
                val result = fieldRepository.fetchCanchas()
                _allFields.value = result.getOrThrow()
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    fun addField(
        name: String,
        type: String,
        location: String,
        pricePerHour: Double,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            try {
                val newField = CanchasDto(
                    id = 0L,
                    name = name,
                    type = type,
                    location = location,
                    pricePerHour = pricePerHour,
                    imageUrl = imageUrl
                )
                fieldRepository.create(newField)
                loadFields()
            } catch (e: Exception) {
                _status.value = e.message
            }
        }
    }

    fun clearStatus() {
        _status.value = null
    }
}

