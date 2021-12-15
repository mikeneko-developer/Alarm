package net.mikemobile.alarm.util

import net.mikemobile.alarm.R


class Constant{
    companion object{
        const val INTENT_STOP_ALARM = "net.mikemobile.myaction.alarm.stop"
        const val INTENT_SUNUZU_ALARM = "net.mikemobile.myaction.alarm.sunuzu"
        var ENABLE_ALARM = false
        var ENABLE_ALARM_SERVICE = false


        enum class AlarmType(val id :Int, val text: String,image_resource: Int)  {
            OneTime(0, "1回", R.mipmap.ic_launcher_round),
            LoopDay(1, "毎日", R.mipmap.ic_launcher_round),
            LoopWeek(2, "曜日", R.mipmap.ic_launcher_round),
            LoopMonth(3, "毎月", R.mipmap.ic_launcher_round),
            LoopYear(4, "毎年", R.mipmap.ic_launcher_round),
            SelectDateTime(5, "指定日", R.mipmap.ic_launcher_round)
        }

        // スヌーズの時間
        val SUNUZU_TIME_TEXT_LIST = arrayOf(
            "1分","2分","3分","4分","5分","6分","7分","8分","9分",
            "10分","15分","20分","30分","40分","50分","60分","90分","120分",
            "指定"
        )
        val SUNUZU_TIME_LIST = arrayOf(
            1,2,3,4,5,6,7,8,9,
            10,15,20,30,40,50,60,90,120,
            999
        )

        val SUNUZU_COUNT_MUGEN = 99


        enum class SoundType(val id :Int, val text: String)  {
            NONE(0, "なし"),
            PATH(1, "ファイルパス"),
            ID(2, "音声ID")
        }


        enum class ServiceCommand(val command: String)  {
            CHECK("check-alarm"),
            RECHECK("recheck-alarm"),
            DELETE("delete-alarm"),

            ALARM_LOAD("alarm-load"),
            ALARM_STOP("alarm-stop"),
            ALARM_END("alarm-end"),

            TEST_PLAY_MUSIC("play-music"),
            TEST_STOP_MUSIC("stop-music"),

            UPDATE("update-alarm")
        }

        val VIB_LIST = arrayOf(
            longArrayOf(0),
            longArrayOf(300,300,300,300,300,300,300,300,300,300),
            longArrayOf(600,300,600,300,600,300,600,300,600,300),
            longArrayOf(600,300,300,300,600,300,300,300,600,300,300,300),
            longArrayOf(100,300,100,300,100,300,100,300,100,300),
            longArrayOf(300,100,300,100,300,100,300,100,300,100,300,100)
        )

        val VIB_LIST_TIME = longArrayOf(
            0,
            5000,
            5000,
            5000,
            5000,
            5000
        )

        enum class AlarmStopType(type: Int) {
            NONE(0),
            ALARM(1),
            SUNUZU(2),
        }

        // --------------------------------------------------------
        // OAuth認証の種類
        val OAUTH_TYPE_QUERY_PARAM = 1
        val OAUTH_TYPE_HEADER = 2

        // 通信メソッド名
        val METHOD_TYPE_GET = "GET"
        val METHOD_TYPE_POST = "POST"

        // --------------------------------------------------------
        // 基本URL
        val API_BASE_URL = "https://test.core.its-mo.com/zmaps/api/apicore/core/v1_0/"


    }

}