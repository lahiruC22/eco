package com.hci.eco.ui.dashboard

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.camera.video.VideoRecordEvent

class DashboardViewModel : ViewModel() {

    // LiveData to hold the current recording state
    private val _recordingState = MutableLiveData<RecordingState>(RecordingState.Idle)
    val recordingState: LiveData<RecordingState> = _recordingState

    // LiveData to hold the last recorded video URI
    private val _lastRecordedVideoUri = MutableLiveData<Uri?>(null)
    val lastRecordedVideoUri: LiveData<Uri?> = _lastRecordedVideoUri

    fun startRecording() {
        _recordingState.value = RecordingState.Recording
    }

    // Function to stop recording
    fun stopRecording() {
        _recordingState.value = RecordingState.Idle
    }

    // Function to handle video record events
    fun onVideoRecordEvent(event: VideoRecordEvent) {
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    // Handle error
                    _recordingState.value = RecordingState.Error(event.error)
                } else {
                    // Video successfully recorded
                    _lastRecordedVideoUri.value = event.outputResults.outputUri
                    _recordingState.value = RecordingState.Idle
                }
            }
            else -> {}
        }
    }

    fun clearLastRecordedVideoUri() {
        _lastRecordedVideoUri.value = null
    }

    // Enum to represent the recording state
    sealed class RecordingState {
        object Idle : RecordingState()
        object Recording : RecordingState()
        data class Error(val error: Int) : RecordingState()
    }
}