package net.mikemobile.android.music

import android.net.Uri
import android.view.SurfaceHolder
import net.mikemobile.media.MediaInfo

interface OnMediaListener {
    fun setList(list: ArrayList<MediaInfo>)

    fun getLoop(): MediaPlayerManager.Companion.LOOP
    fun setLoop(loop: MediaPlayerManager.Companion.LOOP)
    fun getRandom(): Boolean
    fun setRandom(random: Boolean)

    fun getMediaType(): Int
    fun getMediaTypeMusic(): Boolean
    fun getMediaTypeVideo(): Boolean

    fun getList(): ArrayList<MediaInfo>
    fun getData(): MediaInfo?
    fun getPosition(): Int
    fun isPlay(): Boolean

    fun onPlay(position: Int):Boolean
    fun onPlay():Boolean
    fun onPlayVideo(position: Int):Boolean
    fun onPlayVideo():Boolean
    fun onPlayNext():Boolean
    fun onPlayPrev():Boolean

    fun onPlay(uri: Uri): Boolean
    fun onPause()
    fun onStop()

    fun setLoop(loop: Boolean)

    fun onPlayVideo(uri: Uri): Boolean
    fun setSurface(holder: SurfaceHolder?)

    fun getSeek(): Int
    fun getDuration(): Int

    fun setSeek(time: Int)

    fun setOnMediaControllerListener(l: OnMediaControllerListener?)
}