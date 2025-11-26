package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.dto.BookingDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingApi {
    @GET("api/bookings")
    suspend fun getBooking(): List<BookingDto>

    @GET("api/bookings/{id}")
    suspend fun getBookingById(@Path("id") id: Int): BookingDto


    @POST("api/bookings")
    suspend fun createBooking(@Body booking: BookingDto): BookingDto


    @PUT("api/bookings/{id}")
    suspend fun updateBooking(
        @Path("id") id: Int,
        @Body booking: BookingDto
    ): BookingDto


    @DELETE("api/bookings/{id}/cancel")
    suspend fun deleteBooking(@Path("id") id: Int): Response<Unit>

    @GET("api/bookings/user/{userId}")
    suspend fun getBookingsByUser(@Path("userId") userId: Long): List<BookingDto>
}
