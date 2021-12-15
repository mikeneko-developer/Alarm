package net.mikemobile.alarm.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NotiUtilAlarm: NotiUtil() {
    // CHANNLE ID
    override val NOTIFICATION_CANNEL_ID: String
        get() = "com.mikeneko.alarm.notification_cannel_alarm"
    // 通知の名称
    override val NOTIFICATION_CANNEL_NAME: String
        get() = "アラーム用通知"
    // チャンネルの説明
    override val NOTIFICATION_CANNEL_DESCRIPTION: String
        get() = "チャンネルの説明"

    // 通知の種類
    override val NOTIFICATION_TYPE: Int
        get() = NotificationManager.IMPORTANCE_LOW

    //NotificationManager.IMPORTANCE_DEFAULT
    //NotificationManager.IMPORTANCE_LOW

    override fun getNotificationData(): NotificationData {
        val data = NotificationData(
            NOTIFICATION_ID
        )
        data.autoCancel = false
        return data
    }

    private val NOTIFICATION_ID = 11


    fun show(context: Context, title: String, message: String) {
        val notificationData = NotificationData(
                NOTIFICATION_ID,
                title,
                message
        )

        showNotificationToNoClear(context, notificationData)
    }

    fun delete(context: Context) {
        val notificationData = NotificationData(
                NOTIFICATION_ID
        )

        deleteNotification(context, notificationData)
    }



}