package net.mikemobile.android.music

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import java.io.IOException


interface OnMediaPlayerManagerListener {
    fun onStatus(status: CustomMusicPlayer.Companion.STATUS, mp: CustomMusicPlayer)
    fun onVideoStatus(mp: CustomMusicPlayer, width: Int, height: Int)
    fun onComplete()
}
class MediaPlayerManager(val context: Context) {

    companion object {
        const val TAG = "MusicPlayerManager"

        enum class LOOP {
            NONE,
            ONE,
            ALL,
        }
    }

    val STREAM_TYPE = AudioManager.STREAM_MUSIC

    var musicUri: Uri? = null
    var isPlayFlg = false
    var seekPosition = 0
    var holder: SurfaceHolder? = null

    var loop = false

    private var mp = CustomMusicPlayer(context)


    val defaultListener = object: OnPlayStatusListener {

        override fun onStatus(status: CustomMusicPlayer.Companion.STATUS) {
            Log.v(TAG, "onStatus : " + status)
            musiclistener?.onStatus(status, mp)
            if (status == CustomMusicPlayer.Companion.STATUS.LOAD_COMPLETE) {
                isPlayFlg = true
            }
        }

        override fun onStart(mp: CustomMusicPlayer) {
            Log.v(TAG, "onStart")
            musiclistener?.onVideoStatus(mp, mp.videoWidth, mp.videoHeight)
        }

        override fun onPause(mp: CustomMusicPlayer) {
            Log.v(TAG, "onPause")
        }

        override fun onStop(mp: CustomMusicPlayer) {
            Log.v(TAG, "onStop")
        }

        override fun onLoading(mp: CustomMusicPlayer) {
            Log.v(TAG, "onLoading")
        }

        override fun onComplete(mp: CustomMusicPlayer) {
            Log.v(TAG, "onComplete")
            musiclistener?.onComplete()
        }

        override fun onError(mp: CustomMusicPlayer, what: Int, extra: Int, error: String) {
            Log.v(TAG, "onError : " + error)
        }
    }

    private var musiclistener: OnMediaPlayerManagerListener? = null
    fun isPlay(): Boolean {
        return mp.isPlaying
    }
    fun play() {
        Log.v(TAG, "play() isPlay:" + isPlay())
        mp.start()
    }

    fun play(uri: Uri) {
        Log.v(TAG, "play("+uri.path+") isPlay:" + isPlay())
        Log.v(TAG, "play =========================")

        try {
            //音楽が再生中なら停止する
            mp.setOnPlayStatusListener(null)
            mp.reset()

        } catch (e: Exception) {
            Log.e(TAG, "キャッチされたのでスルーしておく")
            Log.e(TAG, "" + e.toString())
        }

        try {
            Log.v(TAG, "プレイヤーの初期化")
            isPlayFlg = false
            //プレイヤーの初期化
            mp = CustomMusicPlayer(context)
            mp.setOnPlayStatusListener(defaultListener)

            //再生完了時のリスナーを宣言する
            mp.isLooping = loop
            mp.setUriData(context, uri)
            //音を再生するタイプを指定する
            mp.setAudioStreamType(STREAM_TYPE)
            mp.setVolume(0.5f, 0.5f)


            Log.v(TAG, "セット完了")

            //指定されたパスが現在再生のものと同一の場合はこちらの処理
            mp.seekTo(0)
            mp.start()


            Log.v(TAG, "再生")
            musicUri = uri
            return
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "IllegalArgumentException : $e")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException : $e")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException : $e")
        } catch (e: IOException) {
            Log.e(TAG, "IllegalStateException : $e")
        }
    }

    fun playVideo(uri: Uri) {
        Log.v(TAG, "playVideo("+uri.path+") isPlay:" + isPlay())
        Log.v(TAG, "playVideo =========================")

        try {
            //音楽が再生中なら停止する

            //指定されたパスが現在再生のものと同一の場合はこちらの処理
            mp.setDisplay(null)
            mp.setOnPlayStatusListener(null)
            mp.reset()

        } catch (e: Exception) {
            Log.e(TAG, "キャッチされたのでスルーしておく")
            Log.e(TAG, "" + e.toString())
        }

        try {
            Log.v(TAG, "プレイヤーの初期化")


            //プレイヤーの初期化
            mp = CustomMusicPlayer(context)
            mp.setOnPlayStatusListener(defaultListener)

            //再生完了時のリスナーを宣言する
            mp.isLooping = false
            mp.setUriData(context, uri)
            //音を再生するタイプを指定する
            mp.setAudioStreamType(STREAM_TYPE)
            mp.setVolume(0.5f, 0.5f)


            //指定されたパスが現在再生のものと同一の場合はこちらの処理
            if (holder != null) {
                Log.v(TAG, "SerfaceHodlerのセット")
                mp.setDisplay(holder)
            }
            Log.v(TAG, "セット完了")

            mp.seekTo(0)
            mp.start()
            Log.v(TAG, "再生")
            isPlayFlg = true
            musicUri = uri
            return
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "IllegalArgumentException : $e")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException : $e")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException : $e")
        } catch (e: IOException) {
            Log.e(TAG, "IllegalStateException : $e")
        }
    }

    fun pause() {
        Log.v(TAG, "pause  isPlayFlg:" + isPlayFlg)
        Log.v(TAG, "pause =========================================================")
        try {
            seekPosition = mp.currentPosition
            mp.pause()
            isPlayFlg = false
        } catch (e: Exception) {
            Log.e(TAG, "pause " + e.toString())
        }
    }

    fun stop() {
        Log.v(TAG, "stop  isPlayFlg:" + isPlayFlg)
        Log.v(TAG, "stop =========================================================")
        //停止 ===========================================
        try {
            mp.stop()
            mp.reset()
            seekPosition = 0
            isPlayFlg = false
        } catch (e: Exception) {
            Log.e(TAG, "stop " + e.toString())
        }
    }

    fun getDuration(): Int {
        return mp.duration
    }

    fun getSeek(): Int {
        return mp.currentPosition
    }

    fun setSeek(time: Int) {
        mp.seekTo(time)
    }

    fun setOnMediaPlayerManagerListener(l: OnMediaPlayerManagerListener?): MediaPlayerManager {
        musiclistener = l
        return this
    }

    fun setDisplay(_holder: SurfaceHolder?) {

        Log.v(TAG + " MovieFragmentTAG", "stop  setDisplay()")

        holder = _holder
        if (mp != null && _holder != null) {
            Log.v(TAG + " MovieFragmentTAG", "mp not null")
            mp.setDisplay(_holder)
            musiclistener?.onVideoStatus(mp, mp.videoWidth, mp.videoHeight)
        } else {
            Log.e(TAG + " MovieFragmentTAG", "mp is null")
        }
    }
}