package com.example.mountainbikextreme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mountainbikextreme.ui.components.Biome
import com.example.mountainbikextreme.ui.screens.ContinueScreen
import com.example.mountainbikextreme.ui.screens.MapSelectionScreen
import com.example.mountainbikextreme.ui.theme.MountainBikeXtremeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MountainBikeXtremeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }
}

private object Routes {
    const val CONTINUE = "continue"
    const val MAP_SELECTION = "map_selection"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val selectedBiome = remember { mutableStateOf(Biome.FOREST) }

    NavHost(navController = navController, startDestination = Routes.CONTINUE) {

        composable(Routes.CONTINUE) {
            ContinueScreen(
                onContinue = {
                    navController.navigate(Routes.MAP_SELECTION)
                },
                onSettings = {
                    // Settings screen not built yet — placeholder for future work
                },
                onExit = {
                    // In a real app, finish() the activity here
                }
            )
        }

        composable(Routes.MAP_SELECTION) {
            MapSelectionScreen(
                initiallySelected = selectedBiome.value,
                onBack = { navController.popBackStack() },
                onEnvironmentChosen = { biome ->
                    selectedBiome.value = biome
                    // Next step: navigate to the main menu / gameplay screen (not built yet)
                }
            )
        }
    }
}
