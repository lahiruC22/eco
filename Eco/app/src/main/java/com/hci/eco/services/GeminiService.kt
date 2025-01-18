package com.hci.eco.services

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import kotlin.text.toLongOrNull

interface InputInterface {
    fun getPrompt(): String
}

class TextInput(val text: String) : InputInterface {
    override fun getPrompt() = text
}

class VideoInput(val videoPath: String) : InputInterface {

    override fun getPrompt(): String {
        Log.d("VideoInput", "Processing video at: $videoPath")
        val frames = extractFrames(videoPath)
        val prompt = generatePromptFromFrames(frames)
        return prompt
    }

    private fun extractFrames(videoPath: String): List<Bitmap> {
        val retriever = MediaMetadataRetriever()
        val file = File(videoPath)
        val uri = Uri.fromFile(file)
        retriever.setDataSource(file.absolutePath)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        val frameCount = 10 // Adjust as needed
        val interval = duration / frameCount
        val frames = mutableListOf<Bitmap>()
        for (i in 0 until frameCount) {
            val time = i * interval
            val frame = retriever.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            if (frame != null) {
                frames.add(frame)
            }
        }
        return frames
    }

    private fun generatePromptFromFrames(frames: List<Bitmap>): String {
        val promptBuilder = StringBuilder()
        for (frame in frames) {
            val frameData = frame.toString()
            promptBuilder.append("Frame: $frameData\n")
        }
        return promptBuilder.toString()
    }
}

interface OutputInterface {
    // Define properties and methods for different output types
}

class TextOutput(val text: String) : OutputInterface

class GeminiService @Inject constructor(
    private val generativeModel: GenerativeModel
) {

    fun generateOutput(input: InputInterface): Flow<OutputInterface> = flow {
        try {
            val prompt = input.getPrompt()
            val content = content {
                text(prompt)
            }
            val response: GenerateContentResponse = generativeModel.generateContent(content)
            val outputText = response.text ?: "No response from Gemini"
            emit(TextOutput(outputText))
        } catch (e: Exception) {
            Log.e("GeminiService", "Error generating output", e)
            emit(TextOutput("Error: ${e.message}"))
        }
    }
}