package net.mikemobile.alarm.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import net.mikemobile.alarm.R

@RequiresApi(Build.VERSION_CODES.O)
class NotiUtilService: NotiUtil() {
    // CHANNLE ID
    override val NOTIFICATION_CANNEL_ID: String
        get() = "com.mikeneko.alarm.notification_cannel_service"
    // 通知の名称
    override val NOTIFICATION_CANNEL_NAME: String
        get() = "サービス稼働の通知"
    // チャンネルの説明
    override val NOTIFICATION_CANNEL_DESCRIPTION: String
        get() = "チャンネルの説明"
    // 通知の種類
    override val NOTIFICATION_TYPE: Int
        get() = NotificationManager.IMPORTANCE_HIGH
    //NotificationManager.IMPORTANCE_HIGH
    //NotificationManager.IMPORTANCE_DEFAULT
    //NotificationManager.IMPORTANCE_LOW

    override fun getNotificationData(): NotificationData {
        val notificationData = NotificationData(
            NOTIFICATION_ID
        )
        notificationData.autoCancel = false
        return notificationData
    }

    val NOTIFICATION_ID = 10

    fun show(context: Context, title: String, message: String, buttons: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()) {
        val notificationData = NotificationData(
            NOTIFICATION_ID,
            title,
            message,
            R.drawable.ic_launcher_foreground,
            buttons
        )

        notificationData.autoCancel = false

        showNotificationToNoClear(context, notificationData)
    }

    fun show(context: Context, notification: Notification) {
        showNotification(context, notification, NOTIFICATION_ID)
    }

    fun delete(context: Context) {
        val notificationData = NotificationData(
                NOTIFICATION_ID
        )

        deleteNotification(context, notificationData)
    }



}