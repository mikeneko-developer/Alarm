package net.mikemobile.alarm.util.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

public class MyVolume {
	public static final int ALARM = AudioManager.STREAM_ALARM;
	public static final int TEL = AudioManager.STREAM_DTMF;
	public static final int MUSIC = AudioManager.STREAM_MUSIC;
	public static final int NOTI = AudioManager.STREAM_NOTIFICATION;
	public static final int RING = AudioManager.STREAM_RING;
	public static final int SYSTEM = AudioManager.STREAM_SYSTEM;
	public static final int VOICE = AudioManager.STREAM_VOICE_CALL;

	public static final int SELECT_TYPE = MUSIC;

	public static int getVolume(Context context) {
		return getVolume(context, SELECT_TYPE);
	}

	public static void setVolume(Context context, int volume) {
		setVolume(context, SELECT_TYPE, volume);
	}

	public static int getMaxVolume(Context context) {
		return getMaxVolume(context, SELECT_TYPE);
	}


	static int getVolume(Context context,int type){
		// AudioManagerを取得する
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
 
        // 現在の音量を取得する
        int ringVolume = am.getStreamVolume(type);
 
        // ストリームごとの最大音量を取得する
        int ringMaxVolume = am.getStreamMaxVolume(type);
 
        // 音量を設定する
        //am.setStreamVolume(type, ringVolume, 0);
 
        // ミュート設定をONにする
        //am.setStreamMute(type, true);
        
        return ringVolume;
	}
	static int getMaxVolume(Context context,int type){
		// AudioManagerを取得する
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
 
        // 現在の音量を取得する
        int ringVolume = am.getStreamVolume(type);
 
        // ストリームごとの最大音量を取得する
        int ringMaxVolume = am.getStreamMaxVolume(type);
 
        // 音量を設定する
        //am.setStreamVolume(type, ringVolume, 0);
 
        // ミュート設定をONにする
        //am.setStreamMute(type, true);
        
        return ringMaxVolume;
	}
	
	static void setVolume(Context context,int type,int vol){
		// AudioManagerを取得する
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        
        // 音量を設定する
        am.setStreamVolume(type, vol, 0);
	}

	public static void saveSystemVolume(Context context) {
		int volume = getVolume(context, SELECT_TYPE);
		setInteger(getPref(context, "AlarmVolume"),"systemVolume", volume);
	}

	public static void resetSystemVolume(Context context) {
		int volume = getInteger(getPref(context, "AlarmVolume"), "systemVolume", -1);
		setVolume(context, SELECT_TYPE, volume);
	}


	public static SharedPreferences getPref(Context context, String group){
		return context.getSharedPreferences(group, Context.MODE_PRIVATE);
	}
	public static void setInteger(SharedPreferences data, String key, int value){
		SharedPreferences.Editor editor = data.edit();
		editor.putInt(key, value);
		editor.apply();

	}
	public static int getInteger(Context context, String group, String key, int def){
		SharedPreferences data = getPref(context,group);
		return getInteger(data,key,def);
	}
	public static int getInteger(SharedPreferences data, String key, int def){
		return data.getInt(key,def);
	}
	
}
