package com.example.uinavegacion.data.remote.dto

data class UserResponseDto (
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val isAdmin: Boolean
)