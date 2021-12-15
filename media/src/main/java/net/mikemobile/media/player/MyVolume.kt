package net.mikemobile.android.music

import android.content.Context
import android.media.AudioManager
import net.mikemobile.android.music.MyVolume
import android.content.SharedPreferences

object MyVolume {
    const val ALARM = AudioManager.STREAM_ALARM
    const val TEL = AudioManager.STREAM_DTMF
    const val MUSIC = AudioManager.STREAM_MUSIC
    const val NOTI = AudioManager.STREAM_NOTIFICATION
    const val RING = AudioManager.STREAM_RING
    const val SYSTEM = AudioManager.STREAM_SYSTEM
    const val VOICE = AudioManager.STREAM_VOICE_CALL
    const val SELECT_TYPE = MUSIC
    fun getVolume(context: Context): Int {
        return getVolume(context, SELECT_TYPE)
    }

    fun setVolume(context: Context, volume: Int) {
        setVolume(context, SELECT_TYPE, volume)
    }

    fun getMaxVolume(context: Context): Int {
        return getMaxVolume(context, SELECT_TYPE)
    }

    fun getVolume(context: Context, type: Int): Int {
        // AudioManagerを取得する
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 現在の音量を取得する
        val ringVolume = am.getStreamVolume(type)

        // ストリームごとの最大音量を取得する
        val ringMaxVolume = am.getStreamMaxVolume(type)

        // 音量を設定する
        //am.setStreamVolume(type, ringVolume, 0);

        // ミュート設定をONにする
        //am.setStreamMute(type, true);
        return ringVolume
    }

    fun getMaxVolume(context: Context, type: Int): Int {
        // AudioManagerを取得する
        val am =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 現在の音量を取得する
        val ringVolume = am.getStreamVolume(type)

        // ストリームごとの最大音量を取得する

        // 音量を設定する
        //am.setStreamVolume(type, ringVolume, 0);

        // ミュート設定をONにする
        //am.setStreamMute(type, true);
        return am.getStreamMaxVolume(type)
    }

    fun setVolume(context: Context, type: Int, vol: Int) {
        // AudioManagerを取得する
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 音量を設定する
        am.setStreamVolume(type, vol, 0)
    }

    fun saveSystemVolume(context: Context) {
        val volume = getVolume(context, SELECT_TYPE)
        setInteger(getPref(context, "AlarmVolume"), "systemVolume", volume)
    }

    fun resetSystemVolume(context: Context) {
        val volume = getInteger(getPref(context, "AlarmVolume"), "systemVolume", -1)
        setVolume(context, SELECT_TYPE, volume)
    }

    fun getPref(context: Context, group: String?): SharedPreferences {
        return context.getSharedPreferences(group, Context.MODE_PRIVATE)
    }

    fun setInteger(data: SharedPreferences, key: String?, value: Int) {
        val editor = data.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInteger(context: Context, group: String?, key: String?, def: Int): Int {
        val data = getPref(context, group)
        return getInteger(data, key, def)
    }

    fun getInteger(data: SharedPreferences, key: String?, def: Int): Int {
        return data.getInt(key, def)
    }
}