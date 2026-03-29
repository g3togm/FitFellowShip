package com.example.fellowshiprunner

import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fellowshiprunner.controller.FellowshipController
import com.example.fellowshiprunner.model.characters.fellowshipMembers
import com.example.fellowshiprunner.view.ActivityLogView
import com.example.fellowshiprunner.view.CharacterSelectionView
import com.example.fellowshiprunner.view.MainView
import com.example.fellowshiprunner.ui.theme.FellowShipRunnerTheme
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

// ── Navigation ────────────────────────────────────────────────────────────────

sealed class Screen {
    data object CharacterSelection : Screen()
    data object Main : Screen()
    data object ActivityLog : Screen()
}

// ── Shared UI ─────────────────────────────────────────────────────────────────

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Color(0xFF0D0A08),
        contentColor = Gold
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                indicatorColor = Color(0xFF2A1F14)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
            label = { Text("Explore") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}

@Composable
fun TopBarActions() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color.DarkGray.copy(alpha = 0.4f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Tools",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.DarkGray.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Gyroscope ─────────────────────────────────────────────────────────────────

@Composable
fun rememberGyroOffset(): State<Offset> {
    val context = LocalContext.current
    val offset = remember { mutableStateOf(Offset.Zero) }
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val gyro = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        var angleX = 0f
        var angleY = 0f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val dt = 0.016f
                angleX = (angleX + event.values[1] * dt).coerceIn(-0.5f, 0.5f)
                angleY = (angleY + event.values[0] * dt).coerceIn(-0.5f, 0.5f)
                angleX *= 0.95f
                angleY *= 0.95f
                offset.value = Offset(angleX, angleY)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager?.registerListener(listener, gyro, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager?.unregisterListener(listener) }
    }
    return offset
}

// ── Eye of Sauron ─────────────────────────────────────────────────────────────

@Composable
fun EyeOfSauronBadge(
    size: Dp = 180.dp,
    threatLevel: Float = 0f
) {
    val infinite = rememberInfiniteTransition(label = "eye")
    val gyroOffset by rememberGyroOffset()
    val context = LocalContext.current

    val pulseScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue  = 1f + 0.06f + threatLevel * 0.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                (2200 - (threatLevel * 700).toInt()).coerceAtLeast(800),
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    val glowAlpha by infinite.animateFloat(
        initialValue = 0.35f + threatLevel * 0.15f,
        targetValue  = 0.75f + threatLevel * 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    val fireFlicker by infinite.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(130, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "flicker"
    )

    // MediaPlayer lives for the lifetime of this composable
    val mediaPlayer = remember {
        MediaPlayer().apply {
            try {
                val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.eye_of_sauron}")
                setDataSource(context, uri)
                isLooping = true
                setVolume(0f, 0f)
                // FIX: set listener on MediaPlayer BEFORE prepareAsync
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.setVolume(0f, 0f)
                    mp.start()
                }
                prepareAsync()
            } catch (e: Exception) { /* silent — no crash if file missing */ }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            try { mediaPlayer.stop() } catch (e: Exception) {}
            mediaPlayer.release()
        }
    }

    // Compose-side Paint with Screen blend mode — works on all API levels
    // because we apply it through Canvas.saveLayer, not graphicsLayer.blendMode
    val screenPaint = remember {
        Paint().apply { blendMode = BlendMode.Screen }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .offset(
                x = (gyroOffset.x * 10f).dp,
                y = (gyroOffset.y * 10f).dp
            )
            .scale(pulseScale)
    ) {
        // Fire glow rings behind the video
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val base   = this.size.minDimension / 2f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF4500).copy(alpha = glowAlpha * 0.55f),
                        Color(0xFFFF6600).copy(alpha = glowAlpha * 0.20f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = base * (1.35f + fireFlicker * 0.08f)
                ),
                radius = base * (1.35f + fireFlicker * 0.08f),
                center = center
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFAA00).copy(alpha = glowAlpha * 0.40f),
                        Color(0xFFFF4500).copy(alpha = glowAlpha * 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = base * (0.9f + fireFlicker * 0.05f)
                ),
                radius = base * (0.9f + fireFlicker * 0.05f),
                center = center
            )
            repeat(8) { i ->
                val angle = (i / 8f) * 2f * Math.PI.toFloat() + fireFlicker
                val r     = base * (1.1f + fireFlicker * 0.12f * (i % 3) / 3f)
                drawCircle(
                    color  = Color(0xFFFFCC44).copy(alpha = glowAlpha * 0.5f),
                    radius = 2.5f + fireFlicker * 2f,
                    center = Offset(center.x + cos(angle) * r, center.y + sin(angle) * r)
                )
            }
        }

        // TextureView with Screen blend via Canvas.saveLayer — works on all APIs
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    isOpaque = false
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(
                            st: SurfaceTexture, w: Int, h: Int
                        ) {
                            // Attach surface to the already-preparing MediaPlayer
                            mediaPlayer.setSurface(Surface(st))
                        }
                        override fun onSurfaceTextureSizeChanged(
                            st: SurfaceTexture, w: Int, h: Int
                        ) {}
                        override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                            mediaPlayer.setSurface(null)
                            return true
                        }
                        override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize(0.88f)
                .drawWithContent {
                    // saveLayer with Screen paint: black pixels → transparent
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(
                            Rect(0f, 0f, size.width, size.height),
                            screenPaint
                        )
                        this.drawContent()
                        canvas.restore()
                    }
                }
        )
    }
}

// ── Entry point ───────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    private val controller: FellowshipController by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FellowShipRunnerTheme {
                FellowshipApp(controller)
            }
        }
    }
}

@Composable
fun FellowshipApp(controller: FellowshipController) {
    val context = LocalContext.current

    val soundManager = remember { SoundManager(context) }
    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }
    LaunchedEffect(Unit) {
        soundManager.startAmbient(volume = 0.2f)
    }

    CompositionLocalProvider(LocalSoundManager provides soundManager) {
        when (controller.screen) {
            is Screen.CharacterSelection -> CharacterSelectionView(
                onCharacterSelected = controller::selectCharacter
            )
            is Screen.Main -> MainView(
                playerCharIndex   = controller.playerCharIndex,
                activityCounts    = controller.activityCounts,
                logEntries        = controller.logEntries,
                onLogActivity     = controller::navigateToActivityLog,
                onChangeCharacter = controller::navigateToCharacterSelection
            )
            is Screen.ActivityLog -> {
                val safeIndex = controller.playerCharIndex
                    .coerceIn(0, fellowshipMembers.lastIndex)
                ActivityLogView(
                    character = fellowshipMembers[safeIndex],
                    onConfirm = controller::logActivity,
                    onBack    = controller::navigateBack
                )
            }
        }
    }
}