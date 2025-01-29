
package com.example.musicgenerator

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekbar: SeekBar
    private lateinit var playBtn: ImageView
    private lateinit var volumeUpBtn: ImageView
    private lateinit var volumeDownBtn: ImageView
    private lateinit var volumeSeekbar: SeekBar
    private val handler = Handler(Looper.getMainLooper()) // Use main looper for UI updates
    private lateinit var audioManager: AudioManager // Declare AudioManager for volume control

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MediaPlayer and AudioManager
        mediaPlayer = MediaPlayer.create(this, R.raw.freemusic)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager // Initialize AudioManager
        seekbar = findViewById(R.id.seekbar)
        playBtn = findViewById(R.id.play_btn)
        volumeUpBtn = findViewById(R.id.volume_up_btn) // Button for increasing volume
        volumeDownBtn = findViewById(R.id.volume_down_btn) // Button for decreasing volume
        volumeSeekbar = findViewById(R.id.volume_seekbar) // Volume SeekBar

        seekbar.progress = 0
        seekbar.max = mediaPlayer.duration

        // Set up play/pause button
        playBtn.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                // Change the button image to a pause icon
                playBtn.setImageResource(R.drawable.pause_24dp_ea33f7_fill0_wght400_grad0_opsz24)
                startSeekBarUpdate() // Start updating SeekBar progress when media starts playing
            } else {
                mediaPlayer.pause()
                // Change the button image to a play icon
                playBtn.setImageResource(R.drawable.play_arrow_24dp_ea33f7_fill0_wght400_grad0_opsz24)
            }
        }

        // Add seek bar event for song progress
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // If the user changes the SeekBar position, update MediaPlayer position
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up volume control SeekBar
        volumeSeekbar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) // Set max volume based on the device
        volumeSeekbar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) // Initialize with current volume

        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Set the volume to the new position of the SeekBar
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle volume up button click
        volumeUpBtn.setOnClickListener {
            // Increase the volume by one step
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume < maxVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 1, 0)
                volumeSeekbar.progress = currentVolume + 1 // Update the SeekBar progress
            }
        }

        // Handle volume down button click
        volumeDownBtn.setOnClickListener {
            // Decrease the volume by one step
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume > 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 1, 0)
                volumeSeekbar.progress = currentVolume - 1 // Update the SeekBar progress
            }
        }

        // Handle completion of the media
        mediaPlayer.setOnCompletionListener {
            // Reset the play button to the play icon when the media completes
            playBtn.setImageResource(R.drawable.play_arrow_24dp_ea33f7_fill0_wght400_grad0_opsz24)
            seekbar.progress = 0 // Reset the SeekBar
        }

        // Handle errors in MediaPlayer
        mediaPlayer.setOnErrorListener { _, _, _ -> true } // Simple error handling

        // Start updating SeekBar when the MediaPlayer is prepared
        mediaPlayer.setOnPreparedListener {
            seekbar.progress = mediaPlayer.currentPosition // Initialize SeekBar with current position
        }
    }

    // Method to continuously update the SeekBar with the song's current position
    private fun startSeekBarUpdate() {
        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekbar.progress = mediaPlayer.currentPosition // Update SeekBar progress
                    handler.postDelayed(this, 100) // Update every 100 milliseconds
                }
            }
        }
        handler.post(updateSeekBarRunnable) // Start updating the SeekBar
    }

    // Ensure MediaPlayer is released when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Release the media player to free up resources
    }

    // Pause the MediaPlayer if the activity is paused
    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }
}
