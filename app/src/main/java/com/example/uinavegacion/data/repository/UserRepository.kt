package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.booking.BookingDao
import com.example.uinavegacion.data.local.booking.BookingEntity
import com.example.uinavegacion.data.local.field.FieldDao
import com.example.uinavegacion.data.local.field.FieldEntity
import com.example.uinavegacion.data.local.user.UserDao       // DAO de usuario
import com.example.uinavegacion.data.local.user.UserEntity    // Entidad de usuario

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao,
    private val bookingDao: BookingDao,
    private val fieldDao: FieldDao
) {

    // --- USUARIOS ---
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) return Result.failure(IllegalStateException("El correo ya está registrado"))

        val id = userDao.insert(
            UserEntity(name = name, email = email, phone = phone, password = password)
        )
        return Result.success(id)
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao.getById(id)

    // --- RESERVAS ---
    suspend fun insertBooking(booking: BookingEntity) {
        val existing = bookingDao.getBookingsForFieldOnDate(booking.fieldId, booking.bookingDate)

        val requestedMinutes = booking.startTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }

        val conflict = existing.any {
            val existingMinutes = it.startTime.split(":").let { t -> t[0].toInt() * 60 + t[1].toInt() }
            kotlin.math.abs(existingMinutes - requestedMinutes) < 60
        }

        if (conflict) throw Exception("La cancha ya está reservada cerca de esa hora")

        bookingDao.insert(booking)
    }

    suspend fun getBookingsByUserId(userId: Long): List<BookingEntity> =
        bookingDao.getBookingsByUser(userId)

    suspend fun deleteBooking(bookingId: Long) {
        bookingDao.deleteBookingById(bookingId)
    }

    // --- CANCHAS ---
    suspend fun getAllFields(): List<FieldEntity> = fieldDao.getAllFields()

    suspend fun addField(field: FieldEntity) {
        fieldDao.insertField(field)
    }

    // --- ADMIN: TODAS LAS RESERVAS ---
    suspend fun getAllBookings(): List<BookingEntity> = bookingDao.getAllBookings()
}
