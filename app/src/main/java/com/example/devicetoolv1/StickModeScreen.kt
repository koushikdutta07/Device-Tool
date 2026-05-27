package com.example.devicetoolv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun StickModeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "STICK MODE",
            color = Color.White,
            fontSize = 28.sp
        )

        Button(
            onClick = {
                navController.popBackStack()
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {

            Text("BACK")
        }
    }
}