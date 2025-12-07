package com.example.uinavegacion.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
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
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AdminViewModel(
    application: Application,
    private val bookingRepository: BookingApiRepository,
    private val fieldRepository: CanchasApiRepository
) : AndroidViewModel(application) {

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
        imageUri: Uri?,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val cancha = CanchasDto(
                    id = null,
                    name = name,
                    type = type,
                    location = location,
                    pricePerHour = pricePerHour,
                    imageUrl = null
                )

                // Llamamos al repositorio nuevo que maneja Uri + Context
                val created = fieldRepository.create(cancha, imageUri, context).getOrThrow()

                // Refrescar lista
                loadFields()
                _status.value = "Cancha creada con éxito"

            } catch (e: Exception) {
                _status.value = "Error: ${e.message}"
            }
        }
    }



    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "upload_image.jpg")
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun clearStatus() {
        _status.value = null
    }
}

