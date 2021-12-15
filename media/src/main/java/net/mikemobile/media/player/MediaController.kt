package net.mikemobile.android.music

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import net.mikemobile.media.MediaInfo


interface OnMediaControllerListener {
    fun onMediaData(info: MediaInfo, duration: Int)
    fun onStatus(status: CustomMusicPlayer.Companion.STATUS)
    fun onVideoStatus(width: Int, height: Int)
    fun onComplete()
}
/**
 * MediaPlayerクラス、再生指定された音楽リスト、ループ処理、ランダム処理、再生曲のデータ、再生ポイントを制御するクラス
 *
 *
 */
class MediaController(val context: Context, var listener: OnMediaControllerListener? = null): OnMediaListener {

    companion object {
        const val TAG = "MediaController"


        const val MEDIA_TYPE_NONE = 0
        const val MEDIA_TYPE_MUSIC = 1
        const val MEDIA_TYPE_MOVIE = 2

    }
    var musicPlayer = MediaPlayerManager(context).setOnMediaPlayerManagerListener(object: OnMediaPlayerManagerListener{
        override fun onStatus(status: CustomMusicPlayer.Companion.STATUS, mp: CustomMusicPlayer) {
            listener?.onStatus(status)

            if (status == CustomMusicPlayer.Companion.STATUS.LOAD_COMPLETE) {
                if (point > -1 && mediaList.size > point) {
                    listener?.onMediaData(mediaList[point], mp.duration)
                }
            }
        }

        override fun onVideoStatus(mp: CustomMusicPlayer, width: Int, height: Int) {
            listener?.onVideoStatus(width, height)
            if (point > -1 && mediaList.size > point) {
                listener?.onMediaData(mediaList[point], mp.duration)
            }
        }

        override fun onComplete() {
            listener?.onComplete()

            Log.i(TAG, "onComplete() mRandom:" + mRandom)
            Log.i(TAG, "onComplete() mLoop:" + mLoop)
            Log.i(TAG, "onComplete() type:" + type)
            if (mRandom) {
                // ランダムが有効
            } else {
                if (mLoop == MediaPlayerManager.Companion.LOOP.ONE) {
                    setSeek(0)
                    if (type == MEDIA_TYPE_MUSIC) {
                        onPlay()
                    } else if (type == MEDIA_TYPE_MOVIE) {
                        onPlayVideo()
                    }
                } else if (mLoop == MediaPlayerManager.Companion.LOOP.ALL) {
                    Log.i(TAG, "onComplete() size:" + mediaList.size)
                    Log.i(TAG, "onComplete() point:" + point)
                    if (mediaList.size - 1 > point) {
                        val next = point + 1
                        Log.i(TAG, "onComplete() next:" + next)
                        if (type == MEDIA_TYPE_MUSIC) {
                            onPlay(next)
                        } else if (type == MEDIA_TYPE_MOVIE) {
                            onPlayVideo(next)
                        }
                    } else {
                        val next = 0
                        Log.i(TAG, "onComplete() next:" + next)
                        if (type == MEDIA_TYPE_MUSIC) {
                            onPlay(next)
                        } else if (type == MEDIA_TYPE_MOVIE) {
                            onPlayVideo(next)
                        }
                    }
                } else {
                    Log.i(TAG, "onComplete() size:" + mediaList.size)
                    Log.i(TAG, "onComplete() point:" + point)
                    if (mediaList.size - 1 > point) {
                        val next = point + 1
                        Log.i(TAG, "onComplete() next:" + next)
                        if (type == MEDIA_TYPE_MUSIC) {
                            onPlay(next)
                        } else if (type == MEDIA_TYPE_MOVIE) {
                            onPlayVideo(next)
                        }
                    }
                }
            }
        }
    })

    var type:Int = MEDIA_TYPE_NONE

    var point = -1
    var mLoop = MediaPlayerManager.Companion.LOOP.NONE
    var mRandom = false

    var mediaList = ArrayList<MediaInfo>()

    override fun isPlay(): Boolean {
        return musicPlayer.isPlay()
    }

    /**
     * 次の曲の再生処理
     */
    override fun onPlayNext(): Boolean {

        if (mediaList.size > 0) {
            val next = point + 1
            if (type == MEDIA_TYPE_MUSIC) {
                Log.i(TAG, "onPlayNext() 音楽")
                if (mediaList.size > next) {
                    return onPlay(next)
                } else if(mLoop != MediaPlayerManager.Companion.LOOP.NONE){
                    return onPlay(0)
                }
            } else if (type == MEDIA_TYPE_MOVIE) {
                Log.i(TAG, "onPlayNext() ビデオ")
                if (mediaList.size > next) {
                    return onPlayVideo(next)
                } else if(mLoop != MediaPlayerManager.Companion.LOOP.NONE){
                    return onPlayVideo(0)
                }
            }

        }
        return false
    }

    /**
     * 前の曲の再生処理
     */
    override fun onPlayPrev(): Boolean {
        Log.i(TAG, "onPlayPrev()")
        if (mediaList.size > 0) {
            val prev = point - 1

            if (type == MEDIA_TYPE_MUSIC) {
                if (0 <= prev) {
                    return onPlay(prev)
                } else if(mLoop != MediaPlayerManager.Companion.LOOP.NONE){
                    return onPlay(mediaList.size - 1)
                }
            } else if (type == MEDIA_TYPE_MOVIE) {
                if (0 <= prev) {
                    return onPlayVideo(prev)
                } else if(mLoop != MediaPlayerManager.Companion.LOOP.NONE){
                    return onPlayVideo(mediaList.size - 1)
                }
            }

        }
        return false
    }

