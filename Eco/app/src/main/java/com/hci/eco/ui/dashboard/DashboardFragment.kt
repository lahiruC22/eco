package com.hci.eco.ui.dashboard

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.hci.eco.R
import androidx.camera.view.PreviewView
import com.hci.eco.ui.dashboard.DashboardViewModel.RecordingState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var previewView: PreviewView
    private lateinit var recordButton: Button
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private var countDownTimer: CountDownTimer? = null
    private val requestCodePermissions = 10
    private val requiredPermissions =
        mutableListOf(
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
        
    // ActivityResultLauncher for requesting permissions
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
    private val maxRecordingTimeMs = 20000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        previewView = view.findViewById(R.id.previewView)
        recordButton = view.findViewById(R.id.recordButton)
        cameraExecutor = Executors.newSingleThreadExecutor()

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        if (!allPermissionsGranted()) {
            requestPermissions()
        } else {
            startCamera()
        }

        recordButton.setOnClickListener {
            captureVideo()
        }

        dashboardViewModel.recordingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RecordingState.Idle -> {
                    recordButton.isEnabled = true
                    recordButton.text = getString(R.string.start_recording)
                }
                is RecordingState.Recording -> {
                    recordButton.text = getString(R.string.stop_recording)
                    startTimer()
                }
                is RecordingState.Error -> {
                    Toast.makeText(requireContext(), getString(R.string.recording_error, state.error), Toast.LENGTH_SHORT).show()
                    recordButton.isEnabled = true
                    recordButton.text = getString(R.string.start_recording)
                }
            }
        }

        dashboardViewModel.lastRecordedVideoUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Toast.makeText(requireContext(), getString(R.string.video_saved, uri.toString()), Toast.LENGTH_SHORT).show()
                dashboardViewModel.clearLastRecordedVideoUri()
            }
        }

        return view
    }

    private fun requestPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it, requiredPermissions, requestCodePermissions
            )
        }
        requestPermissionLauncher.launch(requiredPermissions)
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        activity?.let { it1 -> ContextCompat.checkSelfPermission(it1.baseContext, it) } == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        recordButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording
            curRecording.stop()
            recording = null
            countDownTimer?.cancel()
            dashboardViewModel.stopRecording()
            return
        }

        // Start a new recording
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(requireContext(), mediaStoreOutputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                dashboardViewModel.onVideoRecordEvent(recordEvent)
            }
        dashboardViewModel.startRecording()
    }
    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(maxRecordingTimeMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                recordButton.text = getString(R.string.stop_recording_with_time, secondsRemaining)
            }

            override fun onFinish() {
                // Stop recording when the timer finishes
                recording?.stop()
                recording = null
                dashboardViewModel.stopRecording()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
