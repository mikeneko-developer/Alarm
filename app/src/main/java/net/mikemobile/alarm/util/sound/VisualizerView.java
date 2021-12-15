package net.mikemobile.alarm.util.sound;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();

    private int sound_dankai = 128;

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(3f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    float max = 0;
    float min = 0;

    float maxb = 0;
    float minb = 0;

    int maxP = 0;
    int minP = 0;

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        Paint line = new Paint();
        line.setTextSize(18);
        line.setColor(Color.BLACK);

        canvas.drawLine(0,
                getHeight()/2,
                getWidth(),
                getHeight()/2,
                line);


        line.setStrokeWidth(10);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];

        }

        int sukima = 10;

        mRect.set(0, 0, getWidth() - sukima*2, getHeight());

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        // 波形データは-128～127の範囲のデータであるため128を足して
        // ０からはじまるようにしています。
        int heikin = 0;
        for (int i = 0; i < mBytes.length - 1; i++) {
            if(i < mBytes.length - 2){
                heikin += mBytes[i];
            }else {
                heikin += mBytes[i];
                heikin += mBytes[i+1];
            }
            //スタートぼじしょん
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1) + sukima;
            mPoints[i * 4 + 1] = mRect.height() / 2 + ((byte) (mBytes[i] + sound_dankai))
                    * (mRect.height() / 2) / sound_dankai;


            //ストップポジション
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1) + sukima;
            mPoints[i * 4 + 3] = mRect.height() / 2 + ((byte) (mBytes[i + 1] + sound_dankai))
                    * (mRect.height() / 2) / sound_dankai;



            if(max < mBytes[i+1]){
                max = mBytes[i+1];
                maxb = mPoints[i * 4 + 3];
                maxP = i;
            }else if(max < mBytes[i]){
                max = mBytes[i];
                maxb = mPoints[i * 4 + 1];
                maxP = i;
            }



            if(min > mBytes[i+1]){
                min = mBytes[i+1];
                minb = mPoints[i * 4 + 3];
                minP = i;
            }else if(min > mBytes[i]){
                min = mBytes[i];
                minb = mPoints[i * 4 + 1];
                minP = i;
            }

        }

        canvas.drawLines(mPoints, mForePaint);



        line.setColor(Color.CYAN);
        /**
         canvas.drawLine(0,
         maxb,
         getWidth(),
         maxb,
         line);


         line.setColor(Color.BLUE);
         canvas.drawLine(0,
         minb,
         getWidth(),
         minb,
         line);
         */


        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        canvas.drawText(String.valueOf(max), 0, 19*1, paint);
        canvas.drawText(String.valueOf(min), 0, 19*2, paint);
        canvas.drawText(String.valueOf(maxb), 0, 19*3, paint);
        canvas.drawText(String.valueOf(minb), 0, 19*4, paint);
        canvas.drawText(String.valueOf(maxP), 0, 19*5, paint);
        canvas.drawText(String.valueOf(minP), 0, 19*6, paint);
        canvas.drawText(String.valueOf(heikin / mBytes.length), 0, 19*7, paint);


        //canvas.drawText(String.valueOf(mBytes.length), 0, 19*5, paint);


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    //サイズ調整用らしい
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("MyImageViewLog","onMeasure");

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int nViewWidth =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int nViewHeight =  MeasureSpec.getSize(heightMeasureSpec);


        //setMeasuredDimension(resolveSize(finalWidth, widthMeasureSpec),resolveSize(finalHeight, heightMeasureSpec));

    }
}