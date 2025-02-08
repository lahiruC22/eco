package com.hci.eco

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hci.eco.ui.theme.Shapes

object GalleryRoutes {
    const val GALLERY_SCREEN = "GalleryScreen"
    const val IMAGE_DETAIL_SCREEN =
        "ImageDetailScreen/{imageIndex}" // Route with parameter for image index
}

@Composable
fun GalleryScreen(navController: NavHostController) {
    val images = rememberGalleryImages() // Function to provide sample image data

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 columns in the grid
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(images) { imageData ->
            ImageCard(imageData = imageData) { index ->
                navController.navigate(
                    GalleryRoutes.IMAGE_DETAIL_SCREEN.replace(
                        "{imageIndex}",
                        index.toString()
                    )
                )
            }
        }
    }
}

@Composable
fun ImageCard(imageData: ImageData, onImageClick: (Int) -> Unit) {
    Card(
        shape = Shapes.medium, // Using your defined Shapes.medium, customize in Shapes.kt
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onImageClick(imageData.imageResId) // Pass the resource ID as index for simplicity in example
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(imageData.imageResId),
                contentDescription = imageData.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // Adjust height as needed
                contentScale = ContentScale.Crop
            )
            Text(
                text = imageData.title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun rememberGalleryImages(): List<ImageData> {
    return listOf(
        ImageData(
            R.drawable.baked_goods_1,
            "Party Picture",
            "30th July 2021"
        ), // Replace with your actual image resources
        ImageData(R.drawable.baked_goods_2, "Marriage Hall", "Our First Meet"),
        ImageData(R.drawable.baked_goods_3, "Reception Event", "Lorem Ipsum description..."),
        ImageData(R.drawable.baked_goods_1, "Photo shoot", "Another description..."),
        ImageData(R.drawable.baked_goods_2, "Pre Wedding", "Yet another description..."),
        ImageData(R.drawable.baked_goods_3, "Wedding Photos", "Last description for now...")
        // Add more ImageData instances here
    )
}