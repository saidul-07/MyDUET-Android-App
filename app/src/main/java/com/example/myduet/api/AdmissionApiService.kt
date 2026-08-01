package com.example.myduet.api

import com.example.myduet.models.AdmissionResult
import com.example.myduet.models.SeatPlan
import retrofit2.http.GET
import retrofit2.http.Path

interface AdmissionApiService {
    @GET("api/admission/seat-plan/{roll}")
    suspend fun getSeatPlan(@Path("roll") roll: String): SeatPlan

    @GET("api/admission/result/{roll}")
    suspend fun getAdmissionResult(@Path("roll") roll: String): AdmissionResult
}