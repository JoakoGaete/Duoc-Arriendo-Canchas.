package com.example.uinavegacion.data.local.booking

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    // Inserta una reserva
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(booking: BookingEntity)



    // Obtiene todas las reservas para una cancha en una fecha
    @Query("SELECT * FROM bookings WHERE fieldId = :fieldId AND bookingDate = :date")
    fun getBookingsForFieldOnDate(fieldId: Int, date: String):List<BookingEntity>
    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY bookingDate DESC")
    suspend fun getBookingsByUser(userId: Long): List<BookingEntity>
    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBookingById(bookingId: Long)
}
