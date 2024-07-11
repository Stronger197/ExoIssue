package com.example.exoissue

import androidx.media3.common.AudioAttributes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import java.util.concurrent.TimeUnit

@UnstableApi
class PlaybackService : MediaSessionService() {

    private var player: Player? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        player = createPlayerInstance().also {
            mediaSession = createMediaSessionInstance(it)
            it.addAnalyticsListener(EventLogger())
            it.setMediaItems(getDemoMediaItems())
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    private fun createPlayerInstance() =
        ExoPlayer.Builder(this)
            .setTrackSelector(DefaultTrackSelector(this))
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(TimeUnit.SECONDS.toMillis(10))
            .setSeekForwardIncrementMs(TimeUnit.SECONDS.toMillis(10))
            .build()

    private fun createMediaSessionInstance(player: Player) =
        MediaSession.Builder(this, player).build()
}