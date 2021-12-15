package net.mikemobile.alarm.notification

import net.mikemobile.alarm.R

data class NotificationData(
        val notificationId: Int,
        var title: String = "",
        var message: String = "",
        var smallIcon: Int = R.drawable.alarm_notification,
        var buttons: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
) {
    var autoCancel: Boolean = true // 通知領域からフリックで削除可能にする true:削除可能 false:削除できない
}