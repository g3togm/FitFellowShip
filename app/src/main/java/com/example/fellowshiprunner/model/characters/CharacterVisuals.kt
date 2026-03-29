package com.example.fellowshiprunner.model.characters

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class CharacterVisuals(
    // ── Placeholder (remove once real assets are added) ───────────────────────
    val emoji: String,
    val cardColors: List<Color>,
    // ── Real assets (set these to replace emoji + gradient) ───────────────────
    @DrawableRes val portraitRes: Int? = null,
    @DrawableRes val backgroundRes: Int? = null
)
