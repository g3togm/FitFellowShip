package com.example.fellowshiprunner.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.model.characters.CharacterData
import com.example.fellowshiprunner.rememberSoundManager
import com.example.fellowshiprunner.view.components.AppBackground
import com.example.fellowshiprunner.view.components.CharacterPortrait
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.TextSecondary

@Composable
fun ActivityLogView(
    character: CharacterData,
    onConfirm: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedActivity by remember { mutableStateOf<String?>(null) }
    // Gets the shared instance from CompositionLocal — no new SoundManager created
    val sound = rememberSoundManager()

    val activities = listOf(
        "🏃" to "Joggen",
        "⚽" to "Fußball",
        "🏋️" to "Gewichte"
    )

    AppBackground(gradientHeightFraction = 0.6f) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // FIX: navigationBarsPadding() pushes buttons above the gesture area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D0A08))
                        .navigationBarsPadding()          // ← the key fix
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Abbrechen")
                    }
                    Button(
                        onClick = {
                            selectedActivity?.let { activity ->
                                // Play one of the two Sauron lines randomly
                                sound.playSauronVoice()
                                onConfirm(activity)
                            }
                        },
                        modifier = Modifier.weight(2f),
                        enabled = selectedActivity != null,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Color.Black,
                            disabledContainerColor = Gold.copy(alpha = 0.3f)
                        )
                    ) {
                        Text("Eintragen ✓", fontWeight = FontWeight.Medium)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(48.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CharacterPortrait(
                        visuals = character.visuals,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        emojiSize = 24.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(character.name, color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Normal)
                        Text("Von Mittelerde", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    "Was hast du heute vollbracht?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )

                Spacer(Modifier.height(24.dp))

                activities.forEach { (emoji, name) ->
                    val isSelected = selectedActivity == name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Gold.copy(alpha = 0.15f)
                                else Color(0xFF1A120B).copy(alpha = 0.8f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Gold else Color.DarkGray.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedActivity = name }
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(16.dp))
                        Text(name, color = if (isSelected) Gold else Color.White, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}