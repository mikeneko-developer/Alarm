package net.mikemobile.media.view;

import android.os.Handler;
import android.os.Looper;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by systena on 2017/11/10.
 * 時間設定用のタイマーおよびカウントアップ実装用のクラスです。
 */

public abstract class TimerManager {
    //==============================================================================================
    // オーバーライドするメソッド
    //==============================================================================================

    //タイムリミットを越えた場合に呼ばれる
    public void onTimeup(){

    };

    //カウントアップするたびに呼ばれる
    public void onCountup(int count,long time){

    };


    //==============================================================================================
    // 変数
    //==============================================================================================

    private MyTimerTask timerTask = null;
    private Timer mTimer   = null;

    private Handler mHandler;

    private long limit_time = -1;
    private long start_time = -1;

    private int countUpCount = 1;
    private long one_count_time = 1000;

    private boolean isTimer = false;


    //==============================================================================================
    // コンストラクタ
    //==============================================================================================

    /**
     *
     */
    public TimerManager(){
        mHandler = new Handler(Looper.getMainLooper());
        init();
    }

    //==============================================================================================
    // 非公開メソッド
    //==============================================================================================
    /**
     * タイマー初期化
     */
    private void init(){
        //タイマーの初期化処理
        timerTask = new MyTimerTask();
        mTimer = new Timer(true);

        limit_time = -1;
        start_time = -1;
        countUpCount = 1;
        one_count_time = 1000;
    }


    /**
     * 現在のミリ秒を取得する
     * @return ミリ秒を返します
     */
    public long getTimeMillis(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public boolean getIsTimer(){
        return isTimer;
    }

    public void setExtentionTime(){
        countUpCount = 0;
        start_time = getTimeMillis();
    }

    //==============================================================================================
    // 公開メソッド
    //==============================================================================================
    /**
     * カウントアップする場合の１カウントの秒数を指定します。
     * @param time ミリ秒を指定
     */
    public void setCountUpOneTime(long time){
        one_count_time = time;
    }

    /**
     * カウントアップのスタート処理
     */
    public void start(){
        start(-1);
    }

    /**
     * カウントダウンのスタート処理　引数に
     * @param time
     */
    public void start(long time){
        limit_time = time;
        start_time = getTimeMillis();

        mTimer.schedule( timerTask, 100, 100);
        isTimer = true;
    }

    /**
     * 中断・処理のリセット時に呼び出すメソッド
     */
    public void stop(){
        isTimer = false;
        
        if(mTimer != null) {
            mTimer.cancel();
        }

        if(timerTask != null){
            timerTask.cancel();
        }
        mTimer = null;
        timerTask = null;
    }


    //==============================================================================================
    // 内部クラス
    //==============================================================================================

    /**
     * タイマータスククラスを継承した定刻ループ処理
     */
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            long time = getTimeMillis();

            if(!isTimer){
                //終了状態と判定されたら、なにもしないようにする
            }else if(limit_time == -1){
                //カウントアップ用

                //指定秒数を超えたらカウントアップする
                if ((time - start_time) >= (one_count_time * countUpCount)) {
                    // mHandlerを通じてUI Threadへ処理をキューイング
                    mHandler.post( new CountUpRunnable(countUpCount,(one_count_time * countUpCount)));
                    countUpCount++;

                }
            }else {
                //カウントダウン用
                //該当時間を経過したので返す
                if ((time - start_time) >= limit_time) {
                    stop();
                    mHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            onTimeup();
                        }
                    });
                }
            }
        }
    }

    /**
     * Runnableクラスを継承したカウントアップ毎に情報を返す処理
     */
    class CountUpRunnable implements Runnable{
        int count;
        long time;
        public CountUpRunnable(int count,long time){
            this.count = count;
            this.time = time;
        }

        @Override
        public void run() {
            onCountup(count,time);
        }
    }
}
