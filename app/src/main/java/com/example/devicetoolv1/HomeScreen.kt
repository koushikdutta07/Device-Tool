package com.example.devicetoolv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Device Tool",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            MenuButton("STICK MODE") {

                navController.navigate("dual_joystick")
            }

            MenuButton("VIDEO VIEWING") {

                navController.navigate("video_viewing")
            }
        }

        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            MenuButton("CHANNEL MONITORING") {

                navController.navigate("channel_monitoring")
            }

            MenuButton("ADVANCED OPTIONS") {

                navController.navigate("advanced_options")
            }
        }

        Row(
            modifier = Modifier.padding(top = 20.dp)
        ) {

            MenuButton("FREQUENCY MATCHING") {

                navController.navigate("frequency_matching")
            }
        }
    }
}