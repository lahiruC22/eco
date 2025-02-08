package com.hci.eco

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val CAMERA_PREVIEW_SCREEN = "CameraPreviewScreen"
    const val CAMERA_PREVIEW_CONTENT = "CameraPreviewContentScreen"
    const val LYRICS_SCREEN = "LyricsScreen"

}

@Composable
fun AppNavigation(viewModel: CameraPreviewViewModel) {
    val navController = rememberNavController()

    Column(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding().background(Color.Transparent)) {
        NavHost(
            navController = navController,
            startDestination = Routes.CAMERA_PREVIEW_SCREEN,
            modifier = Modifier.weight(1f)
        ) {
            composable(Routes.CAMERA_PREVIEW_SCREEN) {
                CameraPreviewScreen(
                    onPermissionGranted = {
                        navController.navigate(Routes.CAMERA_PREVIEW_CONTENT) {
                            popUpTo(Routes.CAMERA_PREVIEW_SCREEN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.CAMERA_PREVIEW_CONTENT) {// Get NavController
                CameraPreviewContent(
                    viewModel = viewModel,
                    //navController = navController,
                )
            }

            composable(Routes.LYRICS_SCREEN) {
                LyricsScreen()
            }
        }
        // Persistent ButtonRow at the bottom, outside NavHost
        ButtonRow(
            viewModel = viewModel,
            navController = navController,
        )
    }
}