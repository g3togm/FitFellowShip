package com.example.fellowshiprunner.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.BottomNavBar
import com.example.fellowshiprunner.EyeOfSauronBadge
import com.example.fellowshiprunner.model.characters.ActivityLogEntry
import com.example.fellowshiprunner.model.characters.CharacterData
import com.example.fellowshiprunner.model.characters.fellowshipMembers
import com.example.fellowshiprunner.view.components.AppBackground
import com.example.fellowshiprunner.view.components.CharacterPortrait
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.SauronOrange
import com.example.fellowshiprunner.ui.theme.TextSecondary

@Composable
fun MainView(
    playerCharIndex: Int,
    activityCounts: List<Int>,
    logEntries: List<ActivityLogEntry>,
    onLogActivity: () -> Unit,
    onChangeCharacter: () -> Unit
) {
    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BottomNavBar() }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    "FELLOWSHIP OF FITNESS",
                    color = Gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(20.dp))

                SauronPowerBar(progress = 0.75f)

                Spacer(Modifier.height(16.dp))

                QuestProgressRow()

                Spacer(Modifier.height(24.dp))

                EyeOfSauronBadge(size = 72, eyeSize = 32)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Der Ring muss zerstört werden",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )

                Spacer(Modifier.height(24.dp))

                FellowshipGrid(
                    playerCharIndex = playerCharIndex,
                    activityCounts = activityCounts,
                    onLogActivity = onLogActivity
                )

                Spacer(Modifier.height(24.dp))

                StreakCard()

                Spacer(Modifier.height(20.dp))

                ChronicleCard(logEntries = logEntries)

                Spacer(Modifier.height(16.dp))

                Text(
                    "⚙  CHARACTER WECHSELN",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clickable { onChangeCharacter() }
                        .padding(8.dp)
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SauronPowerBar(progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🔥  SAURONS MACHT", style = MaterialTheme.typography.labelSmall, color = SauronOrange)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = SauronOrange)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SauronOrange,
            trackColor = Color.DarkGray.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Die Feinde liegen am Fingerring",
            color = TextSecondary,
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic
        )
    }
}

private data class QuestStep(val emoji: String, val label: String, val done: Boolean)

@Composable
fun QuestProgressRow() {
    val steps = listOf(
        QuestStep("🌳", "Auenland", true),
        QuestStep("🏔️", "Bruchtal", false),
        QuestStep("⛏️", "Moria", false),
        QuestStep("🌸", "Lórien", false),
        QuestStep("🌋", "Mordor", false)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("✕  DIE QUEESTE NACH MORDOR", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { i, step ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (step.done) Gold.copy(alpha = 0.2f)
                                else Color(0xFF1A1208).copy(alpha = 0.6f)
                            )
                            .then(
                                if (step.done) Modifier.border(1.dp, Gold, CircleShape)
                                else Modifier.border(1.dp, Color.DarkGray.copy(alpha = 0.4f), CircleShape)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(step.emoji, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(step.label, fontSize = 8.sp, color = if (step.done) Gold else TextSecondary)
                }
                if (i < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.DarkGray.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@Composable
fun FellowshipGrid(
    playerCharIndex: Int,
    activityCounts: List<Int>,
    onLogActivity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("DIE GEFÄHRTEN", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(Modifier.height(12.dp))

        fellowshipMembers.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { character ->
                    val index = fellowshipMembers.indexOf(character)
                    FellowshipCard(
                        character = character,
                        completed = activityCounts[index],
                        isPlayer = playerCharIndex == index,
                        modifier = Modifier.weight(1f),
                        onLogActivity = onLogActivity
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun FellowshipCard(
    character: CharacterData,
    completed: Int,
    isPlayer: Boolean,
    modifier: Modifier = Modifier,
    onLogActivity: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A120B)),
        border = BorderStroke(1.dp, if (isPlayer) Gold.copy(alpha = 0.8f) else Color.DarkGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (character.isNew) {
                    Surface(
                        color = Color(0xFF1A6B3A),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            "NEU",
                            color = Color.White,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            CharacterPortrait(
                visuals = character.visuals,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                emojiSize = 36.sp
            )

            Spacer(Modifier.height(6.dp))
            Text(character.name, color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Normal)
            Text(character.specialty, color = TextSecondary, fontSize = 9.sp)
            Spacer(Modifier.height(6.dp))

            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f), thickness = 1.dp)

            Spacer(Modifier.height(6.dp))
            Text("$completed/${character.weeklyGoal}", color = Gold, fontSize = 13.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(character.weeklyGoal) { i ->
                    Text(
                        if (i < completed) "●" else "◦",
                        color = if (i < completed) Gold else Color.DarkGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isPlayer) {
                OutlinedButton(
                    onClick = onLogActivity,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
                ) {
                    Text("+ Training loggen", fontSize = 10.sp)
                }
            } else {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StreakCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A120B)),
        border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏆", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text("FELLOWSHIP-STREAK", style = MaterialTheme.typography.labelSmall, color = Gold)
            }
            Spacer(Modifier.height(16.dp))
            Text("0", fontSize = 80.sp, color = Gold, fontWeight = FontWeight.Light)
            Text("Wochen erfolgreich abgeschlossen", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                "\"Even the smallest person can change the course of the future.\"",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ChronicleCard(logEntries: List<ActivityLogEntry>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A120B)),
        border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📜", fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                Text("CHRONIK DER HELDENTATEN", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Spacer(Modifier.height(16.dp))

            if (logEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Noch keine Heldentaten...",
                        color = TextSecondary,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp
                    )
                }
            } else {
                logEntries.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏃", fontSize = 16.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(entry.characterName, color = Gold, fontSize = 13.sp)
                            Text(entry.activity, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.2f), thickness = 1.dp)
                }
            }
        }
    }
}
