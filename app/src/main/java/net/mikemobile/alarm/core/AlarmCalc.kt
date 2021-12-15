package net.mikemobile.alarm.core

import android.util.Log
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.CustomDateTime

class AlarmCalc {

    companion object {
        const val TAG = "AlarmCalc"
        fun calcAlarm(item: Item, step: Int = 0, startDateTime: Long = 0L): Alarm {
            var datetime = CustomDateTime.getTimeInMillis(
                item.year,
                item.month,
                item.day,
                item.hour,
                item.minute
            )

            var time = CustomDateTime.getTimeInMillisStartHour(item.hour, item.minute)
            var this_time = CustomDateTime.getJastTimeInMillis()
            if (startDateTime > 0L) {
                this_time = startDateTime
            }

            if (item.title == "デバッグモード" && startDateTime == 0L) {
                // 開発モードのため指定日付でそのまま実行

                Log.i(TAG + " ITEM_SAVE",
                    "デバッグモード -> datetime:" + CustomDateTime.getDateTimeText(datetime))
                Log.i(TAG + " ITEM_SAVE",
                    "デバッグモード -> datetime:" + CustomDateTime.getDateTimeText(datetime))
            }else if(item.type == Constant.Companion.AlarmType.OneTime.id){

                if(datetime <= this_time) {
                    // 指定の日付が現在時刻より前

                    if (time <= this_time) {
                        // 今日の日付で指定時刻でも現在時刻より前
                        datetime = CustomDateTime.getNextDate(time, 1)
                    } else {
                        datetime = time
                    }
                } else {
                    // それ以降の場合は、指定の日付で対応する
                }
                Log.i(TAG + " ITEM_SAVE",
                    "OneTime -> datetime:" + CustomDateTime.getDateTimeText(datetime))
            }else if(item.type == Constant.Companion.AlarmType.LoopWeek.id){

                // 曜日で定めるので、常に今日の日付を基準に計算・処理を行う
                Log.i(TAG + " ITEM_SAVE","prev:" + CustomDateTime.getDateTimeText(this_time))

                val weekList = item.getWeekList()
                datetime = CustomDateTime.getNextWeekDateTime(this_time, weekList)

                Log.i(TAG + " ITEM_SAVE","next:" + CustomDateTime.getDateTimeText(datetime))


                android.util.Log.i(TAG + " ITEM_SAVE",
                    "LoopWeek -> datetime:" + CustomDateTime.getDateTimeText(datetime)
                )
            }

            return Alarm(item, datetime, step)
        }


        /**
         * sunuzuセット
         */
        fun calcNextSunuzu(alarm: Alarm, item: Item) : Alarm? {

            val next_minute = Constant.SUNUZU_TIME_LIST[item.sunuzu_time]

            var sunuzu = false
            val sunuzuCount = alarm.sunuzu_count + 1

            if(alarm.sunuzu_endress){
                sunuzu = true
            } else if (sunuzuCount < item.sunuzu_count) {
                sunuzu = true
            }

            if(sunuzu) {
                var next_time = CustomDateTime.getJastTimeInMillis()
                next_time = CustomDateTime.getNextMinute(next_time, next_minute)

                val nextAlarm = Alarm(item)
                nextAlarm.year = CustomDateTime.getYear(next_time)
                nextAlarm.month = CustomDateTime.getMonth(next_time)
                nextAlarm.day = CustomDateTime.getDay(next_time)
                nextAlarm.hour = CustomDateTime.getHour(next_time)
                nextAlarm.minute = CustomDateTime.getMinute(next_time)
                nextAlarm.datetime = next_time
                nextAlarm.sunuzu_count = sunuzuCount

                return nextAlarm
            }

            return null
        }

        /**
         * カスタムスヌーズ
         */
        fun calcNextCustomSunuzu(alarm: Alarm, item: Item, sunuzuList: List<SunuzuItem>): Alarm? {
            LogUtil.i(TAG, "setNextCustomSunuzu")

            val sunuzuCount = alarm.sunuzu_count


            var activePositions = ArrayList<Int>()
            for (i in 0 until sunuzuList.size) {
                val sunuzu = sunuzuList[i]
                if (!sunuzu.onoff) {
                    continue
                }
                activePositions.add(i)
            }

            if (activePositions.size == 0) {
                // 登録されているスヌーズが全て無効だった場合、Itemデータを流用して実行する　この場合、一律5分間隔とする
                val alarmTime =
                    CustomDateTime.getNextMinute(CustomDateTime.getJastTimeInMillis(), 5)

                val nextAlarm = Alarm(item)
                nextAlarm.datetime = alarmTime
                nextAlarm.year = CustomDateTime.getYear(alarmTime)
                nextAlarm.month = CustomDateTime.getMonth(alarmTime)
                nextAlarm.day = CustomDateTime.getDay(alarmTime)
                nextAlarm.hour = CustomDateTime.getHour(alarmTime)
                nextAlarm.minute = CustomDateTime.getMinute(alarmTime)
                nextAlarm.sunuzu_count = sunuzuCount
                nextAlarm.sound_id = item.sound_id
                nextAlarm.sound_name = item.sound_name

                if (item.sound_path != null && !item.sound_path.equals("")) {
                    nextAlarm.sound_path = item.sound_path
                }
                return nextAlarm
            }

            val lastItemPosition = activePositions.size - 1
            if (sunuzuCount >= activePositions[lastItemPosition]) {
                // 最後の項目が選ばれているため、最後のアイテムでスヌーズをセットする
                val sunuzu = sunuzuList[activePositions[lastItemPosition]]

                val alarmTime = CustomDateTime.getNextMinute(
                    CustomDateTime.getJastTimeInMillis(),
                    sunuzu.plusMinute
                )

                val nextAlarm = Alarm(item)
                nextAlarm.datetime = alarmTime
                nextAlarm.year = CustomDateTime.getYear(alarmTime)
                nextAlarm.month = CustomDateTime.getMonth(alarmTime)
                nextAlarm.day = CustomDateTime.getDay(alarmTime)
                nextAlarm.hour = CustomDateTime.getHour(alarmTime)
                nextAlarm.minute = CustomDateTime.getMinute(alarmTime)
                nextAlarm.sunuzu_count = lastItemPosition
                nextAlarm.sound_id = sunuzu.sound_id
                nextAlarm.sound_name = sunuzu.sound_name

                if (sunuzu.sound_path != null && !sunuzu.sound_path.equals("")) {
                    nextAlarm.sound_path = sunuzu.sound_path
                }
                return nextAlarm
            }

            for (p in activePositions) {
                if (sunuzuCount >= p) {
                    // すでに使用済みなので次に進む
                    continue
                }

                val sunuzu = sunuzuList[p]

                val alarmTime = CustomDateTime.getNextMinute(
                    CustomDateTime.getJastTimeInMillis(),
                    sunuzu.plusMinute
                )

                val nextAlarm = Alarm(item)
                nextAlarm.datetime = alarmTime
                nextAlarm.year = CustomDateTime.getYear(alarmTime)
                nextAlarm.month = CustomDateTime.getMonth(alarmTime)
                nextAlarm.day = CustomDateTime.getDay(alarmTime)
                nextAlarm.hour = CustomDateTime.getHour(alarmTime)
                nextAlarm.minute = CustomDateTime.getMinute(alarmTime)
                nextAlarm.sunuzu_count = p
                nextAlarm.sound_id = sunuzu.sound_id
                nextAlarm.sound_name = sunuzu.sound_name

                if (sunuzu.sound_path != null && !sunuzu.sound_path.equals("")) {
                    nextAlarm.sound_path = sunuzu.sound_path
                }
                return nextAlarm
            }

            return null
        }
    }
}