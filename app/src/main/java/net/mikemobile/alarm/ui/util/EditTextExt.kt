package net.mikemobile.alarm.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.EditText


class EditTextExt : androidx.appcompat.widget.AppCompatEditText {
    var TAG = "EditTextExt"
    /**
     * Log.v(TAG, "VERBOSE");  // 冗長（詳細な）ログの出力
     * Log.d(TAG, "DEBUG");    // デバッグログの出力
     * Log.i(TAG, "INFO");     // 情報ログの出力
     * Log.w(TAG, "WARN");     // 警告ログの出力
     * Log.e(TAG, "ERROR");    // エラーログの出力
     */
    /**
     * コンストラクタ
     *
     * @param context
     */
    constructor(context: Context) : super(context) {
        firstSet()
    }

    /**
     * コンストラクタ
     *
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        firstSet()
    }

    fun onPause() {
        invalidate()
    }

    fun firstSet() {}

    /** レイアウトの現在の幅と高さ=========================================  */

    /** 外部から読み込むデータ=========================================  */
    private val paint = Paint()

    /** =========================================  */
    fun UpdateDraw() {
        invalidate()
    }


    var headText = "タイトル"

    var textPaint = Paint()
    fun setTextPaint() {
        textPaint.setTextSize(16f)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var width = getTextWidth(headText, textPaint)
        var height = getTextY(headText, paint, (16f))

        canvas.drawText(headText, 0f,height, textPaint)

    }

    fun getTextWidth(txt: String?, paint: Paint): Float {
        // 文字列の幅を取得
        return paint.measureText(txt)
    }

    fun getTextY(
        txt: String?,
        paint: Paint,
        centerY: Float
    ): Float {
        val s = paint.measureText(txt)
        val fontMetrics = paint.fontMetrics
        // 文字列の幅を取得
        return centerY - (fontMetrics.ascent + fontMetrics.descent) / 2
    }
    /** =====================================================================================================  */ //
    /** =====================================================================================================  */
    /** =====================================================================================================  */ //
    /** =====================================================================================================  */
    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int){
        super.onSizeChanged(w,h,oldw,oldh)
    }

    //サイズ調整用らしい
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**
         * int widthMode = MeasureSpec.getMode(widthMeasureSpec);//高さのモードを取得
         * int heightMode = MeasureSpec.getMode(heightMeasureSpec);//幅のモードを取得
         * int widthSize = MeasureSpec.getSize(widthMeasureSpec);//幅を取得
         * int heightSize = MeasureSpec.getSize(heightMeasureSpec);//高さを取得
         *
         * setMeasuredDimension(widthSize, widthSize);//幅をparentの希望値に、高さを幅の1/4に設定する。
         */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    /** =====================================================================================================  */ //Rクラス関連
    /** =====================================================================================================  */
}