package com.example.uinavegacion.Test.repository


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.uinavegacion.data.remote.CanchasApi
import com.example.uinavegacion.data.remote.dto.CanchasDto
import com.example.uinavegacion.data.repository.CanchasApiRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException


class CanchasApiRepositoryTest {

    private val api = mockk<CanchasApi>()
    private val repo = CanchasApiRepository(api)

    // -------------------------------------------------------------
    // 1) TEST fetchCanchas()
    // -------------------------------------------------------------
    @Test
    fun fetchCanchas_retorna_lista_valida() = runBlocking {
        val sample = listOf(
            CanchasDto(1, "Cancha 1", "Fútbol", "Santiago", 20000.0, null)
        )

        coEvery { api.getCanchas() } returns sample

        val result = repo.fetchCanchas()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Cancha 1", result.getOrNull()!![0].name)
    }

    @Test
    fun fetchCanchas_retorna_failure() = runBlocking {
        coEvery { api.getCanchas() } throws RuntimeException("Error")

        val result = repo.fetchCanchas()

        assertTrue(result.isFailure)
    }


    @Test
    fun createCanchaWithImage_retorna_ok() = runBlocking {
        val cancha = CanchasDto(null, "Nueva", "Fútbol", "Maipú", 15000.0, null)

        val mockPart = mockk<MultipartBody.Part>()

        coEvery { api.createCancha(cancha, mockPart) } returns cancha.copy(id = 10)

        val result = repo.createCanchaWithImage(cancha, mockPart)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrNull()!!.id)
    }

    @Test
    fun createCanchaWithImage_retorna_failure() = runBlocking {
        val cancha = CanchasDto(null, "Nueva", "Fútbol", "Maipú", 15000.0, null)

        coEvery { api.createCancha(cancha, null) } throws RuntimeException("Error")

        val result = repo.createCanchaWithImage(cancha, null)

        assertTrue(result.isFailure)
    }


    @Test
    fun create_con_imagen_retorna_ok() = runBlocking {
        val cancha = CanchasDto(null, "C1", "Fútbol", "Santiago", 25000.0, null)
        val uri = mockk<Uri>()

        // Mock Context + ContentResolver
        val context = mockk<Context>()
        val resolver = mockk<ContentResolver>()
        every { context.contentResolver } returns resolver

        // Fake imagen bytes
        val fakeBytes = "fakeimage".toByteArray()
        val inputStream = fakeBytes.inputStream()

        every { resolver.openInputStream(uri) } returns inputStream

        // El multipart esperado
        val expectedPart = slot<MultipartBody.Part>()

        coEvery { api.createCancha(eq(cancha), capture(expectedPart)) } returns cancha.copy(id = 77)

        val result = repo.create(cancha, uri, context)

        assertTrue(result.isSuccess)
        assertEquals(77L, result.getOrNull()!!.id)
    }

    @Test
    fun create_con_imagen_falla_por_context() = runBlocking {
        val cancha = CanchasDto(null, "C1", "Fútbol", "Santiago", 25000.0, null)
        val uri = mockk<Uri>()
        val context = mockk<Context>()
        val resolver = mockk<ContentResolver>()

        every { context.contentResolver } returns resolver
        every { resolver.openInputStream(uri) } returns null  // falla lectura

        val result = repo.create(cancha, uri, context)

        assertTrue(result.isFailure)
    }


    @Test
    fun update_retorna_ok() = runBlocking {
        val cancha = CanchasDto(1, "X", "Fútbol", "SCL", 10000.0, null)

        coEvery { api.updateCancha(1, cancha) } returns cancha.copy(name = "Editado")

        val result = repo.update(1, cancha)

        assertTrue(result.isSuccess)
        assertEquals("Editado", result.getOrNull()!!.name)
    }

    @Test
    fun update_retorna_failure() = runBlocking {
        val cancha = CanchasDto(1, "X", "Fútbol", "SCL", 10000.0, null)

        coEvery { api.updateCancha(1, cancha) } throws RuntimeException("Error")

        val result = repo.update(1, cancha)

        assertTrue(result.isFailure)
    }


}
