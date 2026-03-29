package com.example.fellowshiprunner

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// ── Gyroscope Flow ────────────────────────────────────────────────────────────

fun gyroFlow(context: Context): Flow<Pair<Float, Float>> = callbackFlow {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // event.values[0] = tilt left/right (roll)
            // event.values[1] = tilt forward/back (pitch)
            val roll  = event.values[0] * 30f   // scale to degrees
            val pitch = event.values[1] * 20f
            trySend(Pair(roll, pitch))
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    if (gyro != null) {
        sensorManager.registerListener(listener, gyro, SensorManager.SENSOR_DELAY_GAME)
    }
    awaitClose { sensorManager.unregisterListener(listener) }
}

// ── Eye of Sauron ─────────────────────────────────────────────────────────────

/**
 * threatLevel: 0f = safe, 1f = Sauron wins
 *
 * Features:
 * - Gyroscope: eye follows phone tilt
 * - Pulse speed & intensity scales with threat
 * - Size grows with threat
 * - Red glow overlay intensifies with threat
 */
@Composable
fun EyeOfSauronBadge(
    threatLevel: Float = 0f,   // paramter 0.0 - 1.0
    baseSize: Dp = 90.dp
) {
    val context = LocalContext.current

    // ── Gyro state ──────────────────────────────────────────────────────────
    var gyroRoll  by remember { mutableFloatStateOf(0f) }
    var gyroPitch by remember { mutableFloatStateOf(0f) }

    // Smooth gyro values
    val smoothRoll  by animateFloatAsState(
        targetValue = gyroRoll.coerceIn(-25f, 25f),
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 80f),
        label = "roll"
    )
    val smoothPitch by animateFloatAsState(
        targetValue = gyroPitch.coerceIn(-15f, 15f),
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 80f),
        label = "pitch"
    )

    LaunchedEffect(Unit) {
        gyroFlow(context).collect { (roll, pitch) ->
            gyroRoll  = roll
            gyroPitch = pitch
        }
    }

    // ── Threat-based values ─────────────────────────────────────────────────
    // Size: grows from baseSize to baseSize + 50dp at max threat
    // base size evtl hoeher
    val targetSize = baseSize + (50.dp * threatLevel)
    val animatedSize by animateDpAsState(
        targetValue = targetSize,
        animationSpec = tween(800, easing = EaseInOutSine),
        label = "size"
    )

    // Pulse speed: faster at higher threat
    // geschwindigkeit teste ich noch
    val pulseDuration = (2000 - (threatLevel * 1200)).toInt().coerceAtLeast(400)

    // ── Infinite animations ─────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "eye")

    // Pulse scale — amplitude grows with threat
    val pulseAmplitude = 0.04f + (threatLevel * 0.1f)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f + pulseAmplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Alpha flicker — more chaotic at high threat
    val flickerSpeed = (800 - (threatLevel * 500)).toInt().coerceAtLeast(150)
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(flickerSpeed, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    // Slow ambient rotation when threat is high
    val ambientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (threatLevel > 0.6f) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (8000 - (threatLevel * 5000)).toInt().coerceAtLeast(3000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambientRot"
    )

    // ── Render ───────────────────────────────────────────────────────────────
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.eye),
            contentDescription = "Eye of Sauron",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(animatedSize)
                .scale(pulseScale)
                .graphicsLayer {
                    // Gyro tilt
                    rotationZ = smoothRoll + ambientRotation
                    rotationX = smoothPitch
                    alpha = flickerAlpha

                    // Red glow effect via color filter approximation
                    // (intensifies at high threat via ambient rotation speed)
                }
        )
    }
}