package com.example.fellowshiprunner.model.characters

import androidx.compose.ui.graphics.Color

data class CharacterData(
    val name: String,
    val visuals: CharacterVisuals,
    val location: String,
    val specialty: String,
    val quote: String,
    val ausdauer: Int,
    val mut: Int,
    val kraft: Int,
    val weeklyGoal: Int = 3,
    val isNew: Boolean = false
)

data class ActivityLogEntry(val characterName: String, val activity: String)

val fellowshipMembers = listOf(
    CharacterData(
        name = "Frodo",
        visuals = CharacterVisuals(
            emoji = "🧙",
            cardColors = listOf(Color(0xFF4A2010), Color(0xFF1A0B05))
        ),
        location = "Auenland",
        specialty = "Joggen",
        quote = "\"Ich wünschte, der Ring wäre nie zu mir gekommen.\"",
        ausdauer = 65, mut = 90, kraft = 50,
        weeklyGoal = 3
    ),
    CharacterData(
        name = "Sam",
        visuals = CharacterVisuals(
            emoji = "🌿",
            cardColors = listOf(Color(0xFF1E3820), Color(0xFF0A1A10))
        ),
        location = "Auenland",
        specialty = "Alle Sportarten",
        quote = "\"Kein Held ohne seinen Sam.\"",
        ausdauer = 85, mut = 100, kraft = 70,
        weeklyGoal = 1
    ),
    CharacterData(
        name = "Gimli",
        visuals = CharacterVisuals(
            emoji = "⚒️",
            cardColors = listOf(Color(0xFF252038), Color(0xFF0F0F1E))
        ),
        location = "Erebor",
        specialty = "Gewichte",
        quote = "\"Und mein Beil!\"",
        ausdauer = 70, mut = 80, kraft = 100,
        weeklyGoal = 3
    ),
    CharacterData(
        name = "Legolas",
        visuals = CharacterVisuals(
            emoji = "🏹",
            cardColors = listOf(Color(0xFF1A3828), Color(0xFF0A1A10))
        ),
        location = "Mirkwood",
        specialty = "Laufen",
        quote = "\"Mein Bogen ist bereit.\"",
        ausdauer = 100, mut = 85, kraft = 65,
        weeklyGoal = 3,
        isNew = true
    )
)
