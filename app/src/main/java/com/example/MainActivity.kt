package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.player.PlayerViewModel
import com.example.presentation.player.VideoPlayerScreen
import com.example.presentation.player.AudioPlayerScreen
import com.example.presentation.playlists.PlaylistViewModel
import com.example.presentation.playlists.PlaylistsScreen
import com.example.presentation.settings.SettingsScreen
import com.example.ui.theme.GlassPlayerTheme

class MainActivity : ComponentActivity() {

    // Instantiate PlayerViewModel globally at Host level
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = applicationContext as GlassPlayerApp
        
        setContent {
            GlassPlayerTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    containerColor = Color.Black,
                    bottomBar = {
                        val navBackStackEntry = navController.currentBackStackEntryAsState().value
                        val currentRoute = navBackStackEntry?.destination?.route ?: "home"
                        if (currentRoute in listOf("home", "playlists", "settings")) {
                            com.example.presentation.navigation.LiquidBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        navController.graph.startDestinationRoute?.let { startRoute ->
                                            popUpTo(startRoute) { saveState = true }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = "home", 
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color.Black)
                    ) {
                        composable("home") {
                            val homeViewModel = remember { HomeViewModel(app.dependencyProvider.localMediaScanner) }
                            HomeScreen(
                                viewModel = homeViewModel,
                                onVideoClick = { mediaItem ->
                                    val encodedUri = android.net.Uri.encode(mediaItem.uri)
                                    if (mediaItem.isVideo) {
                                        // Pause any background audio
                                        playerViewModel.pause()
                                        // Skip background ViewModel for videos, ComposePlayerActivity handles its own ExoPlayer
                                        try {
                                            val intent = android.content.Intent(this@MainActivity, com.example.presentation.player.ComposePlayerActivity::class.java)
                                            intent.putExtra("VIDEO_URI", mediaItem.uri)
                                            startActivity(intent)
                                        } catch (e: Exception) {
                                            android.widget.Toast.makeText(this@MainActivity, "Failed to open video", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        playerViewModel.loadVideo(mediaItem.uri, mediaItem.title)
                                        navController.navigate("audio/$encodedUri")
                                    }
                                }
                            )
                        }
                        composable("audio/{uri}") { backStackEntry ->
                            val uri = backStackEntry.arguments?.getString("uri") ?: ""
                            AudioPlayerScreen(uriString = uri)
                        }
                        composable("playlists") {
                            val factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return PlaylistViewModel(app.dependencyProvider.playlistRepository) as T
                                }
                            }
                            val viewModel: PlaylistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                            PlaylistsScreen(viewModel = viewModel)
                        }
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Here we can resume the ExoPlayer if needed, 
        // usually ExoPlayer handles OS foregrounding internally if configured.
    }

    override fun onStop() {
        super.onStop()
        // Prevent background playback leak when closing the app
        playerViewModel.pause()
    }
}
