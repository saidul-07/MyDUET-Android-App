package com.example.myduet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myduet.models.AdmissionResult
import com.example.myduet.models.SeatPlan
import com.example.myduet.repositories.AdmissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class AdmissionViewModel(private val repository: AdmissionRepository) : ViewModel() {

    private val _seatPlanState = MutableStateFlow<UiState<SeatPlan>>(UiState.Idle)
    val seatPlanState: StateFlow<UiState<SeatPlan>> = _seatPlanState.asStateFlow()

    private val _admissionResultState = MutableStateFlow<UiState<AdmissionResult>>(UiState.Idle)
    val admissionResultState: StateFlow<UiState<AdmissionResult>> = _admissionResultState.asStateFlow()

    fun fetchSeatPlan(roll: String) {
        if (roll.isBlank()) {
            _seatPlanState.value = UiState.Error("Invalid roll number")
            return
        }
        viewModelScope.launch {
            _seatPlanState.value = UiState.Loading
            try {
                val result = repository.getSeatPlan(roll)
                _seatPlanState.value = UiState.Success(result)
            } catch (e: Exception) {
                _seatPlanState.value = UiState.Error(e.localizedMessage ?: "Server error occurred")
            }
        }
    }

    fun fetchAdmissionResult(roll: String) {
        if (roll.isBlank()) {
            _admissionResultState.value = UiState.Error("Invalid roll number")
            return
        }
        viewModelScope.launch {
            _admissionResultState.value = UiState.Loading
            try {
                val result = repository.getAdmissionResult(roll)
                _admissionResultState.value = UiState.Success(result)
            } catch (e: Exception) {
                _admissionResultState.value = UiState.Error(e.localizedMessage ?: "Server error occurred")
            }
        }
    }
}