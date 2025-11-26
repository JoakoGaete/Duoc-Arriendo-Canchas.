package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.remote.CanchasApi
import com.example.uinavegacion.data.remote.RemoteModuleCanchas
import com.example.uinavegacion.data.remote.dto.CanchasDto
import retrofit2.HttpException

class CanchasApiRepository( private val api: CanchasApi = RemoteModuleCanchas.create(CanchasApi::class.java)
) {
        // Obtiene todos los posts desde la API.
        suspend fun fetchCanchas(): Result<List<CanchasDto>> = try {
            Result.success(api.getCanchas())
        } catch (e: Exception) {
            Result.failure(e)
        }

        // Obtiene un post específico por su ID.
        suspend fun fetchCanchasById(id: Int): Result<CanchasDto> = try {
            Result.success(api.getCanchaById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }

        // Crea un nuevo post.
        suspend fun create(cancha: CanchasDto): Result<CanchasDto> = try {
            Result.success(api.createCancha(cancha))
        } catch (e: Exception) {
            Result.failure(e)
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


    }