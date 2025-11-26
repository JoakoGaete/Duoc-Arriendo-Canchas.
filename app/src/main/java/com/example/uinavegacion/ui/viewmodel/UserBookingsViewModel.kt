package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.dto.BookingDto
import com.example.uinavegacion.data.repository.BookingApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserBookingsState(
    val isLoading: Boolean = false,
    val bookings: List<BookingDto> = emptyList(),
    val error: String? = null
)

class UserBookingsViewModel(
    private val repository: BookingApiRepository = BookingApiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserBookingsState())
    val uiState = _uiState.asStateFlow()

    fun loadBookingsByUser(userId: Long) {
        _uiState.value = UserBookingsState(isLoading = true)

        viewModelScope.launch {
            val result = repository.fetchBookingsByUser(userId)

            _uiState.value = result.fold(
                onSuccess = { UserBookingsState(bookings = it) },
                onFailure = { e -> UserBookingsState(error = e.message) }
            )
        }
    }

    fun deleteBooking(id: Long) {
        viewModelScope.launch {
            repository.delete(id.toInt())
            val old = _uiState.value.bookings
            _uiState.value = _uiState.value.copy(
                bookings = old.filter { it.id != id }
            )
        }
    }
}
