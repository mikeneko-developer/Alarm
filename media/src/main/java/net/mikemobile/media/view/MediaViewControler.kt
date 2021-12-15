package net.mikemobile.android.video

import android.content.Context
import android.graphics.Bitmap
import android.widget.SeekBar.OnSeekBarChangeListener
import android.view.MotionEvent
import android.util.Log
import android.view.View
import android.widget.*
import net.mikemobile.android.music.MediaPlayerManager
import net.mikemobile.media.R
import net.mikemobile.media.system.DateUtil
import net.mikemobile.media.view.TimerManager

interface OnMediaViewControlerListener {
    fun onPlay(): Boolean
    fun onPrev()
    fun onNext()
    fun onSeek(): Int
    fun onData()
    fun onChangeSeek(time: Int)
    fun onRepeat(repeat: MediaPlayerManager.Companion.LOOP)
    fun onRandom(random:Boolean)
    fun onFavorite():Boolean

}

abstract class MediaViewControler(
    private val context: Context,
    private val view: View,
    private val map: HashMap<VIEW_ID_KEY, Int>,
    private val listener: OnMediaViewControlerListener
) {
    private var seekBar: SeekBar? = null
    private var button: ImageButton? = null
    private var btn_prev: ImageButton? = null
    private var btn_next: ImageButton? = null
    private var tv_min_time: TextView? = null
    private var tv_max_time: TextView? = null
    private var image: ImageView? = null
    private var btn_loop: ImageButton? = null
    private var btn_random: ImageButton? = null
    private var btn_favorite: ImageButton? = null
    private var tv_play_title: TextView? = null
    private var tv_play_artist: TextView? = null
    private var tv_play_album: TextView? = null

    private var isPlayed = false
    private var isSeekTouched = false


    private var repeat = MediaPlayerManager.Companion.LOOP.NONE
    private var random = false
    companion object {
        const val TAG = "MediaViewControler"

        enum class VIEW_ID_KEY {
            PLAY_TIME_TEXT_VIEW,
            DURATION_TIME_TEXT_VIEW,
            SEEK_BAR,
            PLAY_BUTTON,
            PREV_BUTTON,
            NEXT_BUTTON,
            ART_WORK_IMAGE,
            REPEAT_BUTTON,
            SHUFFLE_BUTTON,
            FAVORITE_BUTTON,
            TITLE_TEXT_VIEW,
            ARTIST_TEXT_VIEW,
            ALBUM_TEXT_VIEW,
        }
    }

    init {
        setup()
    }

    private fun findViewById(id: Int): View {
        return view.findViewById(id)
    }

    private fun setup() {
        if(map.containsKey(VIEW_ID_KEY.PLAY_TIME_TEXT_VIEW)) {
            tv_min_time = findViewById(map[VIEW_ID_KEY.PLAY_TIME_TEXT_VIEW]!!) as TextView
        }

        if(map.containsKey(VIEW_ID_KEY.DURATION_TIME_TEXT_VIEW)) {
            tv_max_time = findViewById(map[VIEW_ID_KEY.DURATION_TIME_TEXT_VIEW]!!) as TextView
        }

        if(map.containsKey(VIEW_ID_KEY.SEEK_BAR)) {
            seekBar = findViewById(map[VIEW_ID_KEY.SEEK_BAR]!!) as SeekBar
        }

        if(map.containsKey(VIEW_ID_KEY.PLAY_BUTTON)) {
            button = findViewById(map[VIEW_ID_KEY.PLAY_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.PREV_BUTTON)) {
            btn_prev = findViewById(map[VIEW_ID_KEY.PREV_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.NEXT_BUTTON)) {
            btn_next = findViewById(map[VIEW_ID_KEY.NEXT_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.ART_WORK_IMAGE)) {
            image = findViewById(map[VIEW_ID_KEY.ART_WORK_IMAGE]!!) as ImageView
        }

        if(map.containsKey(VIEW_ID_KEY.REPEAT_BUTTON)) {
            btn_loop = findViewById(map[VIEW_ID_KEY.REPEAT_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.SHUFFLE_BUTTON)) {
            btn_random = findViewById(map[VIEW_ID_KEY.SHUFFLE_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.FAVORITE_BUTTON)) {
            btn_favorite = findViewById(map[VIEW_ID_KEY.FAVORITE_BUTTON]!!) as ImageButton
        }

        if(map.containsKey(VIEW_ID_KEY.TITLE_TEXT_VIEW)) {
            tv_play_title = findViewById(map[VIEW_ID_KEY.TITLE_TEXT_VIEW]!!) as TextView
        }

        if(map.containsKey(VIEW_ID_KEY.ARTIST_TEXT_VIEW)) {
            tv_play_artist = findViewById(map[VIEW_ID_KEY.ARTIST_TEXT_VIEW]!!) as TextView
        }

        if(map.containsKey(VIEW_ID_KEY.ALBUM_TEXT_VIEW)) {
            tv_play_album = findViewById(map[VIEW_ID_KEY.ALBUM_TEXT_VIEW]!!) as TextView
        }

        btn_loop?.setOnClickListener { repeat() }
        btn_random?.setOnClickListener { random() }
        btn_favorite?.setOnClickListener { favorite(listener.onFavorite()) }

        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (isSeekTouched) {
                    listener.onChangeSeek(i)
                    setSeekBar(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekBar?.setOnTouchListener { view, motionEvent ->
            var text = ""
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    text = "ACTION_DOWN"
                    isSeekTouched = true
                }
                MotionEvent.ACTION_MOVE -> text = "ACTION_MOVE"
                MotionEvent.ACTION_UP -> {
                    text = "ACTION_UP"
                    isSeekTouched = false
                }
                MotionEvent.ACTION_CANCEL -> {
                    text = "ACTION_CANCEL"
                    isSeekTouched = false
                }
            }
            Log.i(TAG, "Action:$text")
            false
        }

        button?.setOnClickListener { play() }
        btn_prev?.setOnClickListener { listener.onPrev() }
        btn_next?.setOnClickListener { listener.onNext() }
        //videoView.setOnCompletionListener { stop() }
        changePlayButton(false)
    }

    fun changePlayButton(flg: Boolean) {
        button?.let {
            it.tag = flg
            if (flg) {
                it.setImageResource(R.drawable.ic_pause)
                startTimer()
            } else {
                it.setImageResource(R.drawable.ic_play_arrow)
                stopTimer()
            }
        }
    }

    private fun repeat() {
        if (repeat == MediaPlayerManager.Companion.LOOP.NONE) {
            repeat = MediaPlayerManager.Companion.LOOP.ALL
        } else if(repeat == MediaPlayerManager.Companion.LOOP.ALL) {
            repeat = MediaPlayerManager.Companion.LOOP.ONE
        } else {
            repeat = MediaPlayerManager.Companion.LOOP.NONE
        }
        setRepeat(repeat)
    }

    fun setRepeat(repeat: MediaPlayerManager.Companion.LOOP) {
        if (repeat == MediaPlayerManager.Companion.LOOP.ALL) {
            btn_loop?.imageAlpha = 255
            btn_loop?.setImageResource(R.drawable.ic_repeat)
        } else if (repeat == MediaPlayerManager.Companion.LOOP.ONE) {
            btn_loop?.setImageResource(R.drawable.ic_repeat_one)
        } else {
            btn_loop?.imageAlpha = 100
            btn_loop?.setImageResource(R.drawable.ic_repeat)
        }

        listener.onRepeat(repeat)
    }

    fun random() {
        random = !random
        setRandom(random)
    }

    fun setRandom(random: Boolean) {
        if (random) {
            btn_random?.imageAlpha = 255
            btn_random?.setImageResource(R.drawable.ic_shuffle)
        } else {
            btn_random?.imageAlpha = 100
            btn_random?.setImageResource(R.drawable.ic_shuffle)
        }
        listener.onRandom(random)
    }

    fun favorite(favorite: Boolean) {
        if (favorite) {
            btn_favorite?.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            btn_favorite?.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }


    private fun play() {
        Log.i(TAG, "play")
        changePlayButton(listener.onPlay())
    }

    fun stop() {
        Log.i(TAG, "stop")
        button?.setImageResource(R.drawable.ic_play_arrow)
        stopTimer()
    }

    fun onPause() {
        stopTimer()
    }


    fun setSeekBar(time: Int) {
        seekBar?.progress = time
        tv_min_time?.text = DateUtil.getTimeString(DateUtil.TIME_MS,time.toLong())
    }

    fun setDuration(max: Int) {
        seekBar?.max = max
        tv_max_time?.text = DateUtil.getTimeString(DateUtil.TIME_MS,max.toLong())
    }

    /////////////

    fun setImageBitmap(bitmap: Bitmap?){
        if (bitmap != null) {
            image?.setImageBitmap(bitmap)
        } else {
            image?.setImageResource(R.drawable.ic_music_note)
        }
    }

    fun setPlayData(title: String, artist: String, album: String) {
        tv_play_title?.text = title
        tv_play_artist?.text = "Artist:" + artist
        tv_play_album?.text =  "Album:" + album
    }



    //////////////

    private var timer: TimerManager? = null
    private fun startTimer() {
        listener.onData()

        timer = object : TimerManager() {
            override fun onCountup(count: Int, time: Long) {
                if (!isSeekTouched) {
                    val seek = listener.onSeek()
                    setSeekBar(seek)
                }
            }
        }
        timer?.start()
    }

    private fun stopTimer() {
        timer?.let {
            it.stop()
            timer = null
        }
        val seek = listener.onSeek()
        setSeekBar(seek)
    }
}