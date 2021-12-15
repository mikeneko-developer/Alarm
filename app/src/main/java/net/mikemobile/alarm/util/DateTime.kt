package net.mikemobile.alarm.util

import java.util.*

class DateTime {

    companion object {
        fun get(type: Int, calendar: Calendar = Calendar.getInstance()) :Int {
            return calendar.get(type)
        }

        fun calcMilliSecond(num: Int, plus: Int, calendar: Calendar = Calendar.getInstance()): Int {
            val calendar = Calendar.getInstance()
            val max = calendar.timeInMillis.toInt()
            if (plus > max) return -1
            return calc(num, plus, max)
        }

        fun calcHour(num: Int, plus: Int): Int {
            val max = 24
            if (plus > max) return -1
            return calc(num, plus, max)
        }

        fun calcMinute(num: Int, plus: Int): Int {
            val max = 60
            if (plus > max) return -1
            return calc(num, plus, max)
        }

        fun calcSecond(num: Int, plus: Int): Int {
            val max = 60
            if (plus > max) return -1
            return calc(num, plus, max)
        }

        private fun calc(num: Int, plus: Int, max: Int): Int {
            var minus = 0

            val count = max / plus
            for (n in 1..count) {
                if (num < (n * plus)) {
                    minus = (n * plus)
                    break
                }
            }

            return minus - num
        }
    }
}