package net.mikemobile.alarm.database.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.CustomDateTime
import java.io.Serializable


@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    open var id: Int,           // データ保存用基本ID
    open var owner_id: Int,     // TimeItemテーブルのID

    open var type: Int,         // タイプ 0=1回のみ（年月日も含め指定し、1回のみ) 1= 2= 3=

    open var datetime: Long,    // millisecond
    open var year: Int,         // 年
    open var month: Int,        // 月
    open var day: Int,          // 日
    open var week: Int,         // 曜日 0=日 1=月 2=火 3=水 4=木 5=金 6=土
    open var hour: Int,         // 時
    open var minute: Int,       // 分

    open var sunuzu: Boolean,       //
    open var sunuzu_time: Int,       // 0=オフ 1～スヌーズ時間
    open var sunuzu_count: Int,       // 0=オフ 1～スヌーズ時間
    open var sunuzu_custom: Boolean,       //スヌーズの時間を一つずつ設定する
    open var sunuzu_endress: Boolean,       //アラーム画面で「終了」を押さない限り、スヌーズを繰り返し続ける

    open var title: String,     // タイトル


    open var sound_id: Int,     // 音声のID 0=無し 0以外=指定音声使用
    open var sound_path: String, // 音声のパス 空=指定なし 空以外=指定したパスの音声を使用 sound_pathが入ってる場合、こちらを優先する
    open var sound_name: String, // 音声のパス 空=指定なし 空以外=指定したパスの音声を使用 sound_pathが入ってる場合、こちらを優先する
    open var sound_volume: Int, // 音量 -1=システムに依存　0〜max 指定音量で再生
    open var vib: Int,

    open var step: Int,          // タイマーの動作タイミング 0=実行前 1=実行中

    open var stopMode: Boolean,
    open var stopModeSelect: Int       // 1= 停止する場合は、アラームのリスト画面から止めるか、通知領域の停止用通知から止めないとスヌーズが永続的に発生する



): Serializable {
    constructor(): this(
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
        0,
        0,
        false,
        false,
        "",
        0,
        "",
        "",
        -1,
        0,
        0,
        false,
        0){
    }

    constructor(item: Item, datetime: Long, step: Int = 0): this(
        0,item.id,item.type,
        datetime,
        CustomDateTime.getYear(datetime),
        CustomDateTime.getMonth(datetime),
        CustomDateTime.getDay(datetime),
        CustomDateTime.getWeek(datetime),
        CustomDateTime.getHour(datetime),
        CustomDateTime.getMinute(datetime),
        item.sunuzu,
        item.sunuzu_time,
        0,
        item.sunuzu_custom,
        item.sunuzu_endress,
        item.title,
        item.sound_id,
        item.sound_path,
        item.sound_name,
        item.sound_volume,
        item.vib,
        step,
        item.stopMode,
        item.stopModeSelect
        ){
    }


    constructor(item:Item):this(
        0,
        item.id,
        item.type,
        CustomDateTime.getTimeInMillis(item.year,item.month,item.day,item.hour,item.minute),
        item.year,
        item.month,
        item.day,
        item.week,
        item.hour,
        item.minute,
        item.sunuzu,
        item.sunuzu_time,
        0,
        item.sunuzu_custom,
        item.sunuzu_endress,
        item.title,
        item.sound_id,
        item.sound_path,
        item.sound_name,
        item.sound_volume,
        item.vib,
        0,
        item.stopMode,
        item.stopModeSelect){

    }

    fun getSoundType(): Constant.Companion.SoundType {
        if(!sound_path.equals("")){
            return Constant.Companion.SoundType.PATH
        }else if(sound_id != 0){
            return Constant.Companion.SoundType.ID
        }
        return Constant.Companion.SoundType.NONE
    }
    fun getAlartDateTime():String {
        val owner_date = CustomDateTime.getTimeInMillis(year,month,day,hour,minute)
        return CustomDateTime.getDateTimeText(owner_date)
        //return ("" + hour + ":" + minute)
    }
}