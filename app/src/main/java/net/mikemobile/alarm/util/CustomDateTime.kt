package net.mikemobile.alarm.util

import android.util.Log
import net.mikemobile.alarm.core.AlarmCalc
import java.util.*

class CustomDateTime{

    companion object{
        fun getTimeInMillis(): Long {
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.getTimeInMillis()
        }

        fun getJastTimeInMillis(): Long {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.set(Calendar.SECOND,0)
            mCalendar.set(Calendar.MILLISECOND,0)
            return mCalendar.getTimeInMillis()
        }

        fun getTimeInMillis(year:Int,month:Int,day:Int,hour:Int,minute:Int): Long {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.set(year,month - 1,day,hour,minute,0)
            mCalendar.set(Calendar.MILLISECOND,0)
            return mCalendar.getTimeInMillis()
        }

        fun getTimeInMillis(year:Int,month:Int,day:Int): Long {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.set(year,month - 1,day,0,0,0)
            mCalendar.set(Calendar.MILLISECOND,0)
            return mCalendar.getTimeInMillis()
        }

        fun getTimeInMillisStartHour(hour:Int,minute:Int): Long {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.set(Calendar.HOUR_OF_DAY,hour)
            mCalendar.set(Calendar.MINUTE,minute)
            mCalendar.set(Calendar.SECOND,0)
            mCalendar.set(Calendar.MILLISECOND,0)
            return mCalendar.getTimeInMillis()
        }

        fun getNextDate(time:Long,move:Int): Long{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            mCalendar.add(Calendar.DAY_OF_MONTH, move)
            return mCalendar.getTimeInMillis()
        }

        fun getNextMinute(time:Long,move:Int): Long{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            mCalendar.add(Calendar.MINUTE, move)
            return mCalendar.getTimeInMillis()
        }


        fun getYear(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.YEAR)
        }
        fun getYear(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.YEAR)
        }
        fun getYearText(): String {
            return getYearText(getYear())
        }
        fun getYearText(time:Long): String {
            val year = getYear(time)
            return getYearText(year)
        }
        fun getYearText(year:Int): String {
            val year_1000 = if(year < 1000){"0"}else{""}
            val year_100 = if(year < 100){"0"}else{""}
            val year_10 = if(year < 10){"0"}else{""}

            return "" + year_1000 + year_100 + year_10 + year
        }

        fun getMonth(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.MONTH) + 1
        }
        fun getMonth(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.MONTH) + 1
        }
        fun getMonthText(): String {
            return getMonthText(getMonth())
        }
        fun getMonthText(time:Long): String {
            val month = getMonth(time)
            return getMonthText(month)
        }
        fun getMonthText(month:Int): String {
            val month_10 = if(month < 10){"0"}else{""}
            return month_10 + month
        }

        fun getDay(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.DAY_OF_MONTH)
        }
        fun getDay(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.DAY_OF_MONTH)
        }
        fun getDayText(): String {
            val day = getDay()
            return getDayText(day)
        }
        fun getDayText(time:Long): String {
            return getDayText(getDay(time))
        }
        fun getDayText(day:Int): String {
            val day_10 = if(day < 10){"0"}else{""}
            return day_10 + day
        }

        fun getWeek(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.DAY_OF_WEEK)
        }
        fun getWeek(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.DAY_OF_WEEK)
        }

        fun getHour(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.HOUR_OF_DAY)
        }
        fun getHour(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.HOUR_OF_DAY)
        }
        fun getHourText(): String {
            val hour = getHour()
            return getHourText(hour)
        }
        fun getHourText(time:Long): String {
            return getHourText(getHour(time))
        }
        fun getHourText(hour:Int): String {
            val hour_10 = if(hour < 10){"0"}else{""}
            return hour_10 + hour
        }

        fun getMinute(): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            return mCalendar.get(Calendar.MINUTE)
        }
        fun getMinute(time:Long): Int{
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.MINUTE)
        }
        fun getMinuteText(): String {
            val minute = getMinute()
            return getMinuteText(minute)
        }
        fun getMinuteText(time:Long): String {
            return getMinuteText(getMinute(time))

        }
        fun getMinuteText(minute:Int): String {
            val minute_10 = if(minute < 10){"0"}else{""}

            return minute_10 + minute
        }
        fun getSecond(time:Long): Int {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.SECOND)
        }
        fun getMilliSecond(time:Long): Int {
            var mCalendar: Calendar = Calendar.getInstance()
            mCalendar.timeInMillis = time
            return mCalendar.get(Calendar.MILLISECOND)
        }

        fun maxDay(year: Int,month: Int): Int {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        fun getWeekToText(week:Int): String{
            return when(week){
                1 -> return "日"
                2 -> return "月"
                3 -> return "火"
                4 -> return "水"
                5 -> return "木"
                6 -> return "金"
                7 -> return "土"
                else -> ""
            }
        }

        fun getDateTimeText(time:Long): String {

            val year = getYear(time)
            val month = getMonth(time)
            val day = getDay(time)
            val week = getWeek(time)
            val hour = getHour(time)
            val minute = getMinute(time)

            val month_10 = if(month < 10){"0"}else{""}
            val day_10 = if(day < 10){"0"}else{""}
            val hour_10 = if(hour < 10){"0"}else{""}
            val minute_10 = if(minute < 10){"0"}else{""}

            return "" + year + "/" + month_10 + month + "/" + day_10 + day + "(" + getWeekToText(week) + ") " + hour_10 + hour + ":" + minute_10 + minute
        }

        fun getTimeText(time:Long): String {

            val hour = getHour(time)
            val minute = getMinute(time)

            val hour_10 = if(hour < 10){"0"}else{""}
            val minute_10 = if(minute < 10){"0"}else{""}

            return hour_10 + hour + ":" + minute_10 + minute
        }

        fun getDateTimeText(): String {
            val time = getTimeInMillis()
            val year = getYear(time)
            val month = getMonth(time)
            val day = getDay(time)
            val week = getWeek(time)
            val hour = getHour(time)
            val minute = getMinute(time)
            val second = getSecond(time)
            val millisecond = getMilliSecond(time)

            val month_10 = if(month < 10){"0"}else{""}
            val day_10 = if(day < 10){"0"}else{""}
            val hour_10 = if(hour < 10){"0"}else{""}
            val minute_10 = if(minute < 10){"0"}else{""}
            val second_10 = if(second < 10){"0"}else{""}
            val msecond_10 = if(millisecond < 10){"0"}else{""}
            val msecond_100 = if(millisecond < 100){"0"}else{""}

            return "" + year + "/" + month_10 + month + "/" + day_10 + day + " " +
                    "" + hour_10 + hour + ":" + minute_10 + minute + ":" + second_10 + second + ":" + msecond_100 + msecond_10 + millisecond
        }



        fun getDateText(): String {
            val time = getTimeInMillis()
            val year = getYear(time)
            val month = getMonth(time)
            val day = getDay(time)

            val month_10 = if(month < 10){"0"}else{""}
            val day_10 = if(day < 10){"0"}else{""}

            return "" + year + "-" + month_10 + month + "-" + day_10 + day
        }

        /**
         */
        fun getNextWeekDateTime(time: Long, week: ArrayList<Int>): Long {

            // 今日の日付を取得する
            val year = getYear(time)
            val month = getMonth(time)
            val day = getDay(time)

            // 時・分は変えずにミリ秒を取得する
            var datetime = getTimeInMillis(year, month, day, getHour(time), getMinute(time))

            // 曜日を取得
            val this_week = getWeek(datetime)

            var move = 0
            var start = 0
            var end = 6
            Log.i(AlarmCalc.TAG,"    time:" + getDateTimeText(time))
            Log.i(AlarmCalc.TAG,"datetime:" + getDateTimeText(datetime))

            if (datetime < getJastTimeInMillis()) {
                Log.i(AlarmCalc.TAG,"datetimeが今日の指定時刻と同じかそれ未満なので明日以降にする")
                start = 1
                end = 7
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

            datetime = getNextDate(datetime, move)

            return datetime
        }



        /**
         */
        fun getPrevWeekDateTime(time: Long, week: ArrayList<Int>): Long {
            val today = getTimeInMillis(getYear(), getMonth(), getDay(), getHour(time), getMinute(time))

            // 今日の日付を取得する
            val year = getYear(time)
            val month = getMonth(time)
            val day = getDay(time)

            // 時・分は変えずにミリ秒を取得する
            var datetime = getTimeInMillis(year, month, day, getHour(time), getMinute(time))

            // 曜日を取得
            val this_week = getWeek(datetime)

            var move = 0
            val start = 1
            val end = 7

            // 次の指定曜日を確認し、次までの移動日を確認する
            for (count in start..end) {
                var next_week = this_week - count

                android.util.Log.i(
                    "ItemLog",
                    "this_week:" + this_week + "  next_week:" + next_week
                )

                if (next_week < 0) {
                    next_week = 7 + next_week
                }

                if (next_week == Calendar.SUNDAY && week[0] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.MONDAY && week[1] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.TUESDAY && week[2] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.WEDNESDAY && week[3] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.THURSDAY && week[4] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.FRIDAY && week[5] == 1) {
                    move -= count
                    break
                } else if (next_week == Calendar.SATURDAY && week[6] == 1) {
                    move -= count
                    break
                }
            }
            datetime = getNextDate(datetime, move)

            android.util.Log.i(
                "TESTTESTTESTTEST",
                "datetime:" + CustomDateTime.getDateTimeText(datetime)
            )

            android.util.Log.i(
                "TESTTESTTESTTEST",
                "today:" + CustomDateTime.getDateTimeText(today)
            )
            if(today >= datetime) {
                // 取得した日が今日かそれよりも前の日の場合、今日から最初の予定日で計算し直す

                android.util.Log.i(
                    "TESTTESTTESTTEST",
                    "今日を含めて日付が古い"
                )
                datetime = getNextWeekDateTime(today, week)
            }

            android.util.Log.i(
                "TESTTESTTESTTEST",
                "datetime2:" + CustomDateTime.getDateTimeText(datetime)
            )
            android.util.Log.i(
                "TESTTESTTESTTEST",
                "datetime1:" + datetime
            )
            android.util.Log.i(
                "TESTTESTTESTTEST",
                "datetime2:" + today
            )
            return datetime
        }
    }


}