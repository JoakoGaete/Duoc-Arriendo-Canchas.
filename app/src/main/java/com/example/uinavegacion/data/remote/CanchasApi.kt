package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.dto.CanchasDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CanchasApi {
    @GET("api/fields")
    suspend fun getCanchas(): List<CanchasDto>

    @GET("api/fields/{id}")
    suspend fun getCanchaById(@Path("id") id: Int): CanchasDto


    @POST("api/fields")
    suspend fun createCancha(@Body cancha: CanchasDto): CanchasDto

    @PUT("api/fields/{id}")
    suspend fun updateCancha(
        @Path("id") id: Int,
        @Body cancha: CanchasDto
    ): CanchasDto

    @DELETE("api/fields/{id}")
    suspend fun deleteCancha(@Path("id") id: Int): Response<Unit>
}
