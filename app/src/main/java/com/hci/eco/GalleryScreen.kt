package com.hci.eco

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.hci.eco.ui.theme.Shapes

@Composable
fun GalleryScreen(navController: NavHostController = rememberNavController()) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: GalleryViewModel = viewModel(factory = GalleryViewModelFactory(application))
    val images by viewModel.images.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.gallery_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(top = 42.dp, bottom = 32.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(images) { index, imageData ->
                ImageCard(imageData = imageData) {
                    navController.navigate(
                        "${GalleryRoutes.IMAGE_DETAIL_SCREEN}/$index"
                    )
                }
            }
        }
    }
}

@Composable
fun ImageCard(imageData: ImageMetaData, onImageClick: () -> Unit) {
    Card(
        shape = Shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onImageClick()
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = rememberAsyncImagePainter(model = imageData.imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

object GalleryRoutes {
    const val GALLERY_SCREEN = "GalleryScreen"
    const val IMAGE_DETAIL_SCREEN = "ImageDetailScreen"
}

