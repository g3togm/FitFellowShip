package com.example.fellowshiprunner.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.fellowshiprunner.Screen
import com.example.fellowshiprunner.model.AppRepository
import com.example.fellowshiprunner.model.characters.ActivityLogEntry
import com.example.fellowshiprunner.model.characters.fellowshipMembers

class FellowshipController(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    // ── Navigation state ──────────────────────────────────────────────────────

    var screen by mutableStateOf<Screen>(Screen.CharacterSelection)
        private set

    // ── App state ─────────────────────────────────────────────────────────────

    var playerCharIndex by mutableStateOf(repository.loadPlayerCharIndex())
        private set

    val activityCounts = mutableStateListOf(*repository.loadActivityCounts().toTypedArray())

    val logEntries = mutableStateListOf(*repository.loadLogEntries().toTypedArray())

    // ── Actions ───────────────────────────────────────────────────────────────

    fun selectCharacter(index: Int) {
        playerCharIndex = index
        screen = Screen.Main
        repository.savePlayerCharIndex(index)
    }

    fun navigateToActivityLog() {
        screen = Screen.ActivityLog
    }

    fun navigateToCharacterSelection() {
        screen = Screen.CharacterSelection
    }

    fun logActivity(activity: String) {
        val goal = fellowshipMembers[playerCharIndex].weeklyGoal
        activityCounts[playerCharIndex] =
            (activityCounts[playerCharIndex] + 1).coerceAtMost(goal)
        logEntries.add(0, ActivityLogEntry(fellowshipMembers[playerCharIndex].name, activity))
        screen = Screen.Main
        repository.saveActivityCounts(activityCounts.toList())
        repository.saveLogEntries(logEntries.toList())
    }

    fun navigateBack() {
        screen = Screen.Main
    }
}