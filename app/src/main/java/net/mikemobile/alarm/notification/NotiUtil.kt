package net.mikemobile.alarm.notification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.services.TimeReceiver

@RequiresApi(Build.VERSION_CODES.O)
abstract class NotiUtil {
    abstract val NOTIFICATION_CANNEL_ID: String
    abstract val NOTIFICATION_CANNEL_NAME: String
    abstract val NOTIFICATION_CANNEL_DESCRIPTION: String
    abstract val NOTIFICATION_TYPE: Int
    abstract fun getNotificationData(): NotificationData

    var builder: Notification.Builder? = null
    var notificationManager: NotificationManager? = null

    fun getNotificationData(title: String, message: String, buttons: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()): NotificationData {
        val data = getNotificationData()
        data.title = title
        data.message = message
        data.buttons = buttons
        return data
    }

    fun checkEnabled(context: Context): Boolean {
        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        return false
    }

    /**
     * 指定パッケージ（自アプリのパッケージ）のNotificationの存在チェック
     */
    fun enableNotification(context: Context): Boolean {
        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val activeNotifications = notificationManager!!.getActiveNotifications()
        if (activeNotifications.size > 0) {
            for(noti in activeNotifications) {
                //LogUtil.d(TimeReceiver.TAG, "packageName : " + noti.packageName)
                //LogUtil.d(TimeReceiver.TAG, "context : " + context.packageName)

                if (noti.packageName == context.packageName) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Channelのセット
     */
    fun setChannel(context: Context) {

        val channel = NotificationChannel(
                NOTIFICATION_CANNEL_ID,
                NOTIFICATION_CANNEL_NAME,
                NOTIFICATION_TYPE
        )

        channel.setDescription(NOTIFICATION_CANNEL_DESCRIPTION)
        channel.enableVibration(false)
        //channel.canShowBadge()
        channel.enableLights(true)
        channel.setLightColor(Color.BLUE)
        // the channel appears on the lockscreen
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE)
        //channel.setSound(defaultSoundUri, null)
        //channel.setShowBadge(true)

        if (notificationManager == null)notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager!!.createNotificationChannel(channel)
    }

    /**
     * Notificationクラスの生成
     */
    fun getNotification(context: Context, title: String, message: String, buttons: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()): Notification {
        return getNotification(context, getNotificationData(title, message, buttons))
    }
    fun getNotification(context: Context, data: NotificationData): Notification {
        val builder = Notification.Builder(context, NOTIFICATION_CANNEL_ID)

        builder.setContentTitle(data.title)
        builder.setContentText(data.message)
        builder.setSmallIcon(data.smallIcon)
        builder.setAutoCancel(data.autoCancel)
        builder.setCategory(NotificationCompat.CATEGORY_ALARM)

        //builder.setContentIntent(pendingIntent)
        var requestCode = 10001

        for(buttoon in data.buttons) {
            val intent = Intent(buttoon[1])
            intent.setPackage(context.packageName)

            val action = Notification.Action(
                R.drawable.ic_media_pause,
                buttoon[0],
                PendingIntent.getBroadcast(
                    context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            requestCode++

            builder.addAction(action)
        }

        return builder.build()
    }

    /**
     * Notificationの実行（通知の表示）
     */
    protected fun showNotification(context: Context, data: NotificationData) {
        val notification = getNotification(context, data)

        var requestCode = 10001

        val intent = Intent(context, MainActivity::class.java)
        intent.setPackage(context.packageName)

        notification.contentIntent = PendingIntent.getBroadcast(
            context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager!!.notify(data.notificationId, notification)



    }

    /**
     * スワイプで削除しないNotificationの実行（通知の表示）
     */
    protected fun showNotificationToNoClear(context: Context, data: NotificationData) {
        val notification = getNotification(context, data)
        notification.flags = Notification.FLAG_NO_CLEAR

        NotificationManagerCompat.from(context).notify(data.notificationId, notification)

    }

    protected fun showNotification(context: Context, notification: Notification, notificationId: Int) {

        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager!!.notify(notificationId, notification)
    }

    /**
     * Notificationの削除（通知の削除）
     */
    protected fun deleteNotification(context: Context, data: NotificationData) {
        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager!!.cancel(data.notificationId)
    }

    /**
     * Channelの削除
     * ※foreground serviceに設定した場合は呼ばないこと
     */
    fun clearChannel(context: Context) {
        if (notificationManager == null)notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager!!.deleteNotificationChannel(NOTIFICATION_CANNEL_ID)
    }



}