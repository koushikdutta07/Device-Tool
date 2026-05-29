package com.example.devicetoolv1

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.math.sqrt

// ── Palette ───────────────────────────────────────────────────────────────────
private val BgDark       = Color(0xFF080C10)
private val BgCard       = Color(0xFF0D1117)
private val BgCardAlt    = Color(0xFF111820)
private val AccentCyan   = Color(0xFF00E5FF)
private val AccentGreen  = Color(0xFF00FF88)
private val AccentYellow = Color(0xFFFFD600)
private val AccentRed    = Color(0xFFFF3D57)
private val AccentPurple = Color(0xFFBB86FC)
private val TextPrimary  = Color(0xFFE8F0F8)
private val TextMuted    = Color(0xFF4A6070)
private val BorderColor  = Color(0xFF1C2830)

@Composable
fun DualJoystickScreen(navController: NavHostController) {

    var leftX  by remember { mutableFloatStateOf(0f) }
    var leftY  by remember { mutableFloatStateOf(0f) }
    var rightX by remember { mutableFloatStateOf(0f) }
    var rightY by remember { mutableFloatStateOf(0f) }

    var ch1 by remember { mutableIntStateOf(1500) }
    var ch2 by remember { mutableIntStateOf(1500) }
    var ch3 by remember { mutableIntStateOf(1000) }
    var ch4 by remember { mutableIntStateOf(1500) }

    var battery by remember { mutableStateOf("BATTERY: --") }
    var rssi    by remember { mutableStateOf("RSSI: --") }
    var latency by remember { mutableStateOf("LATENCY: --") }
    var gps     by remember { mutableStateOf("GPS: --") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            UdpSender.sendChannels(ch1, ch2, ch3, ch4)
            delay(300)
        }
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            while (true) {
                var socket: DatagramSocket? = null
                try {
                    socket = DatagramSocket(6006)
                    socket.soTimeout = 0
                    while (true) {
                        val buffer = ByteArray(1024)
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        val message = String(packet.data, 0, packet.length).trim()
                        withContext(Dispatchers.Main) {
                            when {
                                message.startsWith("BATTERY:") -> battery = message
                                message.startsWith("RSSI:")    -> rssi    = message
                                message.startsWith("LATENCY:") -> latency = message
                                message.startsWith("GPS:")     -> gps     = message
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    socket?.close()
                }
                delay(2000)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Header ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "FPV",
                    color = AccentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "CONTROLLER",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            // GPS badge
            val gpsLocked = gps.contains("LOCKED")
            Box(
                modifier = Modifier
                    .background(
                        color = if (gpsLocked) AccentGreen.copy(alpha = 0.15f)
                        else AccentRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (gpsLocked) "⬤  GPS LOCK" else "⬤  NO GPS",
                    color = if (gpsLocked) AccentGreen else AccentRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = BorderColor)
        Spacer(modifier = Modifier.height(24.dp))

        // ── Telemetry Row ─────────────────────────────────────────
        val batteryPct = battery.filter { it.isDigit() }.toFloatOrNull() ?: 0f
        val rssiPct    = rssi.filter { it.isDigit() }.toFloatOrNull() ?: 0f
        val latencyMs  = latency.filter { it.isDigit() }.toFloatOrNull() ?: 0f

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiniStatCard(
                label = "BATTERY",
                value = if (batteryPct > 0f) "${batteryPct.toInt()}%" else "--",
                progress = (batteryPct / 100f).coerceIn(0f, 1f),
                color = when {
                    batteryPct > 50f -> AccentGreen
                    batteryPct > 20f -> AccentYellow
                    else             -> AccentRed
                },
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                label = "RSSI",
                value = if (rssiPct > 0f) "${rssiPct.toInt()}%" else "--",
                progress = (rssiPct / 100f).coerceIn(0f, 1f),
                color = AccentCyan,
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                label = "LATENCY",
                value = if (latencyMs > 0f) "${latencyMs.toInt()}ms" else "--",
                progress = ((100f - latencyMs) / 100f).coerceIn(0f, 1f),
                color = AccentYellow,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Joystick Panel ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BgCard)
                .padding(vertical = 24.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Left joystick + labels
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "THROTTLE / YAW",
                        color = TextMuted,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    JoystickView(
                        knobColor = AccentCyan,
                        ringColor = AccentCyan.copy(alpha = 0.2f),
                        onMove = { x, y ->
                            leftX = x; leftY = y
                            ch4 = (1500 + x * 4).toInt().coerceIn(1000, 2000)
                            ch3 = (1500 - y * 4).toInt().coerceIn(1000, 2000)
                        },
                        onRelease = {
                            leftX = 0f; leftY = 0f
                            ch4 = 1500
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipValue("T", ch3, AccentCyan)
                        ChipValue("Y", ch4, AccentCyan)
                    }
                }

                // Centre divider
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(6) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(8.dp)
                                .background(BorderColor)
                        )
                    }
                }

                // Right joystick + labels
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROLL / PITCH",
                        color = TextMuted,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    JoystickView(
                        knobColor = AccentGreen,
                        ringColor = AccentGreen.copy(alpha = 0.2f),
                        onMove = { x, y ->
                            rightX = x; rightY = y
                            ch1 = (1500 + x * 4).toInt().coerceIn(1000, 2000)
                            ch2 = (1500 - y * 4).toInt().coerceIn(1000, 2000)
                        },
                        onRelease = {
                            rightX = 0f; rightY = 0f
                            ch1 = 1500; ch2 = 1500
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipValue("R", ch1, AccentGreen)
                        ChipValue("P", ch2, AccentGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Channel Bars ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BgCard)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "CHANNEL OUTPUT",
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                ChannelBar("CH1  ROLL",     ch1, AccentGreen)
                ChannelBar("CH2  PITCH",    ch2, AccentGreen)
                ChannelBar("CH3  THROTTLE", ch3, AccentCyan)
                ChannelBar("CH4  YAW",      ch4, AccentCyan)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Back Button ───────────────────────────────────────────
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = BgCardAlt),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "← BACK",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

// ── Mini Stat Card ────────────────────────────────────────────────────────────
@Composable
fun MiniStatCard(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progress"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp
            )
            Text(
                text = value,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
            // Thin arc progress bar drawn with Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                drawRoundRect(
                    color = BorderColor,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(4f)
                )
                drawRoundRect(
                    color = color,
                    size = Size(size.width * animatedProgress, size.height),
                    cornerRadius = CornerRadius(4f)
                )
            }
        }
    }
}

// ── Channel Bar ───────────────────────────────────────────────────────────────
@Composable
fun ChannelBar(name: String, value: Int, color: Color) {
    val progress = ((value - 1000f) / 1000f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(120),
        label = "bar"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = name,
            color = TextMuted,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.width(110.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BorderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.6f), color)
                        )
                    )
            )
        }
        Text(
            text = value.toString(),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(36.dp)
        )
    }
}

