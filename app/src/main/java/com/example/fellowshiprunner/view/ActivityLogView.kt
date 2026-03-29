package com.example.fellowshiprunner.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.model.characters.CharacterData
import com.example.fellowshiprunner.rememberSoundManager
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.TextSecondary
import com.example.fellowshiprunner.view.components.AppBackground
import com.example.fellowshiprunner.view.components.CharacterPortrait

@Composable
fun ActivityLogView(
    character: CharacterData,
    onConfirm: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedActivity by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D0A08))
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!isSubmitting) onBack()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Abbrechen")
                    }

                    Button(
                        onClick = {
                            val activity = selectedActivity ?: return@Button
                            if (isSubmitting) return@Button

                            isSubmitting = true
                            sound.playWorkoutLogged()
                            sound.playSauronVoice()
                            onConfirm(activity)
                        },
                        modifier = Modifier.weight(2f),
                        enabled = selectedActivity != null && !isSubmitting,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Color.Black,
                            disabledContainerColor = Gold.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            if (isSubmitting) "Wird eingetragen..." else "Eintragen ✓",
                            fontWeight = FontWeight.Medium
                        )
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
                        Text(
                            text = character.name,
                            color = Gold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "Von Mittelerde",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Was hast du heute vollbracht?",
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
                                width = 1.dp,
                                color = if (isSelected) Gold else Color.DarkGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = !isSubmitting) {
                                selectedActivity = name
                            }
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = name,
                            color = if (isSelected) Gold else Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}