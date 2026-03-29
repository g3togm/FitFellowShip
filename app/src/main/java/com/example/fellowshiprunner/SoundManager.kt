package com.example.fellowshiprunner

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class SoundManager(private val context: Context) {

    private var ambientPlayer: MediaPlayer? = null
    private var voicePlayer: MediaPlayer? = null

    // simple guard against very fast repeated taps creating overlapping voice setup
    private var isStartingVoice = false

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var soundWorkout = 0
    private var soundThreat = 0

    init {
        loadSounds()
    }

    private fun loadSounds() {
        try {
            soundWorkout = soundPool.load(context, R.raw.workout_logged, 1)
            soundThreat = soundPool.load(context, R.raw.threat_high, 1)
        } catch (_: Exception) {
            // silent fallback if file missing
        }
    }

    // ── Sauron voice lines ────────────────────────────────────────────────────

    fun playSauronVoice() {
        val resId = if ((0..1).random() == 0) {
            R.raw.sauron_you_cannot_hide
        } else {
            R.raw.sauron_i_see_you
        }
        playVoiceRes(resId)
    }

    // ── Sound effects ─────────────────────────────────────────────────────────

    fun playWorkoutLogged() {
        if (soundWorkout != 0) {
            soundPool.play(soundWorkout, 0.9f, 0.9f, 1, 0, 1f)
        }
    }

    fun playThreatHigh() {
        if (soundThreat != 0) {
            soundPool.play(soundThreat, 0.7f, 0.7f, 1, 0, 1f)
        }
    }

    // ── Character voicelines ──────────────────────────────────────────────────

    fun playVoiceline(characterId: String) {
        val resId = when (characterId) {
            "frodo" -> R.raw.voice_frodo
            "sam" -> R.raw.voice_sam
            "gimli" -> R.raw.voice_gimli
            "legolas" -> R.raw.voice_legolas
            else -> return
        }
        playVoiceRes(resId)
    }

    private fun playVoiceRes(resId: Int) {
        if (isStartingVoice) return
        isStartingVoice = true

        try {
            stopVoice()

            val player = MediaPlayer.create(context, resId) ?: run {
                isStartingVoice = false
                return
            }

            voicePlayer = player

            player.setVolume(1f, 1f)

            player.setOnCompletionListener { completedPlayer ->
                try {
                    completedPlayer.setOnCompletionListener(null)
                } catch (_: Exception) {
                }

                try {
                    completedPlayer.release()
                } catch (_: Exception) {
                }

                if (voicePlayer === completedPlayer) {
                    voicePlayer = null
                }
            }

            player.setOnErrorListener { errorPlayer, _, _ ->
                try {
                    errorPlayer.release()
                } catch (_: Exception) {
                }

                if (voicePlayer === errorPlayer) {
                    voicePlayer = null
                }
                true
            }

            player.start()
        } catch (_: Exception) {
            stopVoice()
        } finally {
            isStartingVoice = false
        }
    }

    private fun stopVoice() {
        val player = voicePlayer ?: return
        voicePlayer = null

        try {
            player.setOnCompletionListener(null)
        } catch (_: Exception) {
        }

        try {
            player.setOnErrorListener(null)
        } catch (_: Exception) {
        }

        // IMPORTANT:
        // Do NOT call stop() here.
        // MediaPlayer.stop() is only valid in certain states and caused the crash.
        try {
            player.release()
        } catch (_: Exception) {
        }
    }

    // ── Ambient ───────────────────────────────────────────────────────────────

    fun startAmbient(volume: Float = 0.25f) {
        if (ambientPlayer != null) return
        try {
            ambientPlayer = MediaPlayer.create(context, R.raw.ambient)?.apply {
                isLooping = true
                setVolume(volume, volume)
                start()
            }
        } catch (_: Exception) {
            // no file yet
        }
    }

    fun setAmbientVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        try {
            ambientPlayer?.setVolume(v, v)
        } catch (_: Exception) {
        }
    }

    fun pauseAmbient() {
        try {
            ambientPlayer?.pause()
        } catch (_: Exception) {
        }
    }

    fun resumeAmbient() {
        try {
            ambientPlayer?.start()
        } catch (_: Exception) {
        }
    }

    fun release() {
        try {
            soundPool.release()
        } catch (_: Exception) {
        }

        try {
            ambientPlayer?.release()
        } catch (_: Exception) {
        }
        ambientPlayer = null

        stopVoice()
    }
}

val LocalSoundManager = compositionLocalOf<SoundManager?> { null }

@Composable
fun rememberSoundManager(): SoundManager {
    return LocalSoundManager.current ?: run {
        val context = LocalContext.current
        remember { SoundManager(context) }
    }
}