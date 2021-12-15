package net.mikemobile.alarm.database.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.Constant.Companion.SUNUZU_TIME_LIST
import net.mikemobile.alarm.util.CustomDateTime
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    open var id: Int,           // データ保存用基本ID
    open var owner_id: Int,     // サブアイテムの場合、オーナーとなるアイテムのIDが格納される（オーナーアイテムなら0）
    open var type: Int,         // タイプ 0=1回のみ（年月日も含め指定し、1回のみ) 1=曜日繰り返し 2= 3=
    open var onoff: Boolean,       // スイッチ 0=オフ 1=オン

    open var year: Int,         // 年
    open var month: Int,        // 月
    open var day: Int,          // 日
    open var week: Int,         // 曜日 0=日 1=月 2=火 3=水 4=木 5=金 6=土
    open var hour: Int,         // 時
    open var minute: Int,       // 分

    open var week_sun: Int,     // 曜日指定　日
    open var week_mon: Int,     // 曜日指定　月
    open var week_tue: Int,     // 曜日指定　火
    open var week_wed: Int,     // 曜日指定　水
    open var week_the: Int,     // 曜日指定　木
    open var week_fri: Int,     // 曜日指定　金
    open var week_sat: Int,     // 曜日指定　土

    open var sunuzu: Boolean,       //
    open var sunuzu_time: Int,       // 1～スヌーズ時間
    open var sunuzu_count: Int,       // 1～スヌーズ時間
    open var sunuzu_custom: Boolean,       //スヌーズの時間を一つずつ設定する
    open var sunuzu_endress: Boolean,       //アラーム画面で「終了」を押さない限り、スヌーズを繰り返し続ける

    open var title: String,     // タイトル

    open var sound_id: Int,     // 音声のID 0=無し 0以外=指定音声使用
    open var sound_path: String, // 音声のパス 空=指定なし 空以外=指定したパスの音声を使用 sound_pathが入ってる場合、こちらを優先する
    open var sound_name: String, // 音声のタイトル
    open var sound_volume: Int, // ボリューム

    open var vib: Int,                   // バイブレーション

    open var stopMode: Boolean ,      // 1= 停止する場合は、アラームのリスト画面から止めるか、通知領域の停止用通知から止めないとスヌーズが永続的に発生する
    open var stopModeSelect: Int       // 1= 停止する場合は、アラームのリスト画面から止めるか、通知領域の停止用通知から止めないとスヌーズが永続的に発生する

): Serializable {
    constructor(): this(
        0,
        0,
        0,
        true,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        false,
        4,
        1,
        false,
        false,
        "",
        0,
        "",
        "",
        -1,
        0,
        false,
        0){
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun getDateTime(): Long {
        return CustomDateTime.getTimeInMillis(year,month,day,hour,minute)
    }

    fun getWeekList(): ArrayList<Int> {
        return arrayListOf(week_sun,week_mon,week_tue,week_wed,week_the,week_fri,week_sat)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // リスト表示用
    fun getAlartDateTime():String {
        val owner_date = CustomDateTime.getTimeInMillis(year,month,day,hour,minute)
        return CustomDateTime.getTimeText(owner_date)
        //return ("" + hour + ":" + minute)
    }

    fun getWeekText(): String{
        var week_text = ""
        if(type == Constant.Companion.AlarmType.LoopWeek.id){
            if(week_sun == 1){week_text += "日"}
            if(week_mon == 1){week_text += "月"}
            if(week_tue == 1){week_text += "火"}
            if(week_wed == 1){week_text += "水"}
            if(week_the == 1){week_text += "木"}
            if(week_fri == 1){week_text += "金"}
            if(week_sat == 1){week_text += "土"}
        } else if (type == Constant.Companion.AlarmType.OneTime.id){
            week_text = CustomDateTime.getDateTimeText(CustomDateTime.getTimeInMillis(year,month,day,hour,minute))
        }

        return week_text
    }

    fun setSunuzuCountCalc(calc:Int){
        var count = sunuzu_count + calc

        if(count < 1){
            count = 99
        }else if(count > 20){
            if(20 < count && count < 99){
                if(calc > 0) {
                    count = 99
                }else {
                    count = 20
                }
            }else if(count > 99){
                count = 1
            }
        }
        sunuzu_count = count
    }

    fun setSunuzuTimeCalc(calc:Int){
        var time = sunuzu_time + calc

        if(time < 0){
            time = SUNUZU_TIME_LIST.size - 1
        }else if(time >= SUNUZU_TIME_LIST.size){
            time = 0
        }
        sunuzu_time = time

        if(sunuzu_time == SUNUZU_TIME_LIST.size - 1){
            sunuzu_custom = true
        }else {
            sunuzu_custom = false
        }
    }

    fun getSunuzuType(): String{
        if(sunuzu_custom) {
            return "カスタマイズ"
        }

        if(sunuzu) {
            val time = "" + SUNUZU_TIME_LIST[sunuzu_time] + "分ごとに"
            val count = if(sunuzu_count == 99) "無制限" else "" + sunuzu_count + "回"

            return "" + time + count
        }


        return "なし"
    }
}