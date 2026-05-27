package com.example.devicetoolv1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.sqrt

@Composable
fun JoystickScreen(navController: NavHostController) {

    var joystickX by remember { mutableFloatStateOf(0f) }
    var joystickY by remember { mutableFloatStateOf(0f) }

    var ch1 by remember { mutableIntStateOf(1500) }
    var ch2 by remember { mutableIntStateOf(1500) }
    var ch3 by remember { mutableIntStateOf(1000) }
    var ch4 by remember { mutableIntStateOf(1500) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "JOYSTICK TEST",
            color = Color.White,
            fontSize = 28.sp
        )

        Canvas(
            modifier = Modifier
                .padding(top = 40.dp)
                .size(300.dp)
                .pointerInput(Unit) {

                    detectDragGestures(

                        onDrag = { change, dragAmount ->

                            change.consume()

                            joystickX += dragAmount.x
                            joystickY += dragAmount.y

                            val distance = sqrt(
                                joystickX * joystickX +
                                        joystickY * joystickY
                            )

                            val maxRadius = 120f

                            if (distance > maxRadius) {

                                joystickX =
                                    joystickX / distance * maxRadius

                                joystickY =
                                    joystickY / distance * maxRadius
                            }

                            ch1 = (1500 + joystickX * 4).toInt()
                            ch2 = (1500 + joystickY * 4).toInt()

                            ch1 = ch1.coerceIn(1000, 2000)
                            ch2 = ch2.coerceIn(1000, 2000)
                        },

                        onDragEnd = {

                            joystickX = 0f
                            joystickY = 0f

                            ch1 = 1500
                            ch2 = 1500
                        }
                    )
                }
        ) {

            drawCircle(
                color = Color.DarkGray,
                radius = size.minDimension / 2
            )

            drawCircle(
                color = Color.Green,
                radius = 40f,
                center = Offset(
                    x = center.x + joystickX,
                    y = center.y + joystickY
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {

            ChannelValue("CH1", ch1)
            ChannelValue("CH2", ch2)
            ChannelValue("CH3", ch3)
            ChannelValue("CH4", ch4)
        }

        Button(
            onClick = {

                navController.popBackStack()
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF444444)
            ),

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {

            Text("BACK")
        }
    }
}

@Composable
fun ChannelValue(
    name: String,
    value: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = name,
            color = Color.White,
            fontSize = 20.sp
        )

        Text(
            text = value.toString(),
            color = Color.Green,
            fontSize = 20.sp
        )
    }

    HorizontalDivider(color = Color.DarkGray)
}