package com.example.uinavegacion.data.remote.dto

data class UserRequestDto(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean = false
)