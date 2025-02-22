package com.hci.eco

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.indendshape.ShapeCornerRadius
import com.exyte.animatednavbar.items.dropletbutton.DropletButton

@Composable
fun ButtonRow(
    viewModel: CameraPreviewViewModel,
    navController: NavController,
    modifier: Modifier = Modifier.background(Color.Transparent)
    //modifier: Modifier,
) {
    var selectedIndex by remember { mutableIntStateOf(1) }
    //val context = LocalContext.current

    AnimatedNavigationBar(
        selectedIndex = selectedIndex,
        barColor = MaterialTheme.colorScheme.onSurface,
        ballColor = Color.White,
        cornerRadius = ShapeCornerRadius(24F, 24F, 0F, 0F),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Gallery Button
        DropletButton(
            isSelected = selectedIndex == 0,
            onClick = {
                selectedIndex = 0
                navController.navigate(Routes.GALLERY_SCREEN)
            },
            icon = R.drawable.music,
            contentDescription = "Music",
            modifier = Modifier.size(48.dp)
        )

        // Capture Button
        DropletButton(
            isSelected = selectedIndex == 1,
            onClick = {
                selectedIndex = 1
                navController.navigate(Routes.CAMERA_PREVIEW_CONTENT)
            },
            icon = R.drawable.dslr_camera,
            contentDescription = "Capture",
            modifier = Modifier.size(48.dp)
        )

        // Settings Button
        DropletButton(
            isSelected = selectedIndex == 2,
            onClick = {
                selectedIndex = 2
                navController.navigate(Routes.LYRICS_SCREEN)
            },
            icon = R.drawable.song_lyrics,
            contentDescription = "Lyrics",
            modifier = Modifier.size(48.dp)
        )
    }
}
