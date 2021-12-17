package net.mikemobile.alarm.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.notification.NotiUtilAlarm
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.alarm.util.sound.MyVolume
import java.util.*
import android.media.AudioManager
import net.mikemobile.alarm.setup.DataBindingApplication
import net.mikemobile.alarm.tools.LocalSave


class SystemReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        context?.let {context ->
            LogUtil.i(TAG,"------------------------------------")
            LogUtil.i(context, TAG,"通知がきました")

            intent?.let {
                LogUtil.i(context, TAG,"action:" + it.action)

                // ループ処理をチェックする
                if(it.action.equals("android.media.RINGER_MODE_CHANGED")) {
                    //着信モードが変更された時の処理を記述

                    return
                } else if(it.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                    //音量が変更された時の処理を記述
                    val newVolume: Int =
                        it.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0)
                    val oldVolume: Int =
                        it.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0)
                    val streamType: Int =
                        it.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 0)

                    if (streamType == AudioManager.STREAM_MUSIC) {
                        // TODO: write your code
                        MyVolume.saveSystemVolume(context)
                    }
                    return
                } else if (it.action == "android.intent.action.BOOT_COMPLETED") {
                    LogUtil.d(context, TAG, "本体起動完了")

                    // TODO: write your code
                    MyVolume.saveSystemVolume(context)
                    TimeReceiver.saveNotificationTime(context, 0)

                } else if (it.action == "android.intent.action.PACKAGE_REPLACED") {
                    LogUtil.d(context, TAG, "アプリの再インストール・更新")

                } else if (it.action == Intent.ACTION_USER_PRESENT) {
                    LogUtil.d(context, TAG + " ALARM_CHECK", "スクリーンロック解除検知")
                    LogUtil.toast(context.applicationContext, "スクリーンロック解除検知")

                    screenLockTo(context, false)

                    return

                } else if (it.action == Intent.ACTION_SCREEN_ON) {
                    LogUtil.d(context, TAG + " ALARM_CHECK", "ACTION_SCREEN_ON")
                    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    LogUtil.toast(context.applicationContext, "ACTION_SCREEN_ON")

                    screenLockTo(context, keyguardManager.isDeviceLocked)

                    return
                } else if (it.action == Intent.ACTION_SCREEN_OFF) {
                    return
                } else {
                    LogUtil.i(context, TAG,"notification:" + it.extras?.getString("notification"))
                    // アラームチェックの処理を呼び出す
                    TimeReceiver.checkNextAlarmNotification(context)
                    TimeReceiver.checkAlarm(context)
                    return
                }

                if (!TimeReceiver.checkLoop(context)) {
                    LogUtil.i(context, TAG, "ループ処理は終了しています")
                } else {
                    TimeReceiver.setNextTime(context)
                }

            }

            // アラームチェックの処理を呼び出す
            TimeReceiver.checkNextAlarmNotification(context)
            TimeReceiver.checkAlarm(context)
        }
    }

    fun screenLockTo(context: Context, screenLock: Boolean) {
        if (screenLock) {
            LogUtil.d(context, TAG + " ALARM_CHECK", "スクリーンロック状態")
            return
        }

        val localSave = LocalSave(context)
        if (!localSave.getAlarmActive()) {
            LogUtil.d(context, TAG + " ALARM_CHECK", "アラームは実行されていない")
            return
        }
        val alarmId = localSave.getAlarmId()

        // アラームIDをチェック
        if (alarmId == -1) {
            LogUtil.d(context, TAG + " ALARM_CHECK", "アラームIDが存在しません")
            return
        }

        if (localSave.getPhoneActive()) {
            LogUtil.d(context, TAG + " ALARM_CHECK", "電話がかかっています")
            return
        }

        LogUtil.d(context, TAG + " ALARM_CHECK", "サービスを実行します")
        // サービス実行
        val intent = Intent("net.mikemobile.myaction.alarm.start")
        intent.setPackage("net.mikemobile.alarm")
        intent.putExtra("alarmId", alarmId)
        context.applicationContext.sendBroadcast(intent)

        //SystemReceiver.openActivity(context)
    }

    companion object {
        const val TAG = "SystemReceiver"

        fun openActivity(context: Context) {
            LogUtil.w(TAG + " ALARM_CHECK", "openActivity")

            val app = context.applicationContext as DataBindingApplication
            if(!app.isAppActived()) {
                LogUtil.d(context, TAG + " ALARM_CHECK", "MainActivityを新規に起動")
                val serviceIntent = Intent(context, MainActivity::class.java)
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.applicationContext.startActivity(serviceIntent)

            } else if(!app.isAppForeground()) {
                LogUtil.d(context, TAG + " ALARM_CHECK", "MainActivityをforegroundに移動")
                moveTaskToFront(context)
            } else {
                LogUtil.d(context, TAG + " ALARM_CHECK", "MainActivityはforegroundで動作中")
            }
        }

        private fun moveTaskToFront(context: Context) {
            val id: Int = getMyAppId(context)
            if (id > 0) {
                val activityManager = context.applicationContext.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
                activityManager.moveTaskToFront(id, ActivityManager.MOVE_TASK_WITH_HOME)
            }
        }

        /**
         * @return 自分のアプリのId
         *
         * マイナス値の場合は自分自身が起動していない
         */
        private fun getMyAppId(context: Context): Int {
            val id = -1
            val activityManager = context.applicationContext.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
            val recentTasks = activityManager.getRunningTasks(Int.MAX_VALUE)
            for (i in recentTasks.indices) {
                if (recentTasks[i].baseActivity!!.packageName == context.applicationContext.packageName) {
                    return recentTasks[i].id
                }
            }
            return id
        }

    }
}