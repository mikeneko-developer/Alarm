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

public class MySound implements OnRecordPositionUpdateListener {
    final static int SAMPLING_RATE = 8000;//44100, 22050 or 11025,8000
    AudioRecord audioRec = null;
    AudioTrack track = null;
    Handler handler = new Handler();

    boolean bIsRecording = false;

    int bufSize;

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
            }
        });
    }

    private boolean input = false;
    public void setInput(boolean flag){
        input = flag;
    }

    private VisualizerView mVisualizerView;

    public MySound(Activity act,VisualizerView mVisualizerView) {
        this.mVisualizerView = mVisualizerView;
        setMic(act);
    }

    public MySound(Activity act) {
        this.mVisualizerView = new VisualizerView(act);
        setMic(act);
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
        volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

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
                AudioManager.STREAM_MUSIC,
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

        track.setPositionNotificationPeriod(1024);
        pay = track.getPositionNotificationPeriod();
        audioRec.setPositionNotificationPeriod(pay);

        Log.v("AudioRecord", "pay:" + pay);

        setVisualizer();

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
            stop();
        }else {
            track.play();

            int id = track.getAudioSessionId();


            // 録音開始
            if(button != null)button.setText("停止");

            Log.v("AudioRecord", "startRecording");

            audioRec.startRecording();

            bIsRecording = true;

            thread = new Thread(runnable);
            thread.start();

        }
    }

    private void stop(){
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

    public void setVisualizer(){

    }

    byte prevbuffer[] = new byte[bufSize];
    int count = 0;
    public void loadSound(final byte[] buf){

        prevbuffer = buf;

        handler.post(new Runnable(){
            @Override
            public void run() {
                //mVisualizerView.updateVisualizer(buf);
                if(input)track.write(buf, 0, buf.length);
            }
        });

    }

    public void end(){
        track.stop();
    }

    private int read = 0;
    Runnable runnable = new Runnable(){
        byte buf[] = new byte[bufSize];
        byte buffer[] = new byte[bufSize];
        byte prevbuffer[] = new byte[bufSize];
        int count = 0;
        @Override
        public void run() {
            count = 0;
            buffer = new byte[bufSize];
            buf = new byte[bufSize];
            // TODO Auto-generated method stub
            while (bIsRecording) {
                read = 0;

                // 録音データ読み込み
                read = audioRec.read(buf, 0, buf.length);
                //audioRec.read(buffer, 0, buffer.length);

                if(count != 0){
                    int s1 = buf.length;
                    int s2 = prevbuffer.length;


                    for(int i=0;i<buf.length;i++){
                        buffer[i] = (byte) (buf[i] - prevbuffer[i]);
                    }
                }


                prevbuffer = buffer;

                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        //mVisualizerView.updateVisualizer(buffer);
                        if(listener != null && bIsRecording)listener.read(read,buf);
                    }
                });

                Log.v("AudioRecord", "read :" + buf.length + " bytes");
                Log.v("AudioRecord", "read2:" + buffer.length + " bytes");

                if(!input){
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            if(mVisualizerView != null)mVisualizerView.updateVisualizer(buf);
                        }
                    });


                }
                count++;
            }
            count = 0;
            // 録音停止
            Log.v("AudioRecord", "stop");

        }
    };


    public void onDestroy() {
        // TODO Auto-generated method stub
        audioRec.release();
        track.release();

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

    }



    /** ================== インターフェース ================== **/
    private onMyMicListener listener;
    public void setOnResultListener(onMyMicListener listener) {this.listener = listener;}


    public interface onMyMicListener {
        public abstract void read(int read, byte[] buf);
        public abstract void end();

    }



}