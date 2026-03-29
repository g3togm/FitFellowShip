package com.example.fellowshiprunner.model

import android.content.Context
import com.example.fellowshiprunner.model.characters.ActivityLogEntry

class AppRepository(context: Context) {

    private val prefs = context.getSharedPreferences("fellowship_state", Context.MODE_PRIVATE)

    // ── Keys ──────────────────────────────────────────────────────────────────

    private object Keys {
        const val PLAYER_CHAR_INDEX = "player_char_index"
        const val ACTIVITY_COUNTS   = "activity_counts"   // "0,1,2,3"
        const val LOG_ENTRIES       = "log_entries"        // "Frodo::Joggen||Sam::Fußball"
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    fun loadPlayerCharIndex(): Int =
        prefs.getInt(Keys.PLAYER_CHAR_INDEX, 0)

    fun loadActivityCounts(): List<Int> =
        prefs.getString(Keys.ACTIVITY_COUNTS, null)
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: listOf(0, 0, 0, 0)

    fun loadLogEntries(): List<ActivityLogEntry> =
        prefs.getString(Keys.LOG_ENTRIES, null)
            ?.takeIf { it.isNotBlank() }
            ?.split("||")
            ?.mapNotNull { entry ->
                val parts = entry.split("::")
                if (parts.size == 2) ActivityLogEntry(parts[0], parts[1]) else null
            }
            ?: emptyList()

    // ── Write ─────────────────────────────────────────────────────────────────

    fun savePlayerCharIndex(index: Int) {
        prefs.edit().putInt(Keys.PLAYER_CHAR_INDEX, index).apply()
    }

    fun saveActivityCounts(counts: List<Int>) {
        prefs.edit().putString(Keys.ACTIVITY_COUNTS, counts.joinToString(",")).apply()
    }

    fun saveLogEntries(entries: List<ActivityLogEntry>) {
        val encoded = entries.joinToString("||") { "${it.characterName}::${it.activity}" }
        prefs.edit().putString(Keys.LOG_ENTRIES, encoded).apply()
    }
}
