package com.hci.eco

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val CAMERA_PREVIEW_SCREEN = "CameraPreviewScreen"
    const val CAMERA_PREVIEW_CONTENT = "CameraPreviewContentScreen"
    const val LYRICS_SCREEN = "LyricsScreen"
    const val GALLERY_SCREEN = GalleryRoutes.GALLERY_SCREEN // Use GalleryRoutes constants
    const val IMAGE_DETAIL_SCREEN = GalleryRoutes.IMAGE_DETAIL_SCREEN
}

@Composable
fun AppNavigation(viewModel: CameraPreviewViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        modifier = Modifier.navigationBarsPadding() // Ensure padding for system navigation bar
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CAMERA_PREVIEW_SCREEN,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            composable(Routes.CAMERA_PREVIEW_CONTENT) {
                CameraPreviewContent(
                    viewModel = viewModel,
                )
            }

            composable(Routes.LYRICS_SCREEN) {
                LyricsScreen()
            }

            // Add Gallery Screen Route
            composable(GalleryRoutes.GALLERY_SCREEN) {
                GalleryScreen(navController = navController)
            }

            // Add Image Detail Screen Route with parameter
            composable(
                "${GalleryRoutes.IMAGE_DETAIL_SCREEN}/{imageIndex}",
                arguments = listOf(navArgument("imageIndex") { type = NavType.StringType })
            ) { backStackEntry ->
                val imageIndex = backStackEntry.arguments?.getString("imageIndex")
                ImageDetailScreen(navController = navController, imageIndex = imageIndex)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val items = listOf(
            BottomNavItem.CameraPreview,
            BottomNavItem.Lyrics,
            BottomNavItem.Gallery
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { item.label },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination to avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    data object CameraPreview :
        BottomNavItem(Routes.CAMERA_PREVIEW_CONTENT, R.drawable.dslr_camera, "Camera")

    data object Lyrics : BottomNavItem(Routes.LYRICS_SCREEN, R.drawable.song_lyrics, "Lyrics")
    data object Gallery : BottomNavItem(Routes.GALLERY_SCREEN, R.drawable.music, "Gallery")
}
