package com.example.uinavegacion.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uinavegacion.data.repository.BookingApiRepository
import com.example.uinavegacion.data.repository.CanchasApiRepository

class AdminViewModelFactory(
    private val application: Application,
    private val bookingRepository: BookingApiRepository,
    private val fieldRepository: CanchasApiRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(application,bookingRepository, fieldRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

