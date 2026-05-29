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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                add(ChannelData(name = "CH$i", value = 1500))
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
            items(channels.size) { index ->
                ChannelRow(
                    channel = channels[index],
                    onValueChange = { newValue ->
                        channels[index] = channels[index].copy(value = newValue)
                    }
                )
            }
        }

        Button(
            onClick = { navController.popBackStack() },
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
fun ChannelRow(
    channel: ChannelData,
    onValueChange: (Int) -> Unit
) {
    var sliderPosition by remember(channel.name) {
        mutableFloatStateOf(channel.value.toFloat())
    }

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

        Slider(
            value = sliderPosition,
            onValueChange = { newVal ->
                sliderPosition = newVal
                onValueChange(newVal.toInt())
            },
            valueRange = 1000f..2000f,
            steps = 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTrackColor = Color.Green,
                inactiveTrackColor = Color.DarkGray
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            color = Color.DarkGray
        )
    }
}