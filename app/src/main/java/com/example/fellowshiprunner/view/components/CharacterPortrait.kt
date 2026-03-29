package com.example.fellowshiprunner.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.fellowshiprunner.model.characters.CharacterVisuals

/**
 * Renders a character portrait.
 *
 * Currently falls back to an emoji on a gradient background.
 * To switch to real images: set [CharacterVisuals.portraitRes] and the
 * emoji/cardColors fields become unused and can be removed.
 *
 * The caller is responsible for size and clip shape via [modifier].
 */
@Composable
fun CharacterPortrait(
    visuals: CharacterVisuals,
    modifier: Modifier = Modifier,
    emojiSize: TextUnit = 36.sp
) {
    if (visuals.portraitRes != null) {
        Image(
            painter = painterResource(visuals.portraitRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Brush.verticalGradient(visuals.cardColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(visuals.emoji, fontSize = emojiSize)
        }
    }
}