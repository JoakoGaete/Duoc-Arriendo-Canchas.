package com.example.uinavegacion.data.repository

import android.content.Context
import android.net.Uri
import com.example.uinavegacion.data.remote.CanchasApi
import com.example.uinavegacion.data.remote.RemoteModuleCanchas
import com.example.uinavegacion.data.remote.dto.CanchasDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class CanchasApiRepository( private val api: CanchasApi = RemoteModuleCanchas.create(CanchasApi::class.java)
) {
        // Obtiene todos los posts desde la API.
        suspend fun fetchCanchas(): Result<List<CanchasDto>> = try {
            Result.success(api.getCanchas())
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun createCanchaWithImage(cancha: CanchasDto, image: MultipartBody.Part?): Result<CanchasDto> = try {
        Result.success(api.createCancha(cancha, image))
    } catch (e: Exception) {
        Result.failure(e)
    }


    // Crea un nuevo post.
    suspend fun create(cancha: CanchasDto, imageUri: Uri?, context: Context): Result<CanchasDto> {
        return try {
            // Creamos el multipart de la imagen si existe
            val imagePart = imageUri?.let { uri ->
                val stream = context.contentResolver.openInputStream(uri)
                    ?: throw RuntimeException("No se pudo leer la imagen")
                val bytes = stream.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "imagen_${cancha.id ?: "new"}.jpg",
                    body = requestBody
                )
            }

            // Llamamos a la API pasando directamente el objeto CanchasDto y el multipart opcional
            val created = api.createCancha(cancha, imagePart)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // Actualiza un post existente.

        suspend fun update(id: Int, cancha: CanchasDto): Result<CanchasDto> = try {
            Result.success(api.updateCancha(id, cancha))
        } catch (e: Exception) {
            Result.failure(e)
        }

        // Elimina un post por su ID.
        suspend fun delete(id: Int): Result<Unit> = try {
            val resp = api.deleteCancha(id)
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(resp))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun uploadFieldImage(id: Long, image: MultipartBody.Part): Result<String> = try {
        val response = api.uploadFieldImage(id, image)
        if (response.isSuccessful) {
            Result.success(response.body() ?: "")
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

}