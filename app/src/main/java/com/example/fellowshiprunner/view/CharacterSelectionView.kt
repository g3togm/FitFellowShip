package com.example.fellowshiprunner.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.BottomNavBar
import com.example.fellowshiprunner.EyeOfSauronBadge
import com.example.fellowshiprunner.TopBarActions
import com.example.fellowshiprunner.model.characters.CharacterData
import com.example.fellowshiprunner.model.characters.fellowshipMembers
import com.example.fellowshiprunner.view.components.AppBackground
import com.example.fellowshiprunner.view.components.CharacterPortrait
import com.example.fellowshiprunner.ui.theme.Gold
import com.example.fellowshiprunner.ui.theme.TextSecondary

@Composable
fun CharacterSelectionView(onCharacterSelected: (Int) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    val selected = fellowshipMembers[selectedIndex]

    AppBackground(gradientHeightFraction = 0.55f) {
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
                TopBarActions()

                EyeOfSauronBadge()

                Spacer(Modifier.height(12.dp))

                Text(
                    "THE FELLOWSHIP\nOF FITNESS",
                    color = Gold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "WÄHLE DEINEN GEFÄHRTEN",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(20.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(fellowshipMembers) { index, character ->
                        CharacterCarouselCard(
                            character = character,
                            isSelected = index == selectedIndex,
                            onClick = { selectedIndex = index }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                CharacterDetailPanel(
                    character = selected,
                    onSelect = { onCharacterSelected(selectedIndex) }
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CharacterCarouselCard(character: CharacterData, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(155.dp)
            .alpha(if (isSelected) 1f else 0.5f)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isSelected) Modifier.border(2.dp, Gold, RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() }
    ) {
        CharacterPortrait(
            visuals = character.visuals,
            modifier = Modifier.fillMaxSize(),
            emojiSize = 52.sp
        )
        Text(
            character.name.uppercase(),
            color = if (isSelected) Gold else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun CharacterDetailPanel(character: CharacterData, onSelect: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(character.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Normal)
                Text(
                    "${character.location} · ${character.specialty}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("SPEZIALITÄT", color = TextSecondary, fontSize = 9.sp, letterSpacing = 1.sp)
                Text(character.specialty, color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(character.quote, color = TextSecondary, fontSize = 12.sp, fontStyle = FontStyle.Italic)

        Spacer(Modifier.height(16.dp))

        StatBar("AUSDAUER", character.ausdauer)
        Spacer(Modifier.height(10.dp))
        StatBar("MUT", character.mut)
        Spacer(Modifier.height(10.dp))
        StatBar("KRAFT", character.kraft)

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Gold),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("${character.name.uppercase()} WÄHLEN →", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatBar(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 10.sp, modifier = Modifier.width(72.dp), letterSpacing = 1.sp)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.DarkGray.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 100f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Gold)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text("$value", color = Gold, fontSize = 12.sp, modifier = Modifier.width(28.dp), textAlign = TextAlign.End)
    }
}