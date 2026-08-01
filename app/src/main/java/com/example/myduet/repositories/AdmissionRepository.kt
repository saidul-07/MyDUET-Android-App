package com.example.myduet.repositories

import com.example.myduet.api.AdmissionApiService
import com.example.myduet.models.AdmissionResult
import com.example.myduet.models.SeatPlan

class AdmissionRepository(private val apiService: AdmissionApiService) {
    suspend fun getSeatPlan(roll: String): SeatPlan {
        return apiService.getSeatPlan(roll)
    }

    suspend fun getAdmissionResult(roll: String): AdmissionResult {
        return apiService.getAdmissionResult(roll)
    }
}