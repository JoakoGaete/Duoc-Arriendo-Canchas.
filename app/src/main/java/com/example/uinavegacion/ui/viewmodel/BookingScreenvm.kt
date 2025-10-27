package com.example.uinavegacion.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.ui.screen.BookingScreen


@Composable
fun BookingScreenVm(
    vm: BookingViewModel,
    userId: Long,
    onBookingSuccess: () -> Unit
) {
    val state by vm.booking.collectAsStateWithLifecycle()

    // Cuando la reserva se crea exitosamente
    if (state.success) {
        vm.clearBookingResult()
        onBookingSuccess()
    }

    BookingScreen(
        vm = vm,
        state = state,
        onSubmit = { vm.submitBooking(userId) }
    )
}

