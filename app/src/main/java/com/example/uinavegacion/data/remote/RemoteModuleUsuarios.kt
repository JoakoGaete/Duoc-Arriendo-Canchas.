package com.example.uinavegacion.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteModuleUsuarios {
    // Línea 7: base URL del microservicio de usuarios
    private const val BASE_URL = "http://10.0.2.2:8085/"

    // Línea 9: creamos un interceptor de logging para depurar tráfico HTTP
    private val logging = HttpLoggingInterceptor().apply {
        // Línea 11: nivel BODY muestra todo (headers + cuerpo)a
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Línea 14: construimos el cliente OkHttp con el interceptor
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging) // Línea 16: agregamos logging
        .build()

    // Línea 19: construimos Retrofit indicando baseURL y convertidor JSON
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Línea 21: host del servicio
        .client(okHttp) // Línea 22: cliente con logging
        .addConverterFactory(GsonConverterFactory.create()) // Línea 23: usa Gson para JSON
        .build()

    // Línea 26: función para crear una implementación de la interfaz API
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}