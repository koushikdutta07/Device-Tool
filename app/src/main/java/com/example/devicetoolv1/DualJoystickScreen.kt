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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.sqrt
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun DualJoystickScreen(navController: NavHostController) {

    // LEFT JOYSTICK
    var leftX by remember { mutableFloatStateOf(0f) }
    var leftY by remember { mutableFloatStateOf(0f) }

    // RIGHT JOYSTICK
    var rightX by remember { mutableFloatStateOf(0f) }
    var rightY by remember { mutableFloatStateOf(0f) }

    // CHANNELS
    var ch1 by remember { mutableIntStateOf(1500) } // Roll
    var ch2 by remember { mutableIntStateOf(1500) } // Pitch
    var ch3 by remember { mutableIntStateOf(1000) } // Throttle
    var ch4 by remember { mutableIntStateOf(1500) } // Yaw

    LaunchedEffect(Unit) {

        while (true) {

            UdpSender.sendChannels(
                ch1,
                ch2,
                ch3,
                ch4
            )

            delay(50)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "RC CONTROLLER",
            color = Color.White,
            fontSize = 28.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),

            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // LEFT JOYSTICK
            JoystickView(
                knobColor = Color.Cyan,

                onMove = { x, y ->

                    leftX = x
                    leftY = y

                    ch4 = (1500 + x * 4).toInt()
                    ch3 = (1500 + y * 4).toInt()

                    ch3 = ch3.coerceIn(1000, 2000)
                    ch4 = ch4.coerceIn(1000, 2000)
                },

                onRelease = {

                    leftX = 0f
                    leftY = 0f

                    ch4 = 1500
                }
            )

            // RIGHT JOYSTICK
            JoystickView(
                knobColor = Color.Green,

                onMove = { x, y ->

                    rightX = x
                    rightY = y

                    ch1 = (1500 + x * 4).toInt()
                    ch2 = (1500 + y * 4).toInt()

                    ch1 = ch1.coerceIn(1000, 2000)
                    ch2 = ch2.coerceIn(1000, 2000)
                },

                onRelease = {

                    rightX = 0f
                    rightY = 0f

                    ch1 = 1500
                    ch2 = 1500
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {

            ChannelDisplay("CH1 ROLL", ch1)
            ChannelDisplay("CH2 PITCH", ch2)
            ChannelDisplay("CH3 THROTTLE", ch3)
            ChannelDisplay("CH4 YAW", ch4)
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
fun JoystickView(
    knobColor: Color,
    onMove: (Float, Float) -> Unit,
    onRelease: () -> Unit
) {

    var joystickX by remember { mutableFloatStateOf(0f) }
    var joystickY by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = Modifier
            .size(180.dp)
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

                        val maxRadius = 70f

                        if (distance > maxRadius) {

                            joystickX =
                                joystickX / distance * maxRadius

                            joystickY =
                                joystickY / distance * maxRadius
                        }

                        onMove(joystickX, joystickY)
                    },

                    onDragEnd = {

                        joystickX = 0f
                        joystickY = 0f

                        onRelease()
                    }
                )
            }
    ) {

        drawCircle(
            color = Color.DarkGray,
            radius = size.minDimension / 2
        )

        drawCircle(
            color = knobColor,
            radius = 30f,
            center = Offset(
                x = center.x + joystickX,
                y = center.y + joystickY
            )
        )
    }
}

@Composable
fun ChannelDisplay(
    name: String,
    value: Int
) {

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
                text = name,
                color = Color.White,
                fontSize = 18.sp
            )

            Text(
                text = value.toString(),
                color = Color.Green,
                fontSize = 18.sp
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            color = Color.DarkGray
        )
    }
}