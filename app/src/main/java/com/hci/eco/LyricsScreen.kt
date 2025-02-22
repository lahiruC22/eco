package com.hci.eco

//import androidx.compose.material3.TextField
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.ImageRequest

@Composable
fun LyricsScreen(
    lyricsViewModel: LyricsViewModel = viewModel()
) {
    val context = LocalContext.current
/*    // Create an ImageLoader instance with caching configuration
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(0.25) // Use up to 25% of the app's available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02) // Use up to 2% of the device's available storage
                    .build()
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(
                        coil.util.CoilUtils.createDefaultCache(context)
                    )
                    .build()
            }
            .build()
    }*/

    val selectedImage = remember { mutableIntStateOf(0) }
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    val prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by lyricsViewModel.uiState.collectAsState()
    val images by lyricsViewModel.images.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.lyrics_generator),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(top = 42.dp, bottom = 32.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(images) { index, imageMetaData ->
                val imageUri = Uri.parse(imageMetaData.imageUri)
                var imageModifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .requiredSize(200.dp)
                    .clickable {
                        selectedImage.intValue = index
                    }
                if (index == selectedImage.intValue) {
                    imageModifier =
                        imageModifier.border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
                }
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .build()
                    ),
                    contentDescription = stringResource(R.string.image_description, index),
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
        }

        Button(
            onClick = {
                val bitmap = if (images.isNotEmpty()) {
                    val selectedImageUri = Uri.parse(images[selectedImage.intValue].imageUri)
                    context.contentResolver.openInputStream(selectedImageUri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } else {
                    null
                }
                bitmap?.let {
                    lyricsViewModel.sendPrompt(it, prompt)
                }
            },
            enabled = prompt.isNotEmpty() && images.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.action_go))
        }

        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            var textColor = MaterialTheme.colorScheme.onSurface
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                result = (uiState as UiState.Success).outputText
            }
            val scrollState = rememberScrollState()
            SelectionContainer {
                Text(
                    text = result,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                )
            }
        }
    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun LyricsScreenPreview() {
//    LyricsScreen()
//}