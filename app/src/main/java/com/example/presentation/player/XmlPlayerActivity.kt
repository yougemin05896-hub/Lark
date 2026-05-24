package com.example.presentation.player

import android.app.PictureInPictureParams
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.util.Rational
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.R
import kotlinx.coroutines.*
import kotlin.math.abs

class XmlPlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var btnCenterPlayPause: ImageView
    private lateinit var btnSmallPlayPause: ImageView
    private lateinit var tvTime: TextView
    private lateinit var timeline: SeekBar
    private lateinit var tvGestureFeedback: TextView
    
    // UI Panels for Auto-Hide
    private lateinit var gradientOverlay: View
    private lateinit var bottomControls: View
    private lateinit var btnLockScreen: ImageView
    private lateinit var btnSpeedFloat: ImageView

    private var isPlaying = true
    private var controlsVisible = true

    // Gestures
    private lateinit var gestureDetector: GestureDetector
    private lateinit var audioManager: AudioManager
    private var maxVolume = 1
    private var isVolumeGesture = false
    private var isBrightnessGesture = false
    private var isSeekGesture = false
    private var seekTargetPosition = 0L

    private var progressJob: Job? = null
    private var hideControlsJob: Job? = null
    private var hideFeedbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Android 15/16 Modern Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        setContentView(R.layout.activity_glass_player)
        supportActionBar?.hide()

        val uriString = intent.getStringExtra("VIDEO_URI") ?: ""

        bindViews()
        initializePlayer(uriString)
        setupGestures()
        setupClickListeners()
        scheduleHideControls()
        setupPictureInPicture()
    }

    private fun setupPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .setAutoEnterEnabled(true)
                .build()
            setPictureInPictureParams(pipParams)
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            hideControls()
            hideControlsJob?.cancel()
        } else {
            showControls()
            scheduleHideControls()
        }
    }

    private fun bindViews() {
        playerView = findViewById(R.id.player_view)
        btnCenterPlayPause = findViewById(R.id.btn_center_play_pause)
        btnSmallPlayPause = findViewById(R.id.btn_small_play_pause)
        tvTime = findViewById(R.id.tv_time)
        timeline = findViewById(R.id.player_timeline)
        gradientOverlay = findViewById(R.id.gradient_overlay)
        bottomControls = findViewById(R.id.bottom_controls_bar)
        btnLockScreen = findViewById(R.id.btn_lock_screen)
        btnSpeedFloat = findViewById(R.id.btn_speed_float)
        tvGestureFeedback = findViewById(R.id.tv_gesture_feedback)
    }

    private fun setupGestures() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                isVolumeGesture = false
                isBrightnessGesture = false
                isSeekGesture = false
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false

                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y

                // Determine gesture type if not set
                if (!isVolumeGesture && !isBrightnessGesture && !isSeekGesture) {
                    if (abs(deltaX) > abs(deltaY)) {
                        isSeekGesture = true
                        seekTargetPosition = player?.currentPosition ?: 0L
                    } else {
                        val screenWidth = resources.displayMetrics.widthPixels
                        if (e1.x > screenWidth / 2f) {
                            isVolumeGesture = true
                        } else {
                            isBrightnessGesture = true
                        }
                    }
                }

                // Handle the detected gesture
                val viewHeight = playerView.height.toFloat()
                val viewWidth = playerView.width.toFloat()

                when {
                    isVolumeGesture -> {
                        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                        // Negative deltaY means swiping up
                        val volumeDelta = (deltaY / viewHeight) * maxVolume * -2.5f
                        var newVolumeX = currentVolume + volumeDelta.toInt()
                        newVolumeX = newVolumeX.coerceIn(0, maxVolume)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolumeX, 0)

                        val percent = (newVolumeX.toFloat() / maxVolume * 100).toInt()
                        showFeedback("Volume $percent%")
                    }
                    isBrightnessGesture -> {
                        // Current brightness
                        var currentBrightness = window.attributes.screenBrightness
                        if (currentBrightness < 0f) {
                            currentBrightness = 0.5f // Default
                        }
                        val brightnessDelta = (deltaY / viewHeight) * -2.5f
                        var newBrightness = currentBrightness + brightnessDelta
                        newBrightness = newBrightness.coerceIn(0.01f, 1f)

                        val lp = window.attributes
                        lp.screenBrightness = newBrightness
                        window.attributes = lp

                        val percent = (newBrightness * 100).toInt()
                        showFeedback("Brightness $percent%")
                    }
                    isSeekGesture -> {
                        player?.let { p ->
                            // 60 seconds total swipe range across full screen width
                            val seekDeltaMs = ((deltaX / viewWidth) * 60000L).toLong()
                            seekTargetPosition = (seekTargetPosition + seekDeltaMs).coerceIn(0L, p.duration)
                            
                            val seekTime = formatTime(seekTargetPosition)
                            showFeedback("Seek $seekTime")
                            p.seekTo(seekTargetPosition)
                        }
                    }
                }
                return true
            }
        })

        // Intercept touches on GradientOverlay and PlayerView
        gradientOverlay.setOnTouchListener { _, event ->
            if (gestureDetector.onTouchEvent(event)) true
            else {
                // If the gesture ends, hide the feedback shortly
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    hideFeedbackAfterDelay()
                }
                false
            }
        }
        playerView.setOnTouchListener { _, event ->
            gradientOverlay.dispatchTouchEvent(event)
        }
    }

    private fun showFeedback(text: String) {
        tvGestureFeedback.text = text
        tvGestureFeedback.visibility = View.VISIBLE
        hideFeedbackJob?.cancel()
    }

    private fun hideFeedbackAfterDelay() {
        hideFeedbackJob?.cancel()
        hideFeedbackJob = scope.launch {
            delay(1000)
            tvGestureFeedback.visibility = View.GONE
        }
    }

    private fun initializePlayer(uriString: String) {
        if(uriString.isEmpty()) return

        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(uriString)))
            prepare()
            playWhenReady = true
        }
        playerView.player = player

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                isPlaying = isPlayingState
                updatePlayPauseUI()
                
                if (isPlayingState) {
                    startProgressTracker()
                    scheduleHideControls()
                } else {
                    stopProgressTracker()
                    showControls() // Keep controls visible when paused
                    hideControlsJob?.cancel()
                }
            }
        })
    }

    private fun updatePlayPauseUI() {
        val iconRes = if (isPlaying) R.drawable.ic_pause_massive else R.drawable.ic_play_massive
        btnCenterPlayPause.setImageResource(iconRes)
        btnSmallPlayPause.setImageResource(iconRes)
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            player?.pause()
        } else {
            player?.play()
        }
    }

    private fun setupClickListeners() {
        btnCenterPlayPause.setOnClickListener { 
            togglePlayPause() 
            scheduleHideControls()
        }
        btnSmallPlayPause.setOnClickListener { 
            togglePlayPause() 
            scheduleHideControls()
        }

        // Tap the screen background to toggle controls ON/OFF
        gradientOverlay.setOnClickListener {
            if (controlsVisible) {
                hideControls()
            } else {
                showControls()
                scheduleHideControls()
            }
        }
    }

    private fun showControls() {
        controlsVisible = true
        gradientOverlay.visibility = View.VISIBLE
        bottomControls.visibility = View.VISIBLE
        btnCenterPlayPause.visibility = View.VISIBLE
        btnLockScreen.visibility = View.VISIBLE
        btnSpeedFloat.visibility = View.VISIBLE
    }

    private fun hideControls() {
        controlsVisible = false
        gradientOverlay.visibility = View.GONE
        bottomControls.visibility = View.GONE
        btnCenterPlayPause.visibility = View.GONE
        btnLockScreen.visibility = View.GONE
        btnSpeedFloat.visibility = View.GONE
    }

    private fun scheduleHideControls() {
        hideControlsJob?.cancel()
        hideControlsJob = scope.launch {
            delay(3500)
            if (isPlaying) {
                hideControls()
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                player?.let { p ->
                    val currentPos = p.currentPosition
                    val duration = p.duration.coerceAtLeast(0)
                    if (duration > 0) {
                        timeline.max = duration.toInt()
                        timeline.progress = currentPos.toInt()
                        tvTime.text = "${formatTime(currentPos)} / ${formatTime(duration)}"
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = millis / (1000 * 60 * 60)
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideControlsJob?.cancel()
        stopProgressTracker()
        player?.release()
    }
}
