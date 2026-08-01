package com.example.myduet.utils

import com.example.myduet.api.AdmissionApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://duet.ac.bd/" // Replace with actual base URL if different

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val admissionApiService: AdmissionApiService by lazy {
        retrofit.create(AdmissionApiService::class.java)
    }
}