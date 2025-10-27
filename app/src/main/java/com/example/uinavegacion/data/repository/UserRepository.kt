package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.booking.BookingDao
import com.example.uinavegacion.data.local.booking.BookingEntity
import com.example.uinavegacion.data.local.field.FieldDao
import com.example.uinavegacion.data.local.field.FieldEntity
import com.example.uinavegacion.data.local.user.UserDao       // DAO de usuario
import com.example.uinavegacion.data.local.user.UserEntity    // Entidad de usuario

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao, // Inyección del DAO
    private val bookingDao: BookingDao,
    private val fieldDao: FieldDao

) {

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)                         // Busca usuario
        return if (user != null && user.password == password) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error
        }
    }

    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                name = name,
                email = email,
                phone = phone,                                       // Teléfono incluido
                password = password
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }

    suspend fun insertBooking(booking: BookingEntity) {
        // Obtenemos todas las reservas del mismo día y cancha
        val existing = bookingDao.getBookingsForFieldOnDate(booking.fieldId, booking.bookingDate)

        // Convertimos startTime de String a minutos desde la medianoche para comparar
        val requestedMinutes = booking.startTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }

        val conflict = existing.any {
            val existingMinutes = it.startTime.split(":").let { t -> t[0].toInt() * 60 + t[1].toInt() }
            kotlin.math.abs(existingMinutes - requestedMinutes) < 60 // menos de 60 min = conflicto
        }

        if (conflict) {
            throw Exception("La cancha ya está reservada cerca de esa hora")
        }

        bookingDao.insert(booking)
    }

    suspend fun getAllFields(): List<FieldEntity> = fieldDao.getAllFields()



    companion object
}