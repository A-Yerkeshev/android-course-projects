package com.example.eduskunta

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 27.09.2024 by Arman Yerkeshev 2214297
// This is a singleton class, responsible for sending and receiving HTTP requests
object NetworkAPI {
    private const val BASE_URL = "https://users.metropolia.fi/~armany/Kotlin/"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}
