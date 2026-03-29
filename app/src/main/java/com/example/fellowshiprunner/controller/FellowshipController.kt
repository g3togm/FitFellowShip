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

    // Clamp saved index so it's always valid even if fellowshipMembers shrinks
    var playerCharIndex by mutableStateOf(
        repository.loadPlayerCharIndex().coerceIn(0, fellowshipMembers.lastIndex)
    )
        private set

    // FIX: Always ensure activityCounts has exactly one entry per fellowship member.
    // If the saved list is shorter (e.g. new member added, fresh install, corrupted save),
    // pad with zeros. If longer, truncate. This is the most common crash source.
    val activityCounts = mutableStateListOf(
        *buildList {
            val saved = repository.loadActivityCounts()
            fellowshipMembers.forEachIndexed { i, _ ->
                add(saved.getOrElse(i) { 0 })
            }
        }.toTypedArray()
    )

    val logEntries = mutableStateListOf(*repository.loadLogEntries().toTypedArray())

    // ── Actions ───────────────────────────────────────────────────────────────

    fun selectCharacter(index: Int) {
        // Guard: only accept valid indices
        if (index !in fellowshipMembers.indices) return
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
        // Guard: ensure index is valid before touching activityCounts
        val safeIndex = playerCharIndex.coerceIn(0, fellowshipMembers.lastIndex)
        if (safeIndex >= activityCounts.size) return

        val goal = fellowshipMembers[safeIndex].weeklyGoal
        activityCounts[safeIndex] = (activityCounts[safeIndex] + 1).coerceAtMost(goal)

        logEntries.add(
            index = 0,
            element = ActivityLogEntry(fellowshipMembers[safeIndex].name, activity)
        )

        screen = Screen.Main
        repository.saveActivityCounts(activityCounts.toList())
        repository.saveLogEntries(logEntries.toList())
    }

    fun navigateBack() {
        screen = Screen.Main
    }
}