package com.hci.eco

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController

@Composable
fun CameraPreviewContent(
    viewModel: CameraPreviewViewModel,
    //navController: NavController,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera preview
            surfaceRequest?.let { request ->
                CameraXViewfinder(
                    surfaceRequest = request,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Buttons overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                //.navigationBarsPadding()
            //.padding(bottom = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large round button
                Box(
                    modifier = Modifier
                        .size(72.dp) // Large button
                        .border(BorderStroke(4.dp, Color.White), CircleShape) // Thick white stroke
                        .background(Color.Transparent, CircleShape) // Background color.
                        .clickable { viewModel.captureImage() }
                )
                Spacer(modifier = Modifier.height(12.dp)) // Spacing between buttons

//                // Existing button row
//                ButtonRow(
//                    viewModel = viewModel,
//                    navController = navController,
//                )
            }
        }
    }
}

