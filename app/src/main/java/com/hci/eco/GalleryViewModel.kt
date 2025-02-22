package com.hci.eco

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : ViewModel() {

    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val imageMetadataDao: ImageMetaDataDao = database.imageMetaDataDao()

    private val _images = MutableStateFlow<List<ImageMetaData>>(emptyList())
    val images: StateFlow<List<ImageMetaData>> = _images

    init {
        fetchImages()
    }

    private fun fetchImages() {
        viewModelScope.launch {
            try {
                val imageList = imageMetadataDao.getAllImages()
                _images.value = imageList
                Log.d("GalleryViewModel", "Fetched ${imageList.size} images")
            } catch (e: Exception) {
                Log.d("GalleryViewModel", "Error fetching images: ${e.message}")
            }
        }
    }
}