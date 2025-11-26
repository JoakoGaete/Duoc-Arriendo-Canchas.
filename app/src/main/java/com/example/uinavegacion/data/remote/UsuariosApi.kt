package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.dto.LoginRequest
import com.example.uinavegacion.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import com.example.uinavegacion.data.remote.dto.UsuariosDto

interface UsuariosApi {

    @GET("users")
    suspend fun getUsuarios(): List<UsuariosDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): LoginResponse

    @GET("users/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): UsuariosDto

    @POST("users")
    suspend fun createUsuario(@Body usuario: UsuariosDto): UsuariosDto

    @PUT("users/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Int,
        @Body usuario: UsuariosDto
    ): UsuariosDto

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @DELETE("users/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): Response<Unit>
}