// ── Chip Value ────────────────────────────────────────────────────────────────
@Composable
fun ChipValue(label: String, value: Int, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "$label: $value",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Joystick View ─────────────────────────────────────────────────────────────
@Composable
fun JoystickView(
    knobColor: Color,
    ringColor: Color,
    onMove: (Float, Float) -> Unit,
    onRelease: () -> Unit
) {
    var joystickX by remember { mutableFloatStateOf(0f) }
    var joystickY by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = Modifier
            .size(160.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        joystickX += dragAmount.x
                        joystickY += dragAmount.y
                        val distance = sqrt(joystickX * joystickX + joystickY * joystickY)
                        val maxRadius = 65f
                        if (distance > maxRadius) {
                            joystickX = joystickX / distance * maxRadius
                            joystickY = joystickY / distance * maxRadius
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
        val radius = size.minDimension / 2f

        // Outer ring fill
        drawCircle(
            color = ringColor,
            radius = radius
        )

        // Outer ring stroke
        drawCircle(
            color = knobColor.copy(alpha = 0.3f),
            radius = radius - 2f,
            style = Stroke(width = 2f)
        )

        // Crosshair lines
        drawLine(
            color = knobColor.copy(alpha = 0.15f),
            start = Offset(center.x - radius + 12f, center.y),
            end   = Offset(center.x + radius - 12f, center.y),
            strokeWidth = 1f
        )
        drawLine(
            color = knobColor.copy(alpha = 0.15f),
            start = Offset(center.x, center.y - radius + 12f),
            end   = Offset(center.x, center.y + radius - 12f),
            strokeWidth = 1f
        )

        // Inner dead-zone circle
        drawCircle(
            color = knobColor.copy(alpha = 0.06f),
            radius = 18f
        )

        // Knob shadow glow
        drawCircle(
            color = knobColor.copy(alpha = 0.25f),
            radius = 34f,
            center = Offset(center.x + joystickX, center.y + joystickY)
        )

        // Knob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.9f), knobColor),
                center = Offset(
                    center.x + joystickX - 6f,
                    center.y + joystickY - 6f
                ),
                radius = 28f
            ),
            radius = 26f,
            center = Offset(center.x + joystickX, center.y + joystickY)
        )

        // Knob rim
        drawCircle(
            color = knobColor,
            radius = 26f,
            center = Offset(center.x + joystickX, center.y + joystickY),
            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
        )
    }
}