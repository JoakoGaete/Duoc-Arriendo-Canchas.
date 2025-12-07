package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.remote.dto.CanchasDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface CanchasApi {
    @GET("api/fields")
    suspend fun getCanchas(): List<CanchasDto>

    @GET("api/fields/{id}")
    suspend fun getCanchaById(@Path("id") id: Int): CanchasDto


    @Multipart
    @POST("api/fields/create")
    suspend fun createCancha(
        @Part("cancha") cancha: CanchasDto,
        @Part image: MultipartBody.Part?
    ): CanchasDto

    @PUT("api/fields/{id}")
    suspend fun updateCancha(
        @Path("id") id: Int,
        @Body cancha: CanchasDto
    ): CanchasDto

    @DELETE("api/fields/{id}")
    suspend fun deleteCancha(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/fields/{id}/imagen")
    suspend fun uploadFieldImage(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Response<String>
}
