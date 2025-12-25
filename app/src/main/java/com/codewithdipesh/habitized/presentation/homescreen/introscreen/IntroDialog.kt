package com.codewithdipesh.habitized.presentation.homescreen.introscreen

import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.codewithdipesh.habitized.R
import com.codewithdipesh.habitized.ui.theme.instrumentSerif

enum class IntroDialogState {
    LOADING,
    PLAYING,
    ENDED,
    NO_INTERNET
}

@OptIn(UnstableApi::class)
@Composable
fun IntroDialog(
    videoUrl: String,
    onDismiss: () -> Unit,
    onVideoEnd : () -> Unit
) {
    val context = LocalContext.current


    var state by rememberSaveable { mutableStateOf(IntroDialogState.LOADING) }
    var videoProgress by rememberSaveable { mutableFloatStateOf(0f) }
    var elapsedTime by rememberSaveable { mutableLongStateOf(0L) }
    val skipThreshold = 15000L // 15 seconds in milliseconds
    val isSkippable = elapsedTime >= skipThreshold
    val skipProgress = (elapsedTime.toFloat() / skipThreshold.toFloat()).coerceIn(0f, 1f)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val cache = VideoCache.getInstance(context)
            val cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))

            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                       state = IntroDialogState.LOADING
                    }
                    Player.STATE_READY -> {
                       state = IntroDialogState.PLAYING
                    }
                    Player.STATE_ENDED -> {
                        state = IntroDialogState.ENDED
                        onVideoEnd()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                state = IntroDialogState.NO_INTERNET
            }
        })
    }

    // Track video progress and elapsed time
    LaunchedEffect(state) {
        while (state == IntroDialogState.PLAYING || state == IntroDialogState.LOADING) {
            val duration = exoPlayer.duration
            val position = exoPlayer.currentPosition
            if (duration > 0) {
                videoProgress = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
            }
            // Track elapsed time for skip button
            if (state == IntroDialogState.PLAYING && elapsedTime < skipThreshold) {
                elapsedTime += 100
            }
            delay(100) // Update every 100ms
        }
        if (state == IntroDialogState.ENDED) {
            videoProgress = 1f
        }
    }

    // Smooth animation for skip progress
    val animatedSkipProgress by animateFloatAsState(
        targetValue = skipProgress,
        animationSpec = tween(durationMillis = 150),
        label = "skipProgress"
    )

    // Smooth animation for progress
    val animatedProgress by animateFloatAsState(
        targetValue = videoProgress,
        animationSpec = tween(durationMillis = 150),
        label = "progress"
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            // Clear video cache since intro won't be shown again
            VideoCache.clearCache(context)
        }
    }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Introduction to 4 types of Habits",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = instrumentSerif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                val isDarkTheme = isSystemInDarkTheme()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ){
                    when(state){
                        IntroDialogState.LOADING -> {
                            LoadingUI(isDarkTheme = isDarkTheme)
                        }
                        IntroDialogState.NO_INTERNET -> {
                            NoInternetUI(isDarkTheme = isDarkTheme)
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        // Use CardView to properly clip the video content
                                        CardView(ctx).apply {
                                            radius = 10f * ctx.resources.displayMetrics.density // 10dp
                                            cardElevation = 0f
                                            setCardBackgroundColor(android.graphics.Color.TRANSPARENT)
                                            clipChildren = true
                                            clipToPadding = true
                                            preventCornerOverlap = true

                                            val playerView = PlayerView(ctx).apply {
                                                player = exoPlayer
                                                useController = false
                                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                            }
                                            addView(playerView)

                                            layoutParams = ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Replay button overlay when video ends
                                if (state == IntroDialogState.ENDED) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                            .clickable {
                                                exoPlayer.seekTo(0)
                                                exoPlayer.play()
                                                videoProgress = 0f
                                                state = IntroDialogState.PLAYING
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.replay),
                                            contentDescription = "Replay",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Horizontal video progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(MaterialTheme.colorScheme.onPrimary)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Skip/Continue button with progress animation
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                   //continue
                    Box(
                        modifier = Modifier
                            .size(167.dp, 36.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (state == IntroDialogState.ENDED)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                            )
                            .clickable(enabled = state == IntroDialogState.ENDED) {
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = instrumentSerif,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.inverseOnSurface.copy(
                                    alpha = if (state == IntroDialogState.ENDED) 1f else 0.3f
                                )
                            )
                        )
                    }

                    //skip
                    Box(
                        modifier = Modifier
                            .size(100.dp, 36.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isSkippable)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                            )
                            .clickable(enabled = state == IntroDialogState.ENDED) {
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isSkippable || state == IntroDialogState.ENDED -> {
                                // Skip button after 15 seconds
                                Text(
                                    text = "Skip",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = instrumentSerif,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.inverseOnSurface
                                    )
                                )
                            }
                            else -> {
                                // Show countdown with circular progress
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        // Background circle
                                        CircularProgressIndicator(
                                            progress = { 1f },
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary.copy(0.3f),
                                            strokeWidth = 2.dp
                                        )
                                        // Progress circle
                                        CircularProgressIndicator(
                                            progress = { animatedSkipProgress },
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Skip in ${((skipThreshold - elapsedTime) / 1000).coerceAtLeast(1)}s",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontFamily = instrumentSerif,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun LoadingUI(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true
) {
   val phoneImage = if (isDarkTheme) R.drawable.intro_phone_dark else R.drawable.intro_phone_light
   Box(modifier.fillMaxSize()){
       Image(
          painter = painterResource(phoneImage),
           contentDescription = null,
           modifier = Modifier.align(Alignment.BottomEnd)
               .padding(end = 24.dp)
       )
       Column(
           modifier = Modifier
               .fillMaxWidth(0.5f)
               .align(Alignment.CenterStart)
               .padding(start = 24.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
       ){
           Text(
               text = "Loading a quick intro to improve your experience...",
               style = TextStyle(
                   fontSize = 16.sp,
                   fontFamily = instrumentSerif,
                   fontWeight = FontWeight.Normal,
                   color = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                   textAlign = TextAlign.Start
               )
           )
           Spacer(Modifier.height(24.dp))
           CircularProgressIndicator(
               modifier = Modifier
                   .size(24.dp),
               color = MaterialTheme.colorScheme.primary,
               strokeWidth = 2.dp,
           )
       }
   }
}

@Composable
fun NoInternetUI(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true
) {
    val phoneImage = if (isDarkTheme) R.drawable.intro_phone_dark else R.drawable.intro_phone_light
    val noInternetIcon = if (isDarkTheme) R.drawable.no_internet_dark else R.drawable.no_internet_light
    Box(modifier.fillMaxSize()){
        Image(
            painter = painterResource(phoneImage),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterStart)
                .padding(start = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                painter = painterResource(noInternetIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "No Internet",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "This intro needs internet once Please connect and reopen app.",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = instrumentSerif,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                )
            )
        }
    }
}

