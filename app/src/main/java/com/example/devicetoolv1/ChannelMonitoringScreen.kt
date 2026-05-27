package com.example.devicetoolv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class ChannelData(
    val name: String,
    var value: Int
)

@Composable
fun ChannelMonitoringScreen(navController: NavHostController) {

    val channels = remember {

        mutableStateListOf<ChannelData>().apply {

            for (i in 1..16) {

                add(
                    ChannelData(
                        name = "CH$i",
                        value = 1500
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp)
    ) {

        Text(
            text = "CHANNEL MONITORING",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(channels) { channel ->

                ChannelRow(channel)
            }
        }

        Button(
            onClick = {

                navController.popBackStack()
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF444444)
            ),

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("BACK")
        }
    }
}

@Composable
fun ChannelRow(channel: ChannelData) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = channel.name,
                color = Color.White,
                fontSize = 18.sp
            )

            Text(
                text = channel.value.toString(),
                color = Color.Green,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.DarkGray
        )
    }
}