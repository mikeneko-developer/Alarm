package net.mikemobile.sampletimer.music

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Vibrator
import android.util.Log
import net.mikemobile.alarm.util.StrageUtil
import net.mikemobile.alarm.util.music.MyMusicPlayer
import net.mikemobile.alarm.util.sound.MyVolume
import java.io.IOException

class MusicController(var context: Context, var music_path:String){

    companion object {
        const val TAG = "MusicController"
    }

    var music_uri: Uri? = null

    //val STREAM_TYPE = AudioManager.STREAM_MUSIC
    val STREAM_TYPE = MyVolume.SELECT_TYPE

    private var vibrator: Vibrator? = null
    private var mp: MyMusicPlayer? = null
    private val musiclistener = MyMusicListener()

    fun isPlay(): Boolean {
        if(mp != null && mp!!.isMyPlaying){
            return true
        }
        return false
    }

    fun play(){
        Log.v(TAG, "playMusic =========================")

        val path3 = StrageUtil.getExternalPath(context)
        var path = path3 + "/music.mp3"

        path = music_path

        Log.v(TAG, "path:"+path)
        try {
            //音楽が再生中なら停止する

            if(mp == null){

            }else {
                mp?.let{mp ->
                    if (mp.isMyPlaying()) {

                        Log.v(TAG, "曲が再生中なので停止する")
                        mp.seekTo(0)
                        mp.reset()

                        //再生中なら一旦停止する
                        release()
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "キャッチされたのでスルーしておく")
            Log.e(TAG, "" + e.toString())
        }

        try {
            Log.v(TAG, "プレイヤーの初期化")

            //プレイヤーの初期化
            mp = MyMusicPlayer()

            //再生完了時のリスナーを宣言する
            //mp.setOnCompletionListener(this);
            mp!!.setOnMyCompletionListener(musiclistener)
            mp!!.setOnPlayStatusListener(musiclistener)
            //player.setMyTag(select);
            //ループ設定を指定する
            mp!!.isLooping = true
            //曲データのパスを代入する
            //mp.setDataSource(path);
            //Uri uri = Uri.parse(path);
            //player.setDataSource(this, uri);
            //File file = new File(path);
            //player.setData(new FileInputStream(new File(path)).getFD());
            mp!!.setUriData(context, path)
            //音を再生するタイプを指定する
            mp!!.setAudioStreamType(STREAM_TYPE)

            //player.prepare();//これは予定調和でやっとくもの
            mp!!.setVolume(0.5f, 0.5f)
            //再生位置を初期化する
            //seekPoint = 0;
            //player.seekTo(0);
            Log.v(TAG, "セット完了")

            if (mp != null && !mp!!.isMyPlaying) {
                //指定されたパスが現在再生のものと同一の場合はこちらの処理
                mp!!.Play()

                Log.v(TAG, "再生")

                //val pattern = item.getVibPattern()

                //if (pattern != null) {
                //    vibrator.vibrate(pattern, 0)
                //}
            }

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
        Log.v(TAG, "stop =========================================================")
        try {
            mp?.let{mp ->
                if (mp.isMyPlaying()) {
                    mp.Pause()
                }
            }
        } catch (e: Exception) {

        }

        if (vibrator != null) {
            //vibrator.cancel()
        }
    }

    fun stop() {
        Log.v(TAG, "stop =========================================================")
        //停止 ===========================================
        try {
            mp?.let{mp ->
                mp.Stop()
            }
        } catch (e: Exception) {
            Log.e(TAG,"" + e.toString())
        }

        if (vibrator != null) {
            //vibrator.cancel()
        }
    }

    fun release() {
        Log.v(TAG, "release =========================================================")

        try {
            mp?.let{it ->
                it.Release()//メディアプレーヤーを開放
            }
            mp = null
        } catch (e: Exception) {
            Log.i(TAG, "error:$e")
        }

    }

    inner class MyMusicListener : MyMusicPlayer.onPlayStatusListener, MyMusicPlayer.onMyCompletionListener {
        override fun onStart(mp: MyMusicPlayer, path: String) {}
        override fun onPause(mp: MyMusicPlayer) {}
        override fun onStop(mp: MyMusicPlayer) {}
        override fun onLoading(mp: MyMusicPlayer) {}
        override fun onComplete(mp: MyMusicPlayer) {}
        override fun onError(mp: MyMusicPlayer, what: Int, extra: Int) {}
        override fun onError(mp: MyMusicPlayer, error: String) {}
        override fun onCompletion(mp: MyMusicPlayer) {}
    }
}