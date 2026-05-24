package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.player.VideoPlayerScreen
import com.example.ui.theme.GlassPlayerTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val app = applicationContext as GlassPlayerApp
    
    setContent {
      GlassPlayerTheme {
        val navController = rememberNavController()
        
        Scaffold(
          bottomBar = {
            NavigationBar {
              NavigationBarItem(
                icon = { Text("Home") },
                selected = false,
                onClick = { navController.navigate("home") }
              )
              NavigationBarItem(
                icon = { Text("Playlists") },
                selected = false,
                onClick = { navController.navigate("playlists") }
              )
              NavigationBarItem(
                icon = { Text("Settings") },
                selected = false,
                onClick = { navController.navigate("settings") }
              )
            }
          }
        ) { innerPadding ->
          NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") {
              val homeViewModel = remember { HomeViewModel(app.dependencyProvider.localMediaScanner) }
              HomeScreen(
                viewModel = homeViewModel,
                onVideoClick = { mediaItem ->
                  val encodedUri = android.net.Uri.encode(mediaItem.uri)
                  if (mediaItem.isVideo) {
                    navController.navigate("player/$encodedUri")
                  } else {
                    navController.navigate("audio/$encodedUri")
                  }
                }
              )
            }
            composable("player/{uri}") { backStackEntry ->
              val uri = backStackEntry.arguments?.getString("uri") ?: ""
              VideoPlayerScreen(uriString = uri)
            }
            composable("audio/{uri}") { backStackEntry ->
              val uri = backStackEntry.arguments?.getString("uri") ?: ""
              com.example.presentation.player.AudioPlayerScreen(uriString = uri)
            }
            composable("playlists") {
              val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                  override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                      return com.example.presentation.playlists.PlaylistViewModel(app.dependencyProvider.playlistRepository) as T
                  }
              }
              val viewModel: com.example.presentation.playlists.PlaylistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
              com.example.presentation.playlists.PlaylistsScreen(viewModel = viewModel)
            }
            composable("settings") {
              com.example.presentation.settings.SettingsScreen()
            }
          }
        }
      }
    }
  }
}

