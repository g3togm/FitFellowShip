package com.example.fellowshiprunner

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class SoundManager(private val context: Context) {

    private var ambientPlayer: MediaPlayer? = null
    private var voicePlayer:   MediaPlayer? = null

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
    private var soundThreat  = 0

    init { loadSounds() }

    private fun loadSounds() {
        try {
            soundWorkout = soundPool.load(context, R.raw.workout_logged, 1)
            soundThreat  = soundPool.load(context, R.raw.threat_high,    1)
        } catch (e: Exception) { /* silent fallback */ }
    }

    // ── Sound effects ─────────────────────────────────────────────────────────

    fun playWorkoutLogged() {
        if (soundWorkout != 0) soundPool.play(soundWorkout, 0.9f, 0.9f, 1, 0, 1f)
    }

    fun playThreatHigh() {
        if (soundThreat != 0) soundPool.play(soundThreat, 0.7f, 0.7f, 1, 0, 1f)
    }

    // ── Voicelines ────────────────────────────────────────────────────────────

    /**
     * Call this when a character is selected in the carousel.
     * characterId: "frodo" | "sam" | "gimli" | "legolas"
     */
    fun playVoiceline(characterId: String) {
        val resId = when (characterId) {
            "frodo"   -> R.raw.voice_frodo
            "sam"     -> R.raw.voice_sam
            "gimli"   -> R.raw.voice_gimli
            "legolas" -> R.raw.voice_legolas
            else      -> return
        }
        stopVoice()
        try {
            voicePlayer = MediaPlayer.create(context, resId)?.apply {
                setVolume(1f, 1f)
                setOnCompletionListener { release() }
                start()
            }
        } catch (e: Exception) { /* silent fallback */ }
    }

    private fun stopVoice() {
        voicePlayer?.stop()
        voicePlayer?.release()
        voicePlayer = null
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
        } catch (e: Exception) { /* no file yet */ }
    }

    /** Scale ambient volume with threat — louder = more danger */
    fun setAmbientVolume(volume: Float) {
        ambientPlayer?.setVolume(volume.coerceIn(0f, 1f), volume.coerceIn(0f, 1f))
    }

    fun pauseAmbient()  { ambientPlayer?.pause() }
    fun resumeAmbient() { ambientPlayer?.start() }

    fun release() {
        soundPool.release()
        ambientPlayer?.release()
        ambientPlayer = null
        voicePlayer?.release()
        voicePlayer = null
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    val manager = remember { SoundManager(context) }
    DisposableEffect(Unit) {
        onDispose { manager.release() }
    }
    return manager
}