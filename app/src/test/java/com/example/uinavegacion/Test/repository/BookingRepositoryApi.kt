package com.example.uinavegacion.Test.repository

import com.example.uinavegacion.data.remote.BookingApi
import com.example.uinavegacion.data.remote.CanchasApi
import com.example.uinavegacion.data.remote.dto.BookingDto
import com.example.uinavegacion.data.remote.dto.CanchasDto
import com.example.uinavegacion.data.repository.BookingApiRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class BookingRepositoryApi {

    @Test
    fun fetchBookings_retorna_lista_valida() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val sample = listOf(
            BookingDto(
                id = 1,
                userId = 10,
                fieldId = 5,
                date = "2025-01-01",
                startTime = "10:00",
                status = "CONFIRMED"
            )
        )

        coEvery { api.getBooking() } returns sample

        val result = repo.fetchBookings()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals(10L, result.getOrNull()!![0].userId)
    }


    @Test
    fun fetchBookingById_retorna_objeto_valido() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val sample = BookingDto(
            id = 2,
            userId = 15,
            fieldId = 3,
            date = "2025-02-02",
            startTime = "12:00",
            status = "CONFIRMED"
        )

        coEvery { api.getBookingById(2) } returns sample

        val result = repo.fetchBookingById(2)

        assertTrue(result.isSuccess)
        assertEquals(2L, result.getOrNull()!!.id)
    }


    @Test
    fun createBooking_retorna_creado() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val input = BookingDto(
            id = null,
            userId = 5,
            fieldId = 2,
            date = "2025-03-10",
            startTime = "18:00",
            status = "CONFIRMED"
        )

        val returned = input.copy(id = 99)

        coEvery { api.createBooking(input) } returns returned

        val result = repo.create(input)

        assertTrue(result.isSuccess)
        assertEquals(99L, result.getOrNull()!!.id)
    }


    @Test
    fun updateBooking_retorna_actualizado() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val booking = BookingDto(
            id = 1,
            userId = 10,
            fieldId = 5,
            date = "2025-01-01",
            startTime = "10:00",
            status = "UPDATED"
        )

        coEvery { api.updateBooking(1, booking) } returns booking

        val result = repo.update(1, booking)

        assertTrue(result.isSuccess)
        assertEquals("UPDATED", result.getOrNull()!!.status)
    }







    @Test
    fun fetchBookingsByUser_retorna_lista_valida() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val sample = listOf(
            BookingDto(
                id = 3,
                userId = 99,
                fieldId = 1,
                date = "2025-04-01",
                startTime = "14:00",
                status = "CONFIRMED"
            )
        )

        coEvery { api.getBookingsByUser(99) } returns sample

        val result = repo.fetchBookingsByUser(99)

        assertTrue(result.isSuccess)
        assertEquals(99, result.getOrNull()!![0].userId)
    }


    @Test
    fun fetchCanchaById_retorna_valido() = runBlocking {
        val api = mockk<BookingApi>()
        val canchasApi = mockk<CanchasApi>()
        val repo = BookingApiRepository(api, canchasApi)

        val cancha = CanchasDto(
            id = 10L,
            name = "Cancha Futbol 5",
            type = "FUTBOL 11",
            location = "Duoc Plaza Norte",
            pricePerHour = 20000.00,
            imageUrl = "cancha1.jpg"
        )

        coEvery { canchasApi.getCanchaById(10) } returns cancha

        val result = repo.fetchCanchaById(10)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrNull()!!.id)
    }
}

