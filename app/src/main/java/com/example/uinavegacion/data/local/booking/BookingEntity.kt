package com.example.uinavegacion.data.local.booking

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,     //id del usuario que reserva
    val fieldId: Int,       //id de la cancha reservada
    val bookingDate: String,//fecha de la reserva
    val startTime: String,    //hora de inicio de la reserva
    val status: String      //estado de la reserva (pendiente, aceptada, rechazada, etc.)
)


