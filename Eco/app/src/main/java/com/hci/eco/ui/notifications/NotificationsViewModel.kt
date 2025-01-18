package com.hci.eco.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private val _geminiResponses = MutableSharedFlow<String>()
    val geminiResponses: SharedFlow<String> = _geminiResponses.asSharedFlow()

    suspend fun addGeminiResponse(response: String) {
        viewModelScope.launch {
            _geminiResponses.emit(response)
        }
    }
}