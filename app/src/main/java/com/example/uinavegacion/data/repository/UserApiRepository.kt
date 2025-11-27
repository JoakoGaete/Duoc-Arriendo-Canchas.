package com.example.uinavegacion.data.repository

// Importamos la interfaz de la API y el DTO de datos.
import com.example.uinavegacion.data.remote.UsuariosApi
import com.example.uinavegacion.data.remote.RemoteModuleUsuarios
import com.example.uinavegacion.data.remote.dto.LoginRequest
import com.example.uinavegacion.data.remote.dto.LoginResponse
import com.example.uinavegacion.data.remote.dto.UserRequestDto
import com.example.uinavegacion.data.remote.dto.UserResponseDto
import com.example.uinavegacion.data.remote.dto.UsuariosDto
import retrofit2.HttpException

class UserApiRepository(
    private val api: UsuariosApi = RemoteModuleUsuarios.create(UsuariosApi::class.java)
) {
    // Obtiene todos los posts desde la API.
    suspend fun fetchUsuarios(): Result<List<UsuariosDto>> = try {
        Result.success(api.getUsuarios())
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Obtiene un post específico por su ID.
    suspend fun fetchuUsuarioById(id: Int): Result<UsuariosDto> = try {
        Result.success(api.getUsuarioById(id))
    } catch (e: Exception) {
        Result.failure(e)
    }
    suspend fun getUserById(userId: Long): LoginResponse {
        return api.getUserById(userId) // Aquí llama al endpoint de tu API GET /users/{id}
    }

    // Crea un nuevo post.
    suspend fun registerUser(dto: UserRequestDto): Result<UserResponseDto> = try {
        Result.success(api.createUsuario(dto))
    } catch (e: Exception) {
        Result.failure(e)
    }


    // Actualiza un post existente.

    suspend fun update(id: Int, usuario: UsuariosDto): Result<UsuariosDto> = try {
        Result.success(api.updateUsuario(id, usuario))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Elimina un post por su ID.
    suspend fun delete(id: Int): Result<Unit> = try {
        val resp = api.deleteUsuario(id)
        if (resp.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(resp))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val req = LoginRequest(email = email, password = password)
            val res = api.login(req)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}