package net.mikemobile.alarm.database.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.mikemobile.alarm.util.CustomDateTime
import java.io.Serializable
import java.util.*

@Entity(tableName = "sunuzuItem")
data class SunuzuItem(
    @PrimaryKey(autoGenerate = true)
    open var id: Int,           // データ保存用基本ID
    open var owner_id: Int,     // サブアイテムの場合、オーナーとなるアイテムのIDが格納される（オーナーアイテムなら0）
    open var onoff: Boolean,       // スイッチ 0=オフ 1=オン

    open var title: String,

    open var year: Int,         // 年
    open var month: Int,        // 月
    open var day: Int,          // 日
    open var week: Int,         // 曜日 0=日 1=月 2=火 3=水 4=木 5=金 6=土
    open var hour: Int,         // 時
    open var minute: Int,       // 分

    open var plusMinute: Int,         // 何分後に追加するか

    open var sound: Boolean,     // false:親と同じ true:独自に指定
    open var sound_id: Int,     // 音声のID 0=無し 0以外=指定音声使用
    open var sound_path: String, // 音声のパス 空=指定なし 空以外=指定したパスの音声を使用 sound_pathが入ってる場合、こちらを優先する
    open var sound_name: String, // 音声のタイトル
    open var vib: Int

    ): Serializable {
    constructor(): this(
        0,
        0,
        true,
        "",
        0,
        0,
        0,
        0,
        0,
        0,
        5,
        false,
        0,
        "",
        "",
        0){

        year = CustomDateTime.getYear()
        month = CustomDateTime.getYear()
        day = CustomDateTime.getYear()
    }

    fun getTimeText(): String{
        var owner_date = CustomDateTime.getTimeInMillis(year,month,day,hour,minute)
        return CustomDateTime.getDateTimeText(owner_date)
    }
}