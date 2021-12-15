package net.mikemobile.alarm.time

import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.CustomDateTime
import java.util.*

class AlarmTimeManager {

    companion object {
        //

        /**
         * item: 登録されたアイテム
         * todayEnable: 今日の日付も対象に入れるかどうかの判断
         * startDateTime: 開始日時を指定する
         */
        fun nextAlarmDateTime(item: Item, startDateTime: Long = 0L): Long {
            val todayEnable = false


            var datetime = item.getDateTime()

            if(item.type == Constant.Companion.AlarmType.LoopDay.id){
                var today = CustomDateTime.getTimeInMillis()
                if (startDateTime > 0L) {
                    today = startDateTime
                }

                datetime = CustomDateTime.getNextDate(CustomDateTime.getTimeInMillis(
                    CustomDateTime.getYear(today),
                    CustomDateTime.getMonth(today),
                    CustomDateTime.getDay(today),
                    CustomDateTime.getHour(datetime),
                    CustomDateTime.getMinute(datetime)
                    ),1)

            }else if(item.type == Constant.Companion.AlarmType.LoopWeek.id){
                val weekList = arrayListOf(
                    item.week_sun,
                    item.week_mon,
                    item.week_tue,
                    item.week_wed,
                    item.week_the,
                    item.week_fri,
                    item.week_sat
                )

                datetime = getNextWeekDateTime(item.getDateTime(), weekList, todayEnable, startDateTime)
            }

            return datetime
        }

        /**
         * defaultDateTime: 最初の日付
         * week: 曜日の使用・未使用を判断するための配列
         * todayEnable: 現在日付を判断材料に入れる
         * startDateTime: 開始日付の値を入れる　（0Lの場合は判断しない
         */
        fun getNextWeekDateTime(
            defaultDateTime: Long,
            week: ArrayList<Int>,
            todayEnable: Boolean = false,
            startDateTime: Long = 0L
        ): Long {

            // 今日の日付を取得する
            var year = CustomDateTime.getYear()
            var month = CustomDateTime.getMonth()
            var day = CustomDateTime.getDay()

            // 開始日時に指定があれば、開始の年月日を代入する
            if (startDateTime > 0) {
                year = CustomDateTime.getYear(startDateTime)
                month = CustomDateTime.getMonth(startDateTime)
                day = CustomDateTime.getDay(startDateTime)
            }

            // 時・分は変えずに次の曜日のミリ秒を取得する
            var datetime = CustomDateTime.getTimeInMillis(
                year,
                month,
                day,
                CustomDateTime.getHour(defaultDateTime),
                CustomDateTime.getMinute(defaultDateTime)
            )

            // 曜日を取得
            val this_week = CustomDateTime.getWeek(datetime)

            var move = if (todayEnable) {
                0
            } else {
                1
            }

            val start = if (todayEnable) {
                0
            } else {
                1
            }
            val end = if (todayEnable) {
                6
            } else {
                7
            }

            // 次の指定曜日を確認し、次までの移動日を確認する
            for (count in start..end) {
                var next_week = this_week + count

                android.util.Log.i(
                    "ItemLog",
                    "this_week:" + this_week + "  next_week:" + next_week
                )

                if (next_week > 7) {
                    next_week = next_week - 7
                }

                if (next_week == Calendar.SUNDAY && week[0] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.MONDAY && week[1] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.TUESDAY && week[2] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.WEDNESDAY && week[3] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.THURSDAY && week[4] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.FRIDAY && week[5] == 1) {
                    move = count
                    break
                } else if (next_week == Calendar.SATURDAY && week[6] == 1) {
                    move = count
                    break
                }
            }

            datetime = CustomDateTime.getNextDate(datetime, move)

            return datetime
        }
    }
}