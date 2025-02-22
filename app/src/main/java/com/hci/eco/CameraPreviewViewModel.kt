package com.hci.eco

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraPreviewViewModel(
    private val application: Application
) : ViewModel() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private val _galleryImageUri = MutableStateFlow<Uri?>(null)

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    private val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(application)
    }

    private val imageMetadataDao: ImageMetaDataDao by lazy { database.imageMetaDataDao() }

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        try {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
            processCameraProvider.bindToLifecycle(
                lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase, imageCapture
            )

            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        } catch (e: Exception) {
            Log.e("CameraPreviewViewModel", "Error binding to camera", e)
        }
    }

    fun captureImage() {
        try {
            val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
                .format(System.currentTimeMillis())
            val imageDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            if (imageDir == null) {
                showToast("Error accessing app's picture directory")
                return
            }

            val imageFile = File(imageDir, "IMG_${timeStamp}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(imageFile)
                        _galleryImageUri.value = savedUri
                        saveImageMetadata(savedUri, timeStamp)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        showToast("Error saving image: ${exception.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("CameraPreviewViewModel", "Error capturing image", e)
        }
    }

    private fun saveImageMetadata(savedUri: Uri, timeStamp: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val imageMetaData = ImageMetaData(
                        imageUri = savedUri.toString(),
                        timeStamp = timeStamp
                    )
                    imageMetadataDao.insert(imageMetaData)
                    withContext (Dispatchers.Main  ) {
                        showToast("Image saved and metadata saved", Toast.LENGTH_SHORT)
                    }
                } catch (e: Exception) {
                    Log.e("CameraPreviewViewModel", "Error saving image metadata", e)
                    withContext (Dispatchers.Main) {
                        showToast("Error saving image metadata: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CameraPreviewViewModel", "Error saving image metadata", e)
        }
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(application, message, duration).show()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}