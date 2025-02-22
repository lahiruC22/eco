package com.hci.eco

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MusicViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}