package com.example.myduet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myduet.repositories.AdmissionRepository

class AdmissionViewModelFactory(private val repository: AdmissionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdmissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdmissionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}