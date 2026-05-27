package com.example.devicetoolv1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"

    ) {

        composable("home") {

            HomeScreen(navController)
        }

        composable("channel_monitoring") {

            ChannelMonitoringScreen(navController)
        }

        composable("stick_mode") {

            StickModeScreen(navController)
        }

        composable("video_viewing") {

            VideoViewingScreen(navController)
        }

        composable("advanced_options") {

            AdvancedOptionsScreen(navController)
        }

        composable("frequency_matching") {

            FrequencyMatchingScreen(navController)
        }
        composable("joystick") {

            JoystickScreen(navController)
        }
        composable("dual_joystick") {

            DualJoystickScreen(navController)
        }
    }
}