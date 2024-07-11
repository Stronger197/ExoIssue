package com.example.exoissue

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.PlayerView
import com.example.exoissue.ui.theme.ExoIssueTheme
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {

    private suspend fun getPlayerController() =
        MediaController.Builder(this, SessionToken(this, ComponentName(this, PlaybackService::class.java)))
            .buildAsync()
            .await()


    private var controller = mutableStateOf<MediaController?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            controller.value = getPlayerController().apply {
                playWhenReady = true
                prepare()
            }
        }

        setContent {
            ExoIssueTheme {
                Surface(color = Color.Black) {
                    Content(
                        onPrevClick = { controller.value?.seekToPreviousMediaItem() },
                        onNextClick = { controller.value?.seekToNextMediaItem() },
                    )
                }
            }
        }
    }

    @Composable
    fun Content(
        onPrevClick: () -> Unit,
        onNextClick: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(controller.value != null) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            useController = false
                            resizeMode = RESIZE_MODE_FIT
                            player = controller.value
                        }
                    },
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            Button(onClick = onPrevClick, Modifier.align(Alignment.BottomStart)) {
                Text(text = "Prev")
            }
            Button(onClick = onNextClick, Modifier.align(Alignment.BottomEnd)) {
                Text(text = "Next")
            }
        }
    }
}