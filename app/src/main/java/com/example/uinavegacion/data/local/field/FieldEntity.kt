package com.example.uinavegacion.data.local.field

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,   //Nombre de la cancha
    val type: String,   //Tipo de cancha (fútbol, futbolito, etc.)
    val location: String,   //Ubicacion de la cancha
    val pricePerHour: Double,   //Precio por hora
    val imageUrl: String
)