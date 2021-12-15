package net.mikemobile.alarm.util.sound;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MyMicControll implements OnRecordPositionUpdateListener {
    private final static int SAMPLING_RATE = 11025;//44100, 22050 or 11025,8000
    private AudioRecord audioRec = null;
    private AudioTrack track = null;
    private Handler handler = new Handler();

    private final int striem = AudioManager.STREAM_VOICE_CALL;
    //AudioManager.STREAM_VOICE_CALL
    //AudioManager.STREAM_MUSIC
    //AudioManager.STREAM_RING

    private boolean bIsRecording = false;

    private int bufSize;

    /** Called when the activity is first created. */

    private AudioManager audio;
    private int volume = 0;

    private Button button;
    public void setButton(Button button){
        this.button = button;
        this.button.setText("再生");
        this.button.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Play();
                if(listener != null)listener.buttonClick(bIsRecording);

            }
        });
    }

    public MyMicControll(Activity act) {
        setMic(act);
    }

    private boolean speakerInMic  = false;
    public void setOutPutSpeaker(boolean flag){
        speakerInMic = flag;
    }

    public AudioTrack getAudioTrack(){
        return track;
    }

    public static int getbufSize(){
        return AudioRecord.getMinBufferSize(
                //int sampleRateInHz
                SAMPLING_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 2;
    }

    public void setMic(Activity act){
        this.audio = (AudioManager)act.getSystemService(Context.AUDIO_SERVICE);
        volume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        audio.setSpeakerphoneOn(true);
        //audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, 0);


        handler = new Handler();
        bufSize = AudioRecord.getMinBufferSize(
                //int sampleRateInHz
                SAMPLING_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 2;


        Log.v("AudioRecord", "bufSize:" + String.valueOf(bufSize));


        // AudioRecordの作成
        audioRec = new AudioRecord(
                //int audioSource
                MediaRecorder.AudioSource.MIC,

                //int sampleRateInHz
                SAMPLING_RATE,

                //int channelConfig
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                //AudioFormat.CHANNEL_CONFIGURATION_STEREO,

                //int audioFormat
                AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_8BIT,

                //int bufferSizeInBytes
                bufSize);


        audioRec.setRecordPositionUpdateListener(this);

        track = new AudioTrack(
                //streamType どの音を出すかってこと?
                AudioManager.STREAM_VOICE_CALL,
                //AudioManager = Androidで音量を調節するもの
                //音楽音量(STREAM_MUSIC)
                //詳しくはhttp://techbooster.jpn.org/andriod/device/1253/

                //sampleRateInHz:サンプリング周波数
                SAMPLING_RATE,

                //channelConfig(モノラル指定)
                AudioFormat.CHANNEL_CONFIGURATION_MONO,

                //audioFormat
                AudioFormat.ENCODING_DEFAULT,

                //bufferSizeInBytes:バイト単位のバッファサイズ
                //サンプリングレートが44100でバッファサイズが44100なので、
                //このAudioTrackには1秒分の音声データを格納することができます。
                bufSize,

                //mode(STREAMモード指定)
                AudioTrack.MODE_STREAM
        );

        int pay = track.getPositionNotificationPeriod();
        //track.setPositionNotificationPeriod(SAMPLING_RATE);

        Log.v("AudioRecord", "pay:" + pay);

        //track.setPositionNotificationPeriod(1024);

        //pay = track.getPositionNotificationPeriod();
        //audioRec.setPositionNotificationPeriod(pay);

        Log.v("AudioRecord", "pay:" + pay);
        track.play();


    }

    @Override
    public void onMarkerReached(AudioRecord recorder) {
        Log.v("AudioRecord", "onMarkerReached");

    }

    @Override
    public void onPeriodicNotification(AudioRecord recorder) {
        Log.v("AudioRecord", "onPeriodicNotification");

    }

    Thread thread;

    public void Play() {
        // TODO Auto-generated method stub
        if (bIsRecording) {
            audio.setSpeakerphoneOn(false);
            stop();

        }else {
            audio.setSpeakerphoneOn(true);

            // 録音開始
            if(button != null)button.setText("停止");

            Log.v("AudioRecord", "startRecording");

            audioRec.startRecording();

            bIsRecording = true;

            thread = new Thread(runnable);
            thread.start();

        }
    }

    public void stop(){
        bIsRecording = false;
        audioRec.stop();

        handler.post(new Runnable(){

            @Override
            public void run() {
                if(button != null)button.setText("再生");
                if(listener != null)listener.end();
            }
        });
    }


    public void loadSound(byte[] buf){

        track.write(buf, 0, buf.length);
    }



    private int read = 0;
    Runnable runnable = new Runnable(){
        byte buf[] = new byte[bufSize];
        @Override
        public void run() {
            buf = new byte[bufSize];

            Log.d("LogList", "bufSize : " + bufSize);

            // TODO Auto-generated method stub
            while (bIsRecording) {
                read = 0;

                // 録音データ読み込み
                read = audioRec.read(buf, 0, buf.length);
                //audioRec.read(buffer, 0, buffer.length);

                if(read > 0){

                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            double db = 0;
                            if(listener != null && bIsRecording && speakerInMic)listener.read(read,buf,db);
                        }
                    });

                    Log.v("AudioRecord", "read :" + buf.length + " bytes");

                    if(speakerInMic){
                        //マイクの音を直接再生する
                        //track.write(buf, 0, buf.length);
                    }
                }

            }

            // 録音停止
            Log.v("AudioRecord", "stop");

        }
    };


    public void onDestroy() {
        // TODO Auto-generated method stub
        audio.setSpeakerphoneOn(false);
        stop();

        audioRec.release();
        track.stop();
        track.release();
        audio.setSpeakerphoneOn(false);
        stop();
        //audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);

    }

    private double baseValue = 12.0;
    public double getdb(int read,byte[] buffer){
        int maxValue = 0;
        for (int i = 0; i < read; i++) {
            maxValue = Math.max(maxValue, buffer[i]);
        }

        double db = 20.0 * Math.log10(maxValue / baseValue);
        Log.d("SoundLevelMeter", "dB:" + db);

        return 0;
    }
    /** ================== インターフェース ================== **/
    private onMyMicControllListener listener;
    public void setOnMyMicControllListener(onMyMicControllListener listener) {this.listener = listener;}


    public interface onMyMicControllListener {
        public abstract void read(int read, byte[] buf, double db);
        public abstract void buttonClick(boolean flag);
        public abstract void end();

    }



}