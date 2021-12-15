package net.mikemobile.android.music

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnErrorListener
import android.net.Uri
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

interface OnPlayStatusListener {
    fun onStatus(satus: CustomMusicPlayer.Companion.STATUS)
    fun onStart(mp: CustomMusicPlayer)
    fun onPause(mp: CustomMusicPlayer)
    fun onStop(mp: CustomMusicPlayer)
    fun onLoading(mp: CustomMusicPlayer)
    fun onComplete(mp: CustomMusicPlayer)
    fun onError(mp: CustomMusicPlayer, what: Int, extra: Int, error: String)
}

class CustomMusicPlayer(val context: Context): MediaPlayer(), OnErrorListener,
    MediaPlayer.OnCompletionListener {

    companion object {
        val TAG = "CustomMusicPlayer"

        enum class STATUS {
            NONE,
            LOADING,
            LOAD_COMPLETE,
            PLAY,
            STOP,
            PAUSE,
            COMPLETE,
            ERROR,

            NEXT,
        }
    }

    var loading = false
    var status = STATUS.NONE

    init {
        setOnErrorListener(this)
        setOnPreparedListener { mp ->
            Log.w(TAG, "ローディング完了")
            statuslistener?.onStatus(STATUS.LOAD_COMPLETE)
            loading = false

            Log.w(TAG, "ローディング-Play")
            mp.start()
            status = STATUS.PLAY
            statuslistener?.onStart(this)
            statuslistener?.onStatus(STATUS.PLAY)
        }
    }

    private var statuslistener: OnPlayStatusListener? = null
    fun setOnPlayStatusListener(_listener: OnPlayStatusListener?) {
        statuslistener = _listener
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        statuslistener?.onError(this, what, extra, "")
        statuslistener?.onStatus(STATUS.ERROR)
        return false
    }

    override fun start() {
        Log.w(TAG, "start()")
        super.start()
        if (status != STATUS.LOADING) {
            statuslistener?.onStart(this)
            statuslistener?.onStatus(STATUS.PLAY)
        }
    }

    override fun pause() {
        Log.w(TAG, "pause()")
        super.pause()
        statuslistener?.onPause(this)
        statuslistener?.onStatus(STATUS.PAUSE)
    }

    override fun stop() {
        Log.w(TAG, "stop()")
        super.stop()
        statuslistener?.onStop(this)
        statuslistener?.onStatus(STATUS.STOP)
    }

    override fun reset() {
        Log.w(TAG, "reset()")
        super.reset()
        setOnCompletionListener(null)
        setOnErrorListener(null)
        setOnPreparedListener(null)
    }

    var time = -1L
    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        Log.w(TAG, "onCompletion()")
        val this_time = getTimeMillis()

        if((this_time - time) > 100) {
            time = this_time
            statuslistener?.onComplete(this)
            statuslistener?.onStatus(STATUS.COMPLETE)
        } else {
            Log.e(TAG, "onCompletion() 差分:" + (this_time - time))
            time = this_time
        }
    }

    /**
     * 現在のミリ秒を取得する
     * @return ミリ秒を返します
     */
    fun getTimeMillis(): Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }


    fun setUriData(context: Context?, uri: Uri?) {
        Log.w(TAG, "setUriData()")
        if (isPlaying) {
            stop()
        }
        var error = ""
        try {
            setOnErrorListener(OnErrorListener { mp, what, extra ->
                if (extra == MEDIA_ERROR_SERVER_DIED
                    || extra == MEDIA_ERROR_MALFORMED
                ) {
                    Log.e(TAG, "erroronplaying")
                } else if (extra == MEDIA_ERROR_IO) {
                    Log.e(TAG, "erroronplaying")
                    return@OnErrorListener false
                }
                false
            })
            setOnBufferingUpdateListener { mp, percent -> Log.i(TAG, "" + percent) }
            this.setDataSource(context!!, uri!!)

            setOnCompletionListener(this)
            prepare() //これは予定調和でやっとくもの
            status = STATUS.LOADING
            statuslistener?.onLoading(this)
            statuslistener?.onStatus(STATUS.LOADING)

            Log.w(TAG, "setUriData() 設定完了")
            return
        } catch (e: IllegalArgumentException) {
            error = e.toString()
        } catch (e: IllegalStateException) {
            error = e.toString()
        } catch (e: FileNotFoundException) {
            error = e.toString()
        } catch (e: IOException) {
            error = e.toString()
        } catch (e: Exception) {
            error = e.toString()
        }
        Log.e(TAG, "setUriData() エラー:$error")
        status = STATUS.ERROR
        statuslistener?.onError(this, 0, 0, error)
        statuslistener?.onStatus(STATUS.ERROR)
    }
}