package com.example.uinavegacion.data.remote.dto

import java.sql.Date

data class BookingDto (
    val id: Long? = null,
    val userId: Long,     //id del usuario que reserva
    val fieldId: Int,       //id de la cancha reservada
    val date: String,//fecha de la reserva
    val startTime: String,    //hora de inicio de la reserva
    val status: String
)
