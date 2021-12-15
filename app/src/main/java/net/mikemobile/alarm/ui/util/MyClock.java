package net.mikemobile.alarm.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import net.mikemobile.alarm.util.MyDate;


public class MyClock extends View{
	public Context context;
	private Handler handler = new Handler();
	
	String TAG = "LogList";  
    /**
     *	Log.v(TAG, "VERBOSE");  // 冗長（詳細な）ログの出力  
     *	Log.d(TAG, "DEBUG");    // デバッグログの出力  
	 *	Log.i(TAG, "INFO");     // 情報ログの出力  
	 *	Log.w(TAG, "WARN");     // 警告ログの出力  
	 *	Log.e(TAG, "ERROR");    // エラーログの出力  
     */
	
    /** ======================================================== **/

	public static final int SUMMER_START_MONTH = 7;
	public static final int SUMMER_END_MONTH = 9;

	public static final int SUMMER_MONING_HOUR = 5;
	public static final int SUMMER_NIGHT_HOUR = 19;

	public static final int MONING_HOUR = 6;
	public static final int NIGHT_HOUR = 18;

    /**
     * コンストラクタ
     *
     * @param context
     */
    public MyClock(Context context) {
        super(context);
        this.context = context;
        
        firstSet();
    }
    
    /**
     * コンストラクタ
     *
     * @param context
     * @param attrs
     */
    public MyClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        firstSet();
    }
    
    
    private boolean onView = false;
    public void onPause(){
    	onView = false;
        invalidate();
    }
    
    public void firstSet(){
    	onView = true;
    	
    	
    }
    
    /** レイアウトの現在の幅と高さ========================================= **/
    int width = 100;
    int height = 100;
    
    /** 外部から読み込むデータ========================================= **/
    private Paint paint = new Paint();
    
    /** ========================================= **/
    public void UpdateDraw(){
    	onView = true;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas){
        //super.onDraw(canvas);

		int back_color = Color.BLACK;
		int draw_color = Color.WHITE;

		int month = MyDate.getMonth();
		int hour = MyDate.getHour();


		int check_start_hour = MONING_HOUR;

		canvas.drawColor(back_color);
		paint.setColor(draw_color);

    	// ビューのレイアウト後の幅
        width = getMeasuredWidth();
        // ビューのレイアウト後の高さ。パディングを考慮する必要がある。
        height = getMeasuredHeight();

    	paint.setStyle(Paint.Style.STROKE);
        frame(canvas,width,height);
        
        paint.setStyle(Paint.Style.FILL);
        time(canvas,width,height);
        date(canvas,width,height);
    }
    
    private void frame(Canvas canvas,int width,int height){
    	paint.setStrokeWidth(20);
    	
        Rect rect = new Rect(10,10,width-10,height-10);
        canvas.drawRect(rect, paint);
        
    }
    
    private void date(Canvas canvas,int width,int height){
    	long date = MyDate.getTimeMillis();
    	int week = MyDate.getWeek();
    	
    	String date_text = MyDate.getDateJapanString(MyDate.DATE_YMD, date);
    	date_text = date_text + "(" + MyDate.getWeekJapanString(week) + ")";
    	
    	int time_text_size = width / 4;
    	
    	int font_size = time_text_size / 4;
    	paint.setTextSize(font_size);
    	
    	float text_width = getTextWidth(date_text,paint);
    	
    	float x = width / 20;
    	float y1 = (height/2 - time_text_size/2)/7*5;
    	float y = getTextY(date_text,paint,y1);
    	
    	paint.setTextSize(font_size);
    	canvas.drawText(date_text, x, y, paint);
    	
    	//canvas.drawRect(0, y1, width, y1, paint);
    	
    }
    
    private void time(Canvas canvas,int width,int height){
    	
    	long date = MyDate.getTimeMillis();
    	
    	String t_time = MyDate.getTimeString(MyDate.TIME_HM, date);
    	String t_second = MyDate.getTimeString(MyDate.TIME_S,date);
    	

    	int font_size = width / 4;
    	paint.setTextSize(font_size);
    	
    	float text_width = getTextWidth(t_time,paint);
    	int font_size2 = font_size/2;
    	paint.setTextSize(font_size2);
    	float text_width2 = getTextWidth(t_second,paint);
    	
    	float x = width/2 - (text_width + text_width2)/2;
    	paint.setTextSize(font_size);
    	float y = getTextY(t_time,paint,height/2);
    	
    	paint.setTextSize(font_size);
    	canvas.drawText(t_time, x, y, paint);
    	
    	
    	font_size = height/2;
    	paint.setTextSize(font_size);
    	
    	float x2 = (width/2 - (text_width + text_width2)/2) + text_width;
    	float y2 = getTextY(t_second,paint,height/2);
    	
    	paint.setTextSize(font_size2);
    	canvas.drawText(t_second, x2, y2, paint);
    	
    }

	public float getTextWidth(String txt,Paint paint){
		// 文字列の幅を取得
    	return paint.measureText(txt);
	}
	
	public float getTextY(String txt,Paint paint,float centerY){
		float s = paint.measureText(txt);
		FontMetrics fontMetrics = paint.getFontMetrics();
    	// 文字列の幅を取得
    	return centerY - (fontMetrics.ascent + fontMetrics.descent)/2;
	}

	/** ===================================================================================================== **/
	//
	/** ===================================================================================================== **/



	/** ===================================================================================================== **/
    //
    /** ===================================================================================================== **/
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
    
    //サイズ調整用らしい
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	/**
    	int widthMode = MeasureSpec.getMode(widthMeasureSpec);//高さのモードを取得
    	int heightMode = MeasureSpec.getMode(heightMeasureSpec);//幅のモードを取得
    	int widthSize = MeasureSpec.getSize(widthMeasureSpec);//幅を取得
    	int heightSize = MeasureSpec.getSize(heightMeasureSpec);//高さを取得

    	setMeasuredDimension(widthSize, widthSize);//幅をparentの希望値に、高さを幅の1/4に設定する。
        */
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    /** ===================================================================================================== **/
    //Rクラス関連
    /** ===================================================================================================== **/
    
    /** リスナー===================================================== **/
    private OnCalendarSelect listener;
    public void setOnCalendarPositionListener(OnCalendarSelect listener) {this.listener = listener;}
    
    private OnCalendarEvent eventListener = new SimpleOnCalendarEvent();
    public void setOnCalendarEventListener(OnCalendarEvent listener) {this.eventListener = listener;}
    
    /** インターフェース===================================================== **/
    public interface OnCalendarSelect {//カレンダー選択
    	public abstract void Select(long date, int year, int month, int day, boolean flag);
    	public abstract void DoubleTap_Date(long date, int year, int month, int day, boolean flag);
    	public abstract void LongClick(long date, int year, int month, int day, boolean flag);
    	public abstract Bitmap getImage(Context context, String fileName, float resize);
    	
    }
    
    
    public interface OnCalendarEvent {//カレンダーの日付のイベント
    	public abstract int getDayEventCount(Context context, int year, int month, int day);
    	public abstract String getDayEventIcon(Context context, int year, int month, int day, int position);
    	public abstract String getDayEventTitle(Context context, int year, int month, int day, int position);
    	public abstract long getDayEventStartYear(Context context, int year, int month, int day, int position);
    	public abstract long getDayEventStartDate(Context context, int year, int month, int day, int position);
    	public abstract int getDayEventAllDay(Context context, int year, int month, int day, int position);
		
    }
    public class SimpleOnCalendarEvent implements OnCalendarEvent{
		@Override
		public int getDayEventCount(Context context,int year, int month, int day) {
			return 0;
		}

		@Override
		public String getDayEventIcon(Context context,int year, int month, int day, int position) {
			return null;
		}
		
		@Override
		public String getDayEventTitle(Context context,int year, int month, int day, int position) {
			// TODO Auto-generated method stub
			return "";
		}

		@Override
		public long getDayEventStartYear(Context context,int year, int month, int day,
				int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getDayEventStartDate(Context context,int year, int month, int day,
				int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getDayEventAllDay(Context context,int year, int month, int day, int position) {
			// TODO Auto-generated method stub
			return 0;
		}
    	
    }
    
}