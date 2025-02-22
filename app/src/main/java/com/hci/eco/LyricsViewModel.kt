package com.hci.eco

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LyricsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _images: MutableStateFlow<List<ImageMetaData>> = MutableStateFlow(emptyList())
    val images: StateFlow<List<ImageMetaData>> = _images.asStateFlow()

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(application, AppDatabase::class.java, "image_metadata_database").build()
    }
    private val imageMetadataDao: ImageMetaDataDao by lazy { database.imageMetaDataDao() }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-exp",
        apiKey = BuildConfig.apiKey
    )

    init {
        fetchImages()
    }

    private fun fetchImages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageList = imageMetadataDao.getAllImages()
                _images.value = imageList
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error fetching images")
            }
        }
    }

    fun sendPrompt(bitmap: Bitmap, prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}