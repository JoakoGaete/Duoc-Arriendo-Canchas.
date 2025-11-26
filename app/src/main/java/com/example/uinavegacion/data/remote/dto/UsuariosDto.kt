package com.example.uinavegacion.data.remote.dto

data class UsuariosDto (

    val id: Long? = null,
    val name: String,                   // Nombre completo del usuario
    val email: String,                  // Correo (idealmente único a nivel de negocio)
    val phone: String,                  // Teléfono del usuario (⚠️ agregado)
    val password: String ,               // Contraseña (para demo; en prod usar hash)
    val isAdmin : Boolean = false
)