    /**
     * リストから曲を指定しての再生処理
     */
    override fun onPlay(position: Int): Boolean {
        Log.i(TAG, "onPlay("+position+")")

        var uri: Uri? = null
        if (mediaList.size > 0 && mediaList.size > position) {
            uri = mediaList[position].data
        }

        if (uri != null) {
            point = position
            return play(uri)
        }

        return false
    }

    override fun onPlay(): Boolean {
        Log.i(TAG, "onPlay")
        return play(null)
    }

    /**
     * 曲データを直接指定しての再生処理
     */
    override fun onPlay(uri: Uri): Boolean {
        Log.i(TAG, "onPlay")
        val list = ArrayList<MediaInfo>()
        list.add(MediaInfo(uri))
        mediaList = list

        return play(uri)
    }

    override fun onPlayVideo(): Boolean {
        Log.i(TAG, "onPlayVideo")
        return playVideo(null)
    }

    override fun onPlayVideo(position: Int): Boolean {
        Log.i(TAG, "onPlayVideo("+position+")")

        var uri: Uri? = null
        if (mediaList.size > 0 && mediaList.size > position) {
            uri = mediaList[position].data
        }

        if (uri != null) {
            point = position
            return playVideo(uri)
        }

        return false
    }

    override fun onPlayVideo(uri: Uri): Boolean {
        Log.i(TAG, "onPlay")
        val list = ArrayList<MediaInfo>()
        list.add(MediaInfo(uri))
        mediaList = list

        return playVideo(uri)
    }

    /**
     * 一時停止
     */
    override fun onPause() {
        Log.i(TAG, "onPause")
        musicPlayer.pause()
    }

    /**
     * 停止
     */
    override fun onStop() {
        Log.i(TAG, "onStop")
        musicPlayer.stop()
    }

    override fun setSurface(holder: SurfaceHolder?) {
        Log.i(TAG + " MovieFragmentTAG", "setSurface")

        try {
            musicPlayer.setDisplay(holder)

        } catch (e: Exception) { /* 省略 */
            Log.e(TAG, "setSurface e:" + e.toString())
        }
    }

    override fun getSeek(): Int {
        musicPlayer?.let {
            return it.getSeek()
        }

        return 0
    }

    override fun setSeek(time: Int) {
        musicPlayer?.let {
            it.setSeek(time)
        }
    }

    override fun getDuration(): Int {
        musicPlayer?.let {
            return it.getDuration()
        }

        return 0
    }

    /**
     * リストの設定
     */
    override fun setList(list: ArrayList<MediaInfo>) {
        mediaList = list
    }

    override fun getList(): ArrayList<MediaInfo> {
        return mediaList
    }

    override fun getData(): MediaInfo? {
        if (point > -1 && mediaList.size > 0 && mediaList.size > point) {
            return mediaList[point].copy()
        }
        return null
    }

    override fun getLoop(): MediaPlayerManager.Companion.LOOP {
        return mLoop
    }
    override fun setLoop(loop: MediaPlayerManager.Companion.LOOP) {
        mLoop = loop
    }

    override fun getRandom(): Boolean {
        return mRandom
    }
    override fun setRandom(random: Boolean) {
        mRandom = random
    }

    override fun getMediaType(): Int {
        return type
    }

    override fun getMediaTypeMusic(): Boolean {
        return (type == MEDIA_TYPE_MUSIC)
    }

    override fun getMediaTypeVideo(): Boolean {
        return (type == MEDIA_TYPE_MOVIE)
    }

    override fun getPosition(): Int {
        return point
    }

    override fun setLoop(loop: Boolean) {
        musicPlayer.loop = loop
    }

    override fun setOnMediaControllerListener(l: OnMediaControllerListener?) {
        Log.i(TAG + " MovieFragmentTAG", "setOnMediaControllerListener")
        listener = l

        if (point == -1 || mediaList.size <= point) {
        }else if (musicPlayer.isPlay() || musicPlayer.isPlayFlg) {
            listener?.onMediaData(mediaList[point], musicPlayer.getDuration())
        }
    }

    private fun play(uri: Uri?): Boolean {
        try {
            if (uri == null) {
                Log.i(TAG, "play to uri null")
                musicPlayer.play()
            } else {
                Log.i(TAG, "play to uri not null")
                musicPlayer.play(uri)
            }
            type = MEDIA_TYPE_MUSIC
            return true
        }catch(e: Exception) {
            Log.e(TAG, "play error:$e")
        }
        return false
    }

    private fun playVideo(uri: Uri?): Boolean {
        try {
            if (uri == null) {
                Log.i(TAG, "playVideo to uri null")
                musicPlayer.play()
            } else {
                Log.i(TAG, "playVideo to uri not null")
                musicPlayer.playVideo(uri)
            }
            type = MEDIA_TYPE_MOVIE
            return true
        }catch(e: Exception) {
            Log.e(TAG, "play error:$e")
        }
        return false
    }

}

