package com.example.videoplayers2.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayers2.DoubleClickListener
import com.example.videoplayers2.R
import com.example.videoplayers2.adapters.IconAdapter
import com.example.videoplayers2.databinding.ActivityExoPlayerMedia3Binding
import com.example.videoplayers2.models.Icon
import com.example.videoplayers2.models.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Math.abs
import kotlin.properties.Delegates

class ExoPlayerMedia3 : AppCompatActivity(), IconAdapter.OnCLickIconListener,
    GestureDetector.OnGestureListener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    //    private lateinit var playerView: DoubleTapPlayerView
//    private lateinit var ytOverlay: YouTubeOverlay
    lateinit var binding: ActivityExoPlayerMedia3Binding

    private var videoList = ArrayList<VideoModel>()
    private var position = -1
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var controller: ConstraintLayout
    private lateinit var back: ImageFilterView
    private lateinit var title: TextView
    private lateinit var playlist: ImageFilterView
    private lateinit var more: ImageFilterView
    private lateinit var currentPosition: TextView
    private lateinit var progressBar: SeekBar
    private lateinit var duration: TextView
    private lateinit var lock: ImageFilterView
    private lateinit var unlock: ImageFilterView
    private lateinit var audio: ImageFilterView
    private lateinit var previous: ImageFilterView
    private lateinit var next: ImageFilterView
    private lateinit var play: ImageFilterView
    private lateinit var scale: ImageFilterView
    private lateinit var brightness: Button
    private lateinit var volume: Button
//    private lateinit var revine: ConstraintLayout
//    private lateinit var forward: ConstraintLayout

    private lateinit var revineBtn: ImageView
    private lateinit var forwardBtn: ImageView


    private lateinit var recyclerView: RecyclerView
    private lateinit var iconAdapter: IconAdapter
    private lateinit var iconList: ArrayList<Icon>

    private var device_height by Delegates.notNull<Int>()
    private var device_width by Delegates.notNull<Int>()

    private lateinit var gestureDetectorCompat: GestureDetectorCompat
//    private lateinit var trackSelector : TrackSelection

    private var brightnessValue: Int = 0
    private var volumeValue: Int = 0
    private var maxBrightness: Int = 100
    private var maxVolume: Int = 100
    private var repeat = false
    private var mute = false
    private var speed = 1.0f

    private lateinit var audioManager: AudioManager


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        binding = ActivityExoPlayerMedia3Binding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        iconList = ArrayList()
        iconList.add(Icon(R.drawable.round_keyboard_arrow_right_24, ""))
        iconList.add(Icon(R.drawable.round_nights_stay_24, "Night"))
        iconList.add(Icon(R.drawable.round_volume_off_24, "Mute"))
        iconList.add(Icon(R.drawable.round_screen_rotation_24, "Rotate"))
        iconList.add(Icon(R.drawable.round_repeat_24, "Repeat"))
        iconList.add(Icon(R.drawable.round_speed_24, "Speed"))


        initialize()
        receiveBundle()


        // Build the uri item list.
        val uriList = getUriList(videoList)
        // Build the media item list.
        val mediaItemList = getMediaItemList(uriList)


        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        setPlayer(mediaItemList)

        startPlayer()
        playerError()


//        val displayMatrix = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMatrix)

        device_height = Resources.getSystem().displayMetrics.heightPixels
        device_width = Resources.getSystem().displayMetrics.widthPixels

//        playerView.setOnTouchListener{this, motionEvent->
//
//        }


        play.setOnClickListener {
            // Check if the video is playing
            if (player.isPlaying) {
                pausePlayer()
            } else {
                playPlayer()
            }
        }
        next.setOnClickListener {
            nextPlayer()
        }
        previous.setOnClickListener {
            previousPlayer()
        }
        back.setOnClickListener {
            finish()
        }

        lock.setOnClickListener {
            controller.visibility = View.INVISIBLE
            unlock.visibility = View.VISIBLE
            Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show()
        }
        unlock.setOnClickListener {
            controller.visibility = View.VISIBLE
            unlock.visibility = View.INVISIBLE
            Toast.makeText(this, "unlocked", Toast.LENGTH_SHORT).show()
        }

        onSeekBarChangeListener()

//        doubleTapFeature()

        playerView.setOnTouchListener { v, motionEvent ->
            Log.d("TAG", "onCreate: setOnTouchListener")
            gestureDetectorCompat.onTouchEvent(motionEvent)

            doubleTapEnable(motionEvent)

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                brightness.visibility = View.INVISIBLE
                volume.visibility = View.INVISIBLE
                revineBtn.visibility = View.INVISIBLE
                forwardBtn.visibility = View.INVISIBLE
            }
            return@setOnTouchListener false
        }


    }


    private fun doubleTapEnable(motionEvent: MotionEvent) {

        playerView.setOnClickListener(DoubleClickListener(

            object : DoubleClickListener.Callback {
                @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
                override fun singleClicked() {

//                    if (playerView.isControllerFullyVisible) {
//                        playerView.hideController()
//                    } else {
//                        playerView.showController()
//                    }

                }


                @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
                override fun doubleClicked() {
                    val x = motionEvent.x
                    if (x > playerView.width / 2) {
                        Log.d("TAG", "onClick: right")
                        //Right side of the screen
                        playerView.showController()
                        forwardBtn.visibility = View.VISIBLE
                        player.seekTo(player.currentPosition + 10000)

                        //delay of 1 sec
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            forwardBtn.visibility = View.INVISIBLE
                        }

                    } else {
                        Log.d("TAG", "onClick: left")
                        //Left side of the screen
                        playerView.showController()
                        revineBtn.visibility = View.VISIBLE
                        player.seekTo(player.currentPosition - 10000)

                        //delay of 1 sec
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            revineBtn.visibility = View.INVISIBLE
                        }

                    }

                }
            }
        ))

    }

    private fun initialize() {

        playerView = findViewById(R.id.playerView)

        controller = findViewById(R.id.cl_controller)

        back = findViewById(R.id.iv_back)
        title = findViewById(R.id.tv_title)
        playlist = findViewById(R.id.iv_playlist)
        more = findViewById(R.id.iv_more)
        currentPosition = findViewById(R.id.tv_position)
        progressBar = findViewById(R.id.progress_bar)
        duration = findViewById(R.id.tv_duration)
        lock = findViewById(R.id.iv_lock)
        unlock = findViewById(R.id.iv_unlock)
        next = findViewById(R.id.iv_next)
        previous = findViewById(R.id.iv_previous)
        scale = findViewById(R.id.iv_scale)
        play = findViewById(R.id.iv_play)
        brightness = findViewById(R.id.btn_brightness)
        volume = findViewById(R.id.btn_volume)
        audio = findViewById(R.id.iv_audio)
//        forward = findViewById(R.id.fl_forward)
//        revine = findViewById(R.id.fl_rewind)
        forwardBtn = findViewById(R.id.btn_forward)
        revineBtn = findViewById(R.id.btn_rewind)

        recyclerView = findViewById(R.id.rv_icons)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        iconAdapter = IconAdapter(this, iconList, this)
        recyclerView.adapter = iconAdapter
//        iconAdapter.notifyDataSetChanged()

        gestureDetectorCompat = GestureDetectorCompat(this, this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    }

    private fun onSeekBarChangeListener() {
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Do something when the user changes the progress of the ProgressBar
                if (fromUser) {
                    // The user is changing the progress of the SeekBar manually
                    currentPosition.text = createTimeTable(progress.toLong())
                    player.seekTo(progress.toLong())
                } else {
                    // The progress of the SeekBar is changing automatically
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something when the user starts tracking the progress of the ProgressBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something when the user stops tracking the progress of the ProgressBar
                player.seekTo(seekBar.progress.toLong())
            }
        })
    }

    private fun setProgressbar() {
        Log.d("TAG", "setProgressbar: player.duration ${player.duration} ")
        progressBar.max = videoList[position].duration.toInt()
        progressBar.progress = 0
    }

    private fun looper() {
        setRunningTime()
        seekbarProgress()
        setDurationTime()
        handler = Handler()
        runnable = Runnable {
            setRunningTime()
            seekbarProgress()
            setDurationTime()
            handler.postDelayed(runnable, 500)

        }
        handler.postDelayed(runnable, 500)
    }

    private fun setRunningTime() {
        currentPosition.text = createTimeTable(player.currentPosition)
    }

    private fun setDurationTime() {
        val playerDuration = player.duration
        var isDone = false
        if (playerDuration > 0 && !isDone) {
            duration.text = createTimeTable(player.duration)
            progressBar.max = playerDuration.toInt()
            isDone = true
        } else if (playerDuration < 0) {
            duration.text = videoList[position].duration
        }
    }

    private fun seekbarProgress() {
        progressBar.progress = player.currentPosition.toInt()
    }

    private fun setTime() {
        setRunningTime()
        setDurationTime()
    }

    private fun setTitle(name: String) {
        title.text = name
    }

    private fun previousPlayer() {
        if (player.hasPreviousMediaItem()) {
            position = player.previousMediaItemIndex
            player.seekToPrevious()
            setOrientation(videoList[position])
            setTitle(videoList[position].title)
        } else {
            finish()
        }
    }

    private fun nextPlayer() {
        if (player.hasNextMediaItem()) {
            position = player.nextMediaItemIndex
            player.seekToNext()
            setOrientation(videoList[position])
            setTitle(videoList[position].title)
        } else {
            finish()
        }
    }

    private fun playPlayer() {
        // Play the video
        player.play()

        // Change the image of the ImageFilterView to a play icon
        play.setImageResource(R.drawable.round_pause_24)
    }

    private fun pausePlayer() {
        // Pause the video
        player.pause()
        // Change the image of the ImageFilterView to a pause icon
        play.setImageResource(R.drawable.round_play_arrow_24)
    }

    private fun playerError() {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                val cause = error.cause
                if (cause is HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    val httpError = cause
                    // It's possible to find out more about the error both by casting and by querying
                    // the cause.
                    if (httpError is HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code, message
                        // and headers.
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause, although
                        // note that it may be null.
                    }
                }
            }
        })
    }

    private fun setFullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setOrientation(video: VideoModel) {
        val mRetriever = MediaMetadataRetriever()
        mRetriever.setDataSource(video.path)
        val frame = mRetriever.frameAtTime
        val width = frame!!.width
        val height = frame.height
        val it = width.toFloat() / height
        if (1.2 < it) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun startPlayer() {
        Log.d("TAG", "startPlayer: player.duration ${player.duration} ")

        // Bind the player to the view.

        setOrientation(videoList[position])
        setTitle(videoList[position].title)
        setProgressbar()
        setTime()
        looper()
    }

    private fun setPlayer(mediaItemList: ArrayList<MediaItem>) {

        player.setMediaItems(mediaItemList)

        player.seekTo(position, C.TIME_UNSET)
        // Prepare the player.
        player.prepare()
        // Start the playback.
        playPlayer()
    }

    private fun getUriList(videoList: ArrayList<VideoModel>): ArrayList<Uri> {
        val list = ArrayList<Uri>()
        videoList.forEach {
            val uri = Uri.fromFile(File(it.path))
            list.add(uri)
        }
        return list;
    }

    private fun receiveBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            position = bundle.getInt("position")
            val list = bundle.getParcelableArrayList<VideoModel>("videoList")
            if (list != null) {
                Log.d("TAG", "onCreate: list size = ${list.size}")
                videoList = list
            }
        }
    }


    private fun getMediaItemList(uriList: ArrayList<Uri>): ArrayList<MediaItem> {
        val list = ArrayList<MediaItem>()
        uriList.forEach {
            val mediaItem = MediaItem.fromUri(it)
            list.add(mediaItem)
        }
        return list
    }

    private fun createTimeTable(time: Long): String {
        var timetable = ""
        val min = time / 1000 / 60;
        val sec = time / 1000 % 60;
        timetable += "$min:"
        if (sec < 10) {
            timetable += "0"
        }
        timetable += "$sec"
        return timetable
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (player.isPlaying) {
            player.stop()
        }
    }


    override fun onPause() {
        super.onPause()

        player.playWhenReady = false
        player.playbackState

    }

    override fun onResume() {
        super.onResume()
        player.playWhenReady = true
        player.playbackState

        if (brightnessValue != 0) setBrightness(brightnessValue)

    }

    override fun onRestart() {
        super.onRestart()
        player.playWhenReady = true
        player.playbackState
    }

    override fun onIconClick(icon: Icon, holder: IconAdapter.ViewHolder) {

        when (icon.title) {
            "" -> {

            }

            "Night" -> {


            }

            "Mute" -> {
                val currentVolume = volumeValue
                if (mute) {
                    holder.icon.setBackgroundResource(R.drawable.circle)
                    mute = false
                    setVolume(currentVolume)
                } else {
                    holder.icon.setBackgroundResource(R.drawable.circle_selected)
                    mute = true
                    setVolume(0)
                }

            }

            "Rotate" -> {


            }

            "Repeat" -> {
                if (repeat) {
                    repeat = false
                    player.repeatMode = Player.REPEAT_MODE_OFF
                    holder.icon.setBackgroundResource(R.drawable.circle)
                } else {
                    repeat = true
                    player.repeatMode = Player.REPEAT_MODE_ONE
                    holder.icon.setBackgroundResource(R.drawable.circle_selected)
                }
            }

            "Speed" -> {
                pausePlayer()
                showSpeedDialog()


            }
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        Unit
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }


    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
        return false
    }

    override fun onScroll(
        event1: MotionEvent, event2: MotionEvent, distanceX: Float, distanceY: Float
    ): Boolean {
        if (abs(distanceX) < abs(distanceY)) {
            //vertical scroll
            if (event1!!.x < device_width / 2) {
                //brightness
                brightness.visibility = View.VISIBLE
                volume.visibility = View.INVISIBLE

//                val newValue = if (distanceY > 0) {
//                    brightnessValue++
//                } else {
//                    brightnessValue--
//                }
                if (distanceY > 0) {
                    brightnessValue += 1
                } else {
                    brightnessValue -= 1
                }

                if (brightnessValue in 1..maxBrightness) {
                    brightness.text = brightnessValue.toString()
                    setBrightness(brightnessValue)
                }


            } else {
                //volume
                brightness.visibility = View.INVISIBLE
                volume.visibility = View.VISIBLE

                if (distanceY > 0) {
                    volumeValue += 1
                } else {
                    volumeValue -= 1
                }
                Log.d("TAG", "onScroll: ")
                if (volumeValue in 1..maxVolume) {
                    volume.text = volumeValue.toString()
                    setVolume((volumeValue * 15) / maxVolume)
                }

            }

        } else {
            //horizontal scroll


        }
        return true
    }

    fun setBrightness(value: Int) {
        val d = 1.0f / maxBrightness
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
    }

    fun setVolume(volume: Int) {
        // Set the volume to the specified value.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    fun showSpeedDialog() {
        val inflater = LayoutInflater.from(this)

        // Inflate the dialog layout
        val customDialogS = inflater.inflate(R.layout.speed_layout, null)

        // Create a dialog builder
        val builder = AlertDialog.Builder(this)

        // Set the dialog view
        builder.setView(customDialogS)

        // Create and show the dialog
        builder.create().show()

        // Get the buttons from the dialog
        val display = customDialogS.findViewById<TextView>(R.id.tv_speed_display)
        val btnDecrease = customDialogS.findViewById<AppCompatImageButton>(R.id.btn_decrease)
        val btnIncrease = customDialogS.findViewById<AppCompatImageButton>(R.id.btn_increase)

        // Set an onclick listener for the decrease button
        btnDecrease.setOnClickListener {
            // Decrease the video playback speed


            if (speed in 1.0..2.0) {
                val temp = (speed - 0.5).toFloat()
                speed = temp
                display.text = "${speed}X"
                player.setPlaybackSpeed(speed)
            }
        }

        // Set an onclick listener for the increase button
        btnIncrease.setOnClickListener {
            // Increase the video playback speed

            if (speed in 0.5..1.5) {
                val temp = (speed + 0.5).toFloat()
                speed = temp
                display.text = "${speed}X"
                player.setPlaybackSpeed(speed)
            }

        }

        if (speed != 1.0f) {
            display.text = "${speed}X"
        }


        builder.setOnDismissListener {
            Toast.makeText(this, "setOnDismissListener", Toast.LENGTH_SHORT).show()
            playPlayer()
        }
        builder.setOnCancelListener {
            Toast.makeText(this, "setOnCancelListener", Toast.LENGTH_SHORT).show()
            playPlayer()

        }

    }
}
