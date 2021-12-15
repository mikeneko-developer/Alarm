package net.mikemobile.alarm.util.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.Log;

import net.mikemobile.media.MediaInfo;
import net.mikemobile.media.MediaManager;
import net.mikemobile.media.MediaUtilityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyMusicPlayer extends MediaPlayer implements OnCompletionListener, OnErrorListener{

	private String TAG = "MyMusicPlayer";

	private Object tag;		//タグを保持するようのオブジェクト変数
	private String path;	//指定されたディレクトリパスやURLを保持する変数
	
	private int playmode = 0;
	
	public static final int NONE = -1;
	public static final int PAUSE = 0;
	public static final int PLAY = 1;
	public static final int LOADING = 2;
	public static final int STOP = 3;
	public static final int COMPLETE = 4;
	public static final int ERROR = 100;
	
	private int loading_percent = 0;
	private boolean bufferPlay = false;
	
	public MyMusicPlayer(){
		playmode = NONE;
		setOnErrorListener(this);
		setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.w(TAG,"ローディング完了");
				loading = false;
				if(play_OK){
					Log.w(TAG,"ローディング-Play");
					mp.start();
					playmode = PLAY;
					bufferPlay = true;
					if(statuslistener != null)statuslistener.onStart(MyMusicPlayer.this,"");
				}
				
			}
		});
		
		setOnBufferingUpdateListener(new OnBufferingUpdateListener(){
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				//Log.w(TAG,"onBufferingUpdate:"+ percent);
				loading_percent = percent;
				
			}
			
		});
		
		this.setOnInfoListener(new OnInfoListener(){
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO 自動生成されたメソッド・スタブ
				return false;
			}
		});
		
		
	}
	
	public void setMyTag(Object _tag){
		tag = _tag;
	}
	
	public Object getMyTag(){
		return tag;
	}
	
	private boolean loading = false;

	public void setUriData(Context context, Uri uri) {
		Log.w(TAG,"setUriData()");

		if(isPlaying()){
			stop();
		}

		String error = "";
		try {
			this.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED
							|| extra == MediaPlayer.MEDIA_ERROR_MALFORMED) {
						Log.e(TAG, "erroronplaying");
					} else if (extra == MediaPlayer.MEDIA_ERROR_IO) {
						Log.e(TAG, "erroronplaying");
						return false;
					}
					return false;
				}
			});
			this.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					Log.i(TAG, "" + percent);

				}
			});

			this.setDataSource(context, uri);

			play_OK = true;
			loading_percent = 0;
			bufferPlay = false;

			this.prepare();//これは予定調和でやっとくもの

			playmode = LOADING;
			if(statuslistener != null)statuslistener.onLoading(this);

			Log.w(TAG,"setUriData() 設定完了");
			return;
		} catch (IllegalArgumentException e) {
			error = e.toString();
		} catch (IllegalStateException e) {
			error = e.toString();
		} catch (FileNotFoundException e) {
			error = e.toString();
		} catch (IOException e) {
			error = e.toString();
		} catch (Exception e) {
			error = e.toString();
		}

		Log.e(TAG,"setUriData() エラー:" + error);
		playmode = ERROR;
		if(statuslistener != null)statuslistener.onError(this,error);
	}

	public void setUriData(Context context,String _path) {
		Log.w(TAG,"setUriData() _path:" + _path);

		if(isPlaying()){
			stop();
		}

		path = _path;
		String error = "";

		try {
			this.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED
							|| extra == MediaPlayer.MEDIA_ERROR_MALFORMED) {
						Log.e(TAG, "erroronplaying");
					} else if (extra == MediaPlayer.MEDIA_ERROR_IO) {
						Log.e(TAG, "erroronplaying");
						return false;
					}
					return false;
				}
			});
			this.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					Log.i(TAG, "" + percent);

				}
			});

			MediaManager mediaManager = MediaUtilityManager.Companion.getMediaManager(context);
			MediaInfo info = mediaManager.onReadMusicData(path);

			Uri uri = info.getData();
			//setDataSource(uri);
			this.setDataSource(context, uri);
			play_OK = true;
			loading_percent = 0;
			bufferPlay = false;

			this.prepare();//これは予定調和でやっとくもの

			playmode = LOADING;
			if(statuslistener != null)statuslistener.onLoading(this);

			Log.w(TAG,"setUriData() 設定完了");
			return;
		} catch (IllegalArgumentException e) {
			error = e.toString();
		} catch (IllegalStateException e) {
			error = e.toString();
		} catch (FileNotFoundException e) {
			error = e.toString();
		} catch (IOException e) {
			error = e.toString();
		} catch (Exception e) {
			error = e.toString();
		}

		Log.e(TAG,"setUriData() エラー:" + error);
		playmode = ERROR;
		if(statuslistener != null)statuslistener.onError(this,error);
		path = null;
	}

	public void setData(Context context,String _path){
		path = _path;
		String error = "";
		try {
			this.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED
							|| extra == MediaPlayer.MEDIA_ERROR_MALFORMED) {
						Log.e(TAG, "erroronplaying");
					} else if (extra == MediaPlayer.MEDIA_ERROR_IO) {
						Log.e(TAG, "erroronplaying");
						return false;
					}
					return false;
				}
			});
			this.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					Log.i(TAG, "" + percent);

				}
			});
			
			if(path.indexOf("https") != -1 || path.indexOf("http") != -1){
				Log.i(TAG,"WEB");
				//URL指定
				Uri uri = Uri.parse(path);
				//setDataSource(uri);
				this.setDataSource(context, uri);
				loading = true;
				play_OK = false;
				//this.prepareAsync();//コレどう使うのかわからん
			}else {
				Log.i(TAG,"ローカル");
				//ローカルディレクトリ指定

				setDataSource(new FileInputStream(new File(path)).getFD());
			}
			
			loading_percent = 0;
			bufferPlay = false;
			
			//this.prepare();//これは予定調和でやっとくもの
			this.prepareAsync();

			playmode = LOADING;
			if(statuslistener != null)statuslistener.onLoading(this);
			
			return;
		} catch (IllegalArgumentException e) {
			error = e.toString();
		} catch (IllegalStateException e) {
			error = e.toString();
		} catch (FileNotFoundException e) {
			error = e.toString();
		} catch (IOException e) {
			error = e.toString();
		} catch (Exception e) {
			error = e.toString();
		}

		Log.e(TAG,"" + error);

		playmode = ERROR;
		if(statuslistener != null)statuslistener.onError(this,error);
		path = null;
	}
	
	public String getData(){
		return path;
	}
	
	public int getMyPlaying(){
		return playmode;
	}
	
	public boolean isMyPlaying(){
		if(playmode == PLAY)return true;
		return false;
	}
	
	
	boolean play_OK = false;
	public void Play() throws IllegalStateException, IOException{
		if(path == null)return;
		
		if(path.indexOf("https") != -1 || path.indexOf("http") != -1){
			if(loading){//URLを指定・設定して最初の再生処理
				play_OK = true;
				
				//this.prepareAsync();
			}else {//２回目以降の再生処理
				start();
				playmode = PLAY;
				if(statuslistener != null)statuslistener.onStart(this,path);
			}
			
		}else {
			//ローカルディレクトリの再生処理
			start();
			playmode = PLAY;
			if(statuslistener != null)statuslistener.onStart(this,path);
		}
		loading = false;
	}
	
	public void Pause(){
		pause();
		playmode = PAUSE;
		if(statuslistener != null)statuslistener.onPause(this);
	}
	
	public void Stop(){
		stop();
		seekTo(0);
		playmode = STOP;
		if(statuslistener != null)statuslistener.onStop(this);
	}
	
	public void Release(){
		stop();
		reset();
		release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		playmode = COMPLETE;
		if(listener != null)listener.onCompletion(this);
		if(statuslistener != null)statuslistener.onComplete(this);
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		playmode = ERROR;
		if(statuslistener != null)statuslistener.onError(this,what,extra);
		return false;
	}
	
	public interface onPlayStatusListener {
		public abstract void onStart(MyMusicPlayer mp, String path);
		public abstract void onPause(MyMusicPlayer mp);
		public abstract void onStop(MyMusicPlayer mp);
		public abstract void onLoading(MyMusicPlayer mp);
		public abstract void onComplete(MyMusicPlayer mp);
		public abstract void onError(MyMusicPlayer mp, int what, int extra);
		public abstract void onError(MyMusicPlayer mp, String error);
	}

	private onPlayStatusListener statuslistener;
	public void setOnPlayStatusListener(onPlayStatusListener _listener){
		setOnCompletionListener(this);
		statuslistener = _listener;
	}
	
	public interface onMyCompletionListener {
		public abstract void onCompletion(MyMusicPlayer mp);
	}
	
	private onMyCompletionListener listener;
	public void setOnMyCompletionListener(onMyCompletionListener _listener){
		setOnCompletionListener(this);
		this.listener = _listener;
	}
	
}
