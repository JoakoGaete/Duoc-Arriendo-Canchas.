package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.remote.BookingApi
import com.example.uinavegacion.data.remote.CanchasApi
import com.example.uinavegacion.data.remote.RemoteModuleBooking
import com.example.uinavegacion.data.remote.dto.BookingDto
import com.example.uinavegacion.data.remote.dto.CanchasDto
import retrofit2.HttpException

class BookingApiRepository (private val api: BookingApi = RemoteModuleBooking.create(BookingApi::class.java),
        private val canchasApi: CanchasApi = RemoteModuleBooking.create(CanchasApi::class.java)
){
    suspend fun fetchBookings(): Result<List<BookingDto>> = try {
        Result.success(api.getBooking())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un booking específico por su ID.
    suspend fun fetchBookingById(id: Int): Result<BookingDto> = try {
        Result.success(api.getBookingById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Crea una nueva agenda.
    suspend fun create(booking: BookingDto): Result<BookingDto> = try {
        Result.success(api.createBooking(booking))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Actualiza una cita existente.
    suspend fun update(id: Int, booking: BookingDto): Result<BookingDto> = try {
        Result.success(api.updateBooking(id, booking))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina una cita por su ID.
    suspend fun delete(id: Int): Result<Unit> = try {
        val resp = api.deleteBooking(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun fetchCanchaById(id: Int): Result<CanchasDto> = try {
        Result.success(canchasApi.getCanchaById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun fetchBookingsByUser(userId: Long): Result<List<BookingDto>> = try {
        Result.success(api.getBookingsByUser(userId))
    } catch (e: Exception) {
        Result.failure(e)
    }

}