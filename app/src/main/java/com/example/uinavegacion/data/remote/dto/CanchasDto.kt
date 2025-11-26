package com.example.uinavegacion.data.remote.dto

data class CanchasDto (
    val id: Long,
    val name: String,   //Nombre de la cancha
    val type: String,   //Tipo de cancha (fútbol, futbolito, etc.)
    val location: String,   //Ubicacion de la cancha
    val pricePerHour: Double,   //Precio por hora
    val imageUrl: String
)