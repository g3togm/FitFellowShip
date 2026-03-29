package com.example.fellowshiprunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.controller.FellowshipController
import com.example.fellowshiprunner.model.characters.fellowshipMembers
import com.example.fellowshiprunner.view.ActivityLogView
import com.example.fellowshiprunner.view.CharacterSelectionView
import com.example.fellowshiprunner.view.MainView
import com.example.fellowshiprunner.ui.theme.FellowShipRunnerTheme
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.TextSecondary

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

// ── Eye of Sauron ─────────────────────────────────────────────────────────────

@Composable
fun EyeOfSauronBadge(size: Dp = 90.dp, eyeSize: Int = 28) {
    val infiniteTransition = rememberInfiniteTransition(label = "eye")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.eye),
            contentDescription = "Eye of Sauron",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .scale(scale)
                .graphicsLayer {
                    rotationZ = rotation
                    this.alpha = alpha
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
    when (controller.screen) {
        is Screen.CharacterSelection -> CharacterSelectionView(
            onCharacterSelected = controller::selectCharacter
        )
        is Screen.Main -> MainView(
            playerCharIndex = controller.playerCharIndex,
            activityCounts = controller.activityCounts,
            logEntries = controller.logEntries,
            onLogActivity = controller::navigateToActivityLog,
            onChangeCharacter = controller::navigateToCharacterSelection
        )
        is Screen.ActivityLog -> ActivityLogView(
            character = fellowshipMembers[controller.playerCharIndex],
            onConfirm = controller::logActivity,
            onBack = controller::navigateBack
        )
    }
}