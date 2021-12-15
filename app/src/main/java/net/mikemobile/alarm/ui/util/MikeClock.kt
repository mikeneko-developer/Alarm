package net.mikemobile.alarm.ui.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import net.mikemobile.alarm.util.CustomDateTime

import java.util.ArrayList


interface MikeClockListener {

}

class MikeClock : View {

    internal var TAG = "LogList"


    internal var haba = 20//枠の幅

    /** リストに関係するデータ=========================================  */
    private var list = ArrayList<String>()

    private val textPaint = Paint()
    private var textSize = 28f
    private var color = Color.BLACK

    private var reelColor = Color.WHITE

    private var gradation_start = Color.argb(255, 0, 0, 0)
    private var gradation_end = Color.argb(0, 0, 0, 0)

    private var select = 0//選択された項目

    private var outLineColor = Color.argb(255, 0, 0, 0)//


    /** インターフェース=====================================================  */
    private var listener: MikeClockListener? = null

    /** タッチ操作関連全て=====================================================  */
    private val fling = false
    private var moveY = 0f

    private val speed = 1.08f
    private val brake = 0.92f

    private var firstTouchX = 0f
    private var firstTouchY = 0f

    private var touchModeY = 0f
    private var touchModeY_prev = 0f
    private var touchModeY_next = 0f

    private var touchClick = false
    private var touchTouchUp = false

    private var touchDownX = 0f
    private var touchDownY = 0f


    /** ========================================================  */

    /**
     * コンストラクタ
     *
     * @param context
     */
    constructor(context: Context) : super(context) {

        setDefoList()
        setPaint()
    }

    /**
     * コンストラクタ
     *
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {

    }

    /** 外部から読み込むデータ=========================================  */
    fun setTextColor(color: Int) {
        this.color = color

        Update()
    }

    fun setTextSize(pxSize: Int) {
        textSize = pxSize.toFloat()
        Update()
    }

    fun setOutLineColor(color: Int) {
        this.outLineColor = color

        Update()
    }

    /** =========================================  */

    private fun Update() {
        invalidate()
    }

    private fun setDefoList() {
        list = ArrayList()

        for (i in 1..100) {
            list.add("その$i")
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

    }

    //サイズ調整用らしい
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var hScale = 1.0f
        var vScale = 1.0f

        val min_width = haba * 2 + 100
        val min_height = haba * 2 + 150

        val width = haba * 2 + 100
        val height = haba * 2 + 150


        /**
         * if(width > layoutWidth){
         * width = layoutWidth;
         * }
         * if(height > layoutheight){
         * height = layoutheight;
         * }
         */

        if (widthMode != View.MeasureSpec.UNSPECIFIED && widthSize < width) {
            hScale = widthSize.toFloat() / width.toFloat()
        }


        if (heightMode != View.MeasureSpec.UNSPECIFIED && heightSize < height) {
            vScale = heightSize.toFloat() / height.toFloat()
        }

        val scale = Math.min(hScale, vScale)

        var finalWidth = (width * scale).toInt()
        var finalHeight = (height * scale).toInt()

        if (finalWidth < min_width) finalWidth = min_width
        if (finalHeight < min_height) finalHeight = min_height

        // Viewのサイズから時計表示に使用する基本情報を計算する
        calclateSize(widthSize, heightSize)
        setPaint()

        setMeasuredDimension(
            View.resolveSize(finalWidth, widthMeasureSpec),
            View.resolveSize(finalHeight, heightMeasureSpec)
        )
    }


    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas);

        canvas.drawColor(reelColor)


        var text = CustomDateTime.getTimeText(CustomDateTime.getTimeInMillis())

        var text_width = getTextWidth(text, textPaint)
        var text_moveY = getTextHeight_yohaku(text, textPaint)

        canvas.drawText(text, centerX - (text_width / 2) , cneterY - text_moveY, textPaint)

    }



    /** */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) Log.i(
            "LogList",
            "onTouch==================================="
        )

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                //Log.i("LogList", "ACTION_DOWN");
                touchTouchUp = false

                touchDownX = event.getX(0)
                touchDownY = event.getY(0)

                firstTouchX = touchDownX
                firstTouchY = touchDownY

                if (height / 2 - textSize < touchDownY && touchDownY < height / 2 + textSize) {
                    touchClick = true
                }


                touchModeY = 0f
                touchModeY_next = 0f
                touchModeY_prev = 0f

            }

            MotionEvent.ACTION_MOVE -> {
                Log.i("LogList", "ACTION_MOVE")
                touchTouchUp = false

                val TouchX = event.getX(0)
                val TouchY = event.getY(0)

                if (!(height / 2 - textSize < TouchY && TouchY < height / 2 + textSize)) {
                    touchClick = false
                }

                //新しい移動距離
                touchModeY_next = TouchY - firstTouchY

                //新しい移動距離から前回の移動距離を引いて、今回の移動距離を取得する
                touchModeY = touchModeY_next - touchModeY_prev

                //今回の移動距離を総合的な移動距離変数に足す
                moveY = moveY + touchModeY

                //今回の移動距離を前の移動距離に直す
                touchModeY_prev = touchModeY_next
            }

            MotionEvent.ACTION_UP -> {
                Log.i("LogList", "ACTION_UP")

                val TouchX2 = event.getX(0)
                val TouchY2 = event.getY(0)

                touchClick = false

                touchTouchUp = true

                //touchModeY = 0;
                touchModeY_next = 0f
                touchModeY_prev = 0f
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i("LogList", "ACTION_CANCEL")
                touchClick = false
                touchTouchUp = false

            }
        }
        Update()
        return true
    }

    /////////////
    /** レイアウトの最低の幅・高さ=========================================  */
    private var layoutWidth = 0
    private var layoutheight = 0

    private var centerX = 0f
    private var cneterY = 0f


    private fun setPaint() {
        textPaint.textSize = textSize
        textPaint.color = color
        textPaint.isAntiAlias = true
        textPaint.strokeWidth = 1f

    }

    fun calclateSize(width: Int, height: Int) {
        layoutWidth = width
        layoutheight = height

        centerX = width / 2f
        cneterY = height / 2f

        textSize = layoutWidth / 6f

    }


    fun HankakuCount(s: String): Int {
        val sb = StringBuffer(s)

        var count = 0

        //記号を全て全角にする
        for (i in 0 until sb.length) {
            val c = sb[i]
            if (c == '(' || c == ')' || c == '~') {
                if (c == '(') {
                    sb.setCharAt(i, (c - '(' + '（'.toInt()).toChar())
                    count++
                } else if (c == ')') {
                    sb.setCharAt(i, (c - ')' + '）'.toInt()).toChar())
                    count++
                } else if (c == '~') {
                    sb.setCharAt(i, (c - '~' + '～'.toInt()).toChar())
                    count++
                }
            } else if (c >= '!' && c <= '~') {

                if (c == '！') {
                    sb.setCharAt(i, (c - '！' + '！'.toInt()).toChar())
                    count++
                } else if (c == '?') {
                    sb.setCharAt(i, (c - '?' + '?'.toInt()).toChar())
                    count++
                } else if (c == '？') {
                    sb.setCharAt(i, (c - '？' + '？'.toInt()).toChar())
                    count++
                } else {

                }


            } else {
                if (c == '"') {
                    sb.setCharAt(i, (c - '"' + '”'.toInt()).toChar())
                    count++
                } else if (c == '#') {
                    sb.setCharAt(i, (c - '#' + '＃'.toInt()).toChar())
                    count++
                } else if (c == '%') {
                    sb.setCharAt(i, (c - '%' + '％'.toInt()).toChar())
                    count++
                } else if (c == '$') {
                    sb.setCharAt(i, (c - '$' + '＄'.toInt()).toChar())
                    count++
                } else if (c == '&') {
                    sb.setCharAt(i, (c - '&' + '＆'.toInt()).toChar())
                    count++
                } else if (c == '=') {
                    sb.setCharAt(i, (c - '=' + '＝'.toInt()).toChar())
                    count++
                } else if (c == '|') {
                    sb.setCharAt(i, (c - '|' + '｜'.toInt()).toChar())
                    count++
                } else if (c == '+') {
                    sb.setCharAt(i, (c - '+' + '＋'.toInt()).toChar())
                    count++
                } else if (c == '*') {
                    sb.setCharAt(i, (c - '*' + '＊'.toInt()).toChar())
                    count++
                } else if (c == '_') {
                    sb.setCharAt(i, (c - '_' + '＿'.toInt()).toChar())
                    count++
                } else if (c == '>') {
                    sb.setCharAt(i, (c - '>' + '＞'.toInt()).toChar())
                    count++
                } else if (c == '<') {
                    sb.setCharAt(i, (c - '<' + '＜'.toInt()).toChar())
                    count++
                } else if (c == '{') {
                    sb.setCharAt(i, (c - '{' + '｛'.toInt()).toChar())
                    count++
                } else if (c == '}') {
                    sb.setCharAt(i, (c - '}' + '｝'.toInt()).toChar())
                    count++
                } else {

                }
            }
        }


        //半角カタカナを全角カタカナに変換　濁点があったらまとめる
        var t = 0
        t = 0
        while (t < sb.length - 1) {
            val originalChar1 = sb[t]
            val originalChar2 = sb[t + 1]
            val margedChar = mergeChar(originalChar1, originalChar2)
            if (margedChar != originalChar1) {
                sb.setCharAt(t, margedChar)
                sb.deleteCharAt(t + 1)
                count++
            } else {
                val convertedChar = hankakuKatakanaToZenkakuKatakana(originalChar1)
                if (convertedChar != originalChar1) {
                    sb.setCharAt(t, convertedChar)
                    count++
                }
            }
            t++
        }
        if (t < sb.length) {
            val originalChar1 = sb[t]
            val convertedChar = hankakuKatakanaToZenkakuKatakana(originalChar1)
            if (convertedChar != originalChar1) {
                sb.setCharAt(t, convertedChar)
                count++
            }
        }


        //半角英字を全角英字に変換
        for (i in 0 until s.length) {
            val c = s[i]
            if (c >= 'a' && c <= 'z') {
                sb.setCharAt(i, (c - 'a' + 'ａ'.toInt()).toChar())
                count++
            } else if (c >= 'A' && c <= 'Z') {
                sb.setCharAt(i, (c - 'A' + 'Ａ'.toInt()).toChar())
                count++
            }
        }


        //半角数字を全角数字に変換
        for (i in 0 until sb.length) {
            val c = sb[i]
            if (c >= '0' && c <= '9') {
                sb.setCharAt(i, (c - '0' + '０'.toInt()).toChar())
                count++
            }
        }


        return count
    }


    fun getTextWidth(txt: String, paint: Paint): Float {
        // 文字列の幅を取得
        return paint.measureText(txt)
    }

    fun getTextHeight_yohaku(txt: String, paint: Paint): Float {
        val s = paint.measureText(txt)
        val fontMetrics = paint.fontMetrics
        // 文字列の幅を取得
        return (fontMetrics.ascent + fontMetrics.descent) / 2
    }

    companion object {


        private val HANKAKU_KATAKANA = charArrayOf(
            '｡',
            '｢',
            '｣',
            '､',
            '･',
            'ｦ',
            'ｧ',
            'ｨ',
            'ｩ',
            'ｪ',
            'ｫ',
            'ｬ',
            'ｭ',
            'ｮ',
            'ｯ',
            'ｰ',
            'ｱ',
            'ｲ',
            'ｳ',
            'ｴ',
            'ｵ',
            'ｶ',
            'ｷ',
            'ｸ',
            'ｹ',
            'ｺ',
            'ｻ',
            'ｼ',
            'ｽ',
            'ｾ',
            'ｿ',
            'ﾀ',
            'ﾁ',
            'ﾂ',
            'ﾃ',
            'ﾄ',
            'ﾅ',
            'ﾆ',
            'ﾇ',
            'ﾈ',
            'ﾉ',
            'ﾊ',
            'ﾋ',
            'ﾌ',
            'ﾍ',
            'ﾎ',
            'ﾏ',
            'ﾐ',
            'ﾑ',
            'ﾒ',
            'ﾓ',
            'ﾔ',
            'ﾕ',
            'ﾖ',
            'ﾗ',
            'ﾘ',
            'ﾙ',
            'ﾚ',
            'ﾛ',
            'ﾜ',
            'ﾝ',
            'ﾞ',
            'ﾟ'
        )

        private val ZENKAKU_KATAKANA = charArrayOf(
            '。',
            '「',
            '」',
            '、',
            '・',
            'ヲ',
            'ァ',
            'ィ',
            'ゥ',
            'ェ',
            'ォ',
            'ャ',
            'ュ',
            'ョ',
            'ッ',
            'ー',
            'ア',
            'イ',
            'ウ',
            'エ',
            'オ',
            'カ',
            'キ',
            'ク',
            'ケ',
            'コ',
            'サ',
            'シ',
            'ス',
            'セ',
            'ソ',
            'タ',
            'チ',
            'ツ',
            'テ',
            'ト',
            'ナ',
            'ニ',
            'ヌ',
            'ネ',
            'ノ',
            'ハ',
            'ヒ',
            'フ',
            'ヘ',
            'ホ',
            'マ',
            'ミ',
            'ム',
            'メ',
            'モ',
            'ヤ',
            'ユ',
            'ヨ',
            'ラ',
            'リ',
            'ル',
            'レ',
            'ロ',
            'ワ',
            'ン',
            '゛',
            '゜'
        )

        private val HANKAKU_KATAKANA_FIRST_CHAR = HANKAKU_KATAKANA[0]

        private val HANKAKU_KATAKANA_LAST_CHAR = HANKAKU_KATAKANA[HANKAKU_KATAKANA.size - 1]

        /**
         * 半角カタカナから全角カタカナへ変換します。
         * @param c 変換前の文字
         * @return 変換後の文字
         */
        fun hankakuKatakanaToZenkakuKatakana(c: Char): Char {
            return if (c >= HANKAKU_KATAKANA_FIRST_CHAR && c <= HANKAKU_KATAKANA_LAST_CHAR) {
                ZENKAKU_KATAKANA[c - HANKAKU_KATAKANA_FIRST_CHAR]
            } else {
                c
            }
        }

        /**
         * 2文字目が濁点・半濁点で、1文字目に加えることができる場合は、合成した文字を返します。
         * 合成ができないときは、c1を返します。
         * @param c1 変換前の1文字目
         * @param c2 変換前の2文字目
         * @return 変換後の文字
         */

        fun mergeChar(c1: Char, c2: Char): Char {
            if (c2 == 'ﾞ') {
                var text = "ｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾊﾋﾌﾍﾎ"
                if (text.indexOf(c1.toInt() as Char) > 0) {
                    when (c1) {
                        'ｶ' -> return 'ガ'
                        'ｷ' -> return 'ギ'
                        'ｸ' -> return 'グ'
                        'ｹ' -> return 'ゲ'
                        'ｺ' -> return 'ゴ'
                        'ｻ' -> return 'ザ'
                        'ｼ' -> return 'ジ'
                        'ｽ' -> return 'ズ'
                        'ｾ' -> return 'ゼ'
                        'ｿ' -> return 'ゾ'
                        'ﾀ' -> return 'ダ'
                        'ﾁ' -> return 'ヂ'
                        'ﾂ' -> return 'ヅ'
                        'ﾃ' -> return 'デ'
                        'ﾄ' -> return 'ド'
                        'ﾊ' -> return 'バ'
                        'ﾋ' -> return 'ビ'
                        'ﾌ' -> return 'ブ'
                        'ﾍ' -> return 'ベ'
                        'ﾎ' -> return 'ボ'
                    }
                }
            } else if (c2 == 'ﾟ') {
                if ("ﾊﾋﾌﾍﾎ".indexOf(c1.toInt() as Char) > 0) {
                    when (c1) {
                        'ﾊ' -> return 'パ'
                        'ﾋ' -> return 'ピ'
                        'ﾌ' -> return 'プ'
                        'ﾍ' -> return 'ペ'
                        'ﾎ' -> return 'ポ'
                    }
                }
            }
            return c1
        }
    }

}