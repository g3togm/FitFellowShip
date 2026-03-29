package com.example.fellowshiprunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.viewModels
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
            Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun EyeOfSauronBadge(size: Int = 64, eyeSize: Int = 28) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(
                Brush.radialGradient(listOf(Color(0xFFCC2200), Color(0xFF661100))),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("👁️", fontSize = eyeSize.sp)
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
