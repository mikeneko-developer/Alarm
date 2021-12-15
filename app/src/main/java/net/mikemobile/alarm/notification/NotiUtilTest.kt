package net.mikemobile.alarm.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NotiUtilTest: NotiUtil() {
    // CHANNLE ID
    override val NOTIFICATION_CANNEL_ID: String
        get() = "com.mikeneko.alarm.notification_cannel_test"
    // 通知の名称
    override val NOTIFICATION_CANNEL_NAME: String
        get() = "定期ループ処理通知用"
    // チャンネルの説明
    override val NOTIFICATION_CANNEL_DESCRIPTION: String
        get() = "1分、または1時間ごとのループ通知用"
    // 通知の種類
    override val NOTIFICATION_TYPE: Int
        get() = NotificationManager.IMPORTANCE_LOW

    //NotificationManager.IMPORTANCE_DEFAULT
    //NotificationManager.IMPORTANCE_LOW

    override fun getNotificationData(): NotificationData {
        return NotificationData(
                NOTIFICATION_ID
        )
    }

    private val NOTIFICATION_ID = 13


    fun show(context: Context, title: String, message: String) {
        val notificationData = NotificationData(
                NOTIFICATION_ID,
                title,
                message
        )

        showNotification(context, notificationData)
    }

    fun delete(context: Context) {
        val notificationData = NotificationData(
                NOTIFICATION_ID
        )

        deleteNotification(context, notificationData)
    }



}