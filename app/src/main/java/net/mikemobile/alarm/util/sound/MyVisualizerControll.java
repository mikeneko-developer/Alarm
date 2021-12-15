package net.mikemobile.alarm.util.sound;

import android.app.Activity;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.view.View;


/**
 *
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 * パーミッションを忘れずに
 *
 */

public class MyVisualizerControll {
    private VisualizerView mVisualizerView;
    private Visualizer mVisualizer;

    public MyVisualizerControll(){

    }

    public MyVisualizerControll(Activity activity,int layoutid){
        mVisualizerView = (VisualizerView)activity.findViewById(layoutid);
    }

    public MyVisualizerControll(View view,int layoutid){
        mVisualizerView = (VisualizerView)view.findViewById(layoutid);
    }

    public MyVisualizerControll(VisualizerView mVisualizerView){
        this.mVisualizerView = mVisualizerView;
    }

    public void setVisualizeMedia(MediaPlayer mp){
        int id = mp.getAudioSessionId();

        setVisualize(id);
    }

    public void setVisualizeMedia(AudioTrack track){
        int id = track.getAudioSessionId();

        setVisualize(id);
    }

    public void setVisualizeMedia(MyMicControll mic){
        int id = mic.getAudioTrack().getAudioSessionId();

        setVisualize(id);
    }

    public void updateVisualizerView(byte[] bytes){
        int max = 1024;

        byte[] bytes_data = bytes;
        if(bytes.length > max){
            bytes_data = new byte[max];
            int cnt = bytes.length / max;

            for(int i=0;i<1024;i++){
                byte count = 0;
                for(int j=0;j<cnt;j++){
                    if(count < bytes[(i*cnt) + j]){
                        count = bytes[(i*cnt) + j];
                    }
                }
                bytes_data[i] = count;
            }
        }

        if(mVisualizerView != null)mVisualizerView.updateVisualizer(bytes_data);
    }

    double phase = 0.0;
    //サイン波の生成
    void sinWave(short data[],double freq) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (short)(Short.MAX_VALUE * Math.sin(phase));
            phase += 2.0 * Math.PI * freq / 44100;
        }
    }

    private void setVisualize(int id){
        //Visualizerの初期化
        mVisualizer = new Visualizer(id);
        //これおまじない、一回無効にしないと、有効になってくれないので
        mVisualizer.setEnabled(false);
        //音声データをキャプチャするサイズを設定

        int size = Visualizer.getCaptureSizeRange()[1];
        mVisualizer.setCaptureSize(size);
        //キャプチャしたデータを定期的に取得するリスナーを設定
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                                               //Wave形式のキャプチャーデータ
                                               public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
                                                   //Log.v("AudioRecord", "read3:" + bytes.length + " bytes");
                                                   if(mVisualizerView != null)mVisualizerView.updateVisualizer(bytes);

                                               }

                                               //高速フーリエ変換のキャプチャーデータ
                                               public void onFftDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
                                                   Log.v("AudioRecord", "フーリエ変換");
                                               }
                                           },
                Visualizer.getMaxCaptureRate() / 2, //キャプチャーデータの取得レート（ミリヘルツ）
                true,//これがTrueだとonWaveFormDataCaptureにとんでくる
                false);//これがTrueだとonFftDataCaptureにとんでくる
        //mVisualizer.setEnabled(true);
    }

    public void start(){
        mVisualizer.setEnabled(true);
    }

    public void stop(){
        mVisualizer.setEnabled(false);
    }

}
