package com.example.uinavegacion.Test.repository



import com.example.uinavegacion.data.remote.UsuariosApi
import com.example.uinavegacion.data.remote.dto.LoginRequest
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.remote.dto.UserRequestDto
import com.example.uinavegacion.data.remote.dto.UserResponseDto
import com.example.uinavegacion.data.remote.dto.UsuariosDto
import com.example.uinavegacion.data.repository.UserApiRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class UserApiRepositoryTest {

    private val api = mockk<UsuariosApi>()
    private val repo = UserApiRepository(api)

    // -------------------------------------------------------------
    // 1) fetchUsuarios()
    // -------------------------------------------------------------
    @Test
    fun fetchUsuarios_retorna_lista_valida() = runBlocking {
        val sample = listOf(
            UsuariosDto(1, "Juan", "correo@ejemplo.com", "1234", password = "1234", isAdmin = false)
        )

        coEvery { api.getUsuarios() } returns sample

        val result = repo.fetchUsuarios()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Juan", result.getOrNull()!![0].name)
    }

    @Test
    fun fetchUsuarios_retorna_failure() = runBlocking {
        coEvery { api.getUsuarios() } throws RuntimeException("Error")

        val result = repo.fetchUsuarios()

        assertTrue(result.isFailure)
    }

    // -------------------------------------------------------------
    // 2) fetchUsuarioById()
    // -------------------------------------------------------------
    @Test
    fun fetchUsuarioById_retorna_ok() = runBlocking {
        val usuario = UsuariosDto(2, "Pedro", "pedro@mail.com", "1234", password = "1234", isAdmin = false)

        coEvery { api.getUsuarioById(2) } returns usuario

        val result = repo.fetchuUsuarioById(2)

        assertTrue(result.isSuccess)
        assertEquals("Pedro", result.getOrNull()!!.name)
    }

    @Test
    fun fetchUsuarioById_retorna_failure() = runBlocking {
        coEvery { api.getUsuarioById(2) } throws RuntimeException("Error")

        val result = repo.fetchuUsuarioById(2)

        assertTrue(result.isFailure)
    }

    // -------------------------------------------------------------
    // 3) getUserById()  (NO USA Result)
    // -------------------------------------------------------------
    @Test
    fun getUserById_retorna_ok() = runBlocking {
        val sample = LoginResponse(2, "Pedro", "+5691223456","pedro@mail.com",false)

        coEvery { api.getUserById(2) } returns sample

        val result = repo.getUserById(2)

        assertEquals("Pedro", result.name)
    }


    @Test
    fun registerUser_retorna_ok() = runBlocking {
        val req = UserRequestDto("Juan", "+569123456789", "test@test.cl", "1234",false)
        val resp = UserResponseDto(10, "Juan", "+569123456789", "test@test.cl",false)

        coEvery { api.createUsuario(req) } returns resp

        val result = repo.registerUser(req)

        assertTrue(result.isSuccess)
        assertEquals(10, result.getOrNull()!!.id)
    }

    @Test
    fun registerUser_retorna_failure() = runBlocking {
        val req = UserRequestDto("Juan", "+56123456", "test@test.com", "1234",false)

        coEvery { api.createUsuario(req) } throws RuntimeException("Error")

        val result = repo.registerUser(req)

        assertTrue(result.isFailure)
    }

    // -------------------------------------------------------------
    // 5) update()
    // -------------------------------------------------------------
    @Test
    fun update_retorna_ok() = runBlocking {
        val u = UsuariosDto(3, "Maria", "maria@mail.com", "+569123567", password = "1234", isAdmin = false)

        coEvery { api.updateUsuario(3, u) } returns u.copy(name = "MariaEditada")

        val result = repo.update(3, u)

        assertTrue(result.isSuccess)
        assertEquals("MariaEditada", result.getOrNull()!!.name)
    }

    @Test
    fun update_retorna_failure() = runBlocking {
        val u = UsuariosDto(3, "Maria", "maria@mail.com", "+569123456", password = "1234", isAdmin = false)

        coEvery { api.updateUsuario(3, u) } throws RuntimeException("Error")

        val result = repo.update(3, u)

        assertTrue(result.isFailure)
    }

    // -------------------------------------------------------------
    // 6) delete()
    // -------------------------------------------------------------
    @Test
    fun delete_retorna_ok() = runBlocking {
        val response = mockk<Response<Unit>>()
        every { response.isSuccessful } returns true

        coEvery { api.deleteUsuario(5) } returns response

        val result = repo.delete(5)

        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_retorna_failure() = runBlocking {
        val response = mockk<Response<Unit>>()
        every { response.isSuccessful } returns false

        coEvery { api.deleteUsuario(5) } returns response

        val result = repo.delete(5)

        assertTrue(result.isFailure)
    }


    @Test
    fun login_retorna_ok() = runBlocking {
        val req = LoginRequest("correo@mail.com", "1234")
        val resp = LoginResponse(7, "Carlos", "+561234567", "correo@mail.com",false)

        coEvery { api.login(req) } returns resp

        val result = repo.login("correo@mail.com", "1234")

        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull()!!.id)
    }

    @Test
    fun login_retorna_failure() = runBlocking {
        val req = LoginRequest("correo@mail.com", "1234")

        coEvery { api.login(req) } throws RuntimeException("Error")

        val result = repo.login("correo@mail.com", "1234")

        assertTrue(result.isFailure)
    }
}
