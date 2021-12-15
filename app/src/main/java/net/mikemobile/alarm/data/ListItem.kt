package net.mikemobile.alarm.data

import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import java.io.Serializable

data class ListItem(var data: Item, var sunuzuList: ArrayList<SunuzuItem>, var alarm: Alarm, var alarmFlag: Boolean):
    Serializable {

    /**
     * スキップボタンの表示・非表示判定
     */
    fun enableSKipAlarm(): Boolean {
        if (data.type == 0) {
            // １回のみ実行
            return false
        } else if (alarm.sunuzu_count == 0) {
            return true
        } else {
            return false
        }
    }

}