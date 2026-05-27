package com.example.devicetoolv1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuButton(
    title: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(220.dp)
            .height(90.dp)
            .background(
                color = Color(0xFF3A3A3A),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {

                onClick()
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}