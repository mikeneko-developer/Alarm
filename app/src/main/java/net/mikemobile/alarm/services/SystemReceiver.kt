package net.mikemobile.alarm.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.mikemobile.alarm.AlarmActivity
import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.notification.NotiUtilAlarm
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.alarm.util.DateTime
import net.mikemobile.alarm.util.sound.MyVolume
import java.text.SimpleDateFormat
import java.util.*
import android.media.AudioManager
import android.widget.Toast
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

                    saveNotificationTime(context, 0)

                } else if (it.action == "android.intent.action.PACKAGE_REPLACED") {
                    LogUtil.d(context, TAG, "アプリの再インストール・更新")

                } else if (it.action == Intent.ACTION_USER_PRESENT) {
                    LogUtil.d(context, TAG, "スクリーンロック解除検知")
                    //LogUtil.toast(context.applicationContext, "スクリーンロック解除検知")

                    screenLockTo(context, false)


                    return

                } else if (it.action == Intent.ACTION_SCREEN_ON) {
                    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


                    screenLockTo(context, keyguardManager.isDeviceLocked)
                    if(!keyguardManager.isDeviceLocked) {
                        LogUtil.d(context, TAG, "スクリーンロックされていないのでアラーム状態確認")

                        val localSave = LocalSave(context)
                        if (!localSave.getAlarmActive()) {
                            return
                        }

                        if (AlarmService.activeService(context)) {
                            LogUtil.d(context, TAG, "サービス実行中")
                            SystemReceiver.openActivity(context)
                        } else {
                            LogUtil.d(context, TAG, "サービスは動いてません")
                        }
                    } else {
                        LogUtil.d(context, TAG, "スクリーンロック中")
                    }

                    return
                } else if (it.action == Intent.ACTION_SCREEN_OFF) {
                    return
                } else {
                    LogUtil.i(context, TAG,"notification:" + it.extras?.getString("notification"))
                    // アラームチェックの処理を呼び出す

                    checkNextAlarmNotification(context)
                    checkAlarm(context)
                    return
                }

                if (!checkLoop(context)) {
                    LogUtil.i(context, TAG, "ループ処理は終了しています")
                } else {
                    TimeReceiver.setNextTime(context)
                }


            }

            // アラームチェックの処理を呼び出す
            checkNextAlarmNotification(context)
            checkAlarm(context)
        }
    }

    fun screenLockTo(context: Context, screenLock: Boolean) {
        if (screenLock) {
            LogUtil.d(context, TAG, "スクリーンロック状態")
            return
        }

        val localSave = LocalSave(context)
        if (!localSave.getAlarmActive()) {
            LogUtil.d(context, TAG, "アラームは実行されていない")
            return
        }
        val alarmId = localSave.getAlarmId()

        // アラームIDをチェック
        if (alarmId == -1) {
            LogUtil.d(context, TAG, "アラームIDが存在しません")
            return
        }

        if (localSave.getPhoneActive()) {
            LogUtil.d(context, TAG, "電話がかかっています")
            return
        }

        if (AlarmService.activeService(context)) {
            LogUtil.d(context, TAG, "サービス実行中")
            SystemReceiver.openActivity(context)
        } else {
            LogUtil.d(context, TAG, "サービスは動いてません")
        }

        // サービス実行
        val intent = Intent("net.mikemobile.myaction.alarm.start")
        intent.setPackage("net.mikemobile.alarm")
        intent.putExtra("alarmId", alarmId)
        context.applicationContext.sendBroadcast(intent)

        SystemReceiver.openActivity(context)
    }



    fun checkNextAlarmNotification(context: Context) {
        LogUtil.d(TAG, "checkNextAlarmNotification")
        val dbModel = DataBaseManager.getInstance(context)

        var list:List<Alarm>? = null
        Completable.fromCallable {
            dbModel.runInTransaction {
                //list = dbModel.onAlarmDataDao().findIsOnTimer(CustomDateTime.getJastTimeInMillis())
                list = dbModel.onAlarmDataDao().findAll()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                if(list == null){
                    list = ArrayList<Alarm>()
                }

                val notificationAlarm = NotiUtilAlarm()
                if (list!!.size > 0) {
                    val prevId = getAlarmId(context)
                    val id = list!![0].id

                    val prevDateTime = getNotificationTime(context)
                    val dateTime = CustomDateTime.getTimeInMillis()

                    val sabunDateTime = dateTime - prevDateTime

                    val prevCount = getAlarmCount(context)

                    if(prevId != -1 && id == prevId && sabunDateTime <= (1000 * 60 * 60) && prevCount == list!!.size) {

                        return@subscribeBy
                    }

                    saveAlarmId(context, id)
                    saveNotificationTime(context, dateTime)
                    saveAlarmCount(context, list!!.size)

                    LogUtil.d(TAG, "checkNextAlarmNotification >> 次のアラーム")
                    LogUtil.d(TAG, "checkNextAlarmNotification >> id:" + id)
                    LogUtil.d(TAG, "checkNextAlarmNotification >> owner_id:" + list!![0].owner_id)
                    LogUtil.d(TAG, "checkNextAlarmNotification >> title:" + list!![0].title)
                    LogUtil.d(TAG,"checkNextAlarmNotification >> date:" + CustomDateTime.getTimeText(list!![0].datetime))


                    val title = "次のアラーム"
                    val message = "" + CustomDateTime.getDateTimeText(list!![0].datetime) + " : " + list!![0].title

                    notificationAlarm.setChannel(context)
                    notificationAlarm.show(context, title, message)
                } else {
                    LogUtil.d(TAG, "checkNextAlarmNotification >> アラームを削除します")
                    notificationAlarm.delete(context)
                    notificationAlarm.clearChannel(context)
                }
            },
            onError = {
                LogUtil.e(TAG, "checkNextAlarmNotification onError : " + it.toString())
            }
        )
    }

    fun checkAlarm(context: Context) {
        LogUtil.d(TAG, "checkAlarm")
        val dbModel = DataBaseManager.getInstance(context)

        var list:List<Alarm>? = null
        Completable.fromCallable {
            dbModel.runInTransaction {
                list = dbModel.onAlarmDataDao().findIsOnTimer(CustomDateTime.getJastTimeInMillis())
                //list = dbModel.onAlarmDataDao().findAll()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                if(list == null){
                    list = ArrayList<Alarm>()
                }

                LogUtil.d(TAG, "checkAlarm >> list.size:" + list!!.size)
                if (list!!.size > 0) {
                    LogUtil.d(TAG, "checkAlarm >> アラームがあります")

                    var bool = false
                    val date = CustomDateTime.getJastTimeInMillis()
                    //LogUtil.d(TAG, "date:" + CustomDateTime.getDateTimeText(date))
                    for(item in list!!) {
                        //LogUtil.d(TAG, "date:" + CustomDateTime.getDateTimeText(item.datetime))
                        if (date >= item.datetime){
                            bool = true
                            break
                        }
                    }

                    if (bool) {
                        LogUtil.d(TAG, "checkAlarm >> サービス実行")
                        LogUtil.d(TAG, "checkAlarm >> id:" + list!![0].id)
                        LogUtil.d(TAG, "checkAlarm >> owner_id:" + list!![0].owner_id)
                        LogUtil.d(TAG, "checkAlarm >> title:" + list!![0].title)
                        LogUtil.d(TAG,"checkAlarm >> date:" + CustomDateTime.getTimeText(list!![0].datetime))


                        // サービス実行
                        val intent = Intent("net.mikemobile.myaction.alarm.start")
                        intent.setPackage("net.mikemobile.alarm")
                        intent.putExtra("alarmId", list!![0].id)
                        context.applicationContext.sendBroadcast(intent)

                        AlarmService.start(
                            context.applicationContext,
                            "alarm",
                            list!![0].id,
                            list!![0].title,
                            CustomDateTime.getTimeText(list!![0].datetime),
                            list!![0].sound_path
                        )

                        openActivity(context)
                    }
                } else {
                    LogUtil.e(TAG, "checkAlarm >> アラームはありません")

                    if (AlarmService.activeService(context.applicationContext)) {
                        AlarmService.stop(context.applicationContext)
                    }
                }
            },
            onError = {
                LogUtil.e(TAG, "checkAlarm onError : " + it.toString())
            }
        )
    }

    fun checkLoop(context: Context): Boolean {
        val sp = context.getSharedPreferences("timer",Context.MODE_PRIVATE)
        return sp.getBoolean("ScheduledTime", false)
    }

    fun getAlarmId(context: Context): Int {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        return sp.getInt("id", -1)
    }
    fun saveAlarmId(context: Context,id: Int) {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putInt("id", id)
        editor.commit()
    }

    fun getNotificationTime(context: Context): Long {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        return sp.getLong("time", 0)
    }
    fun saveNotificationTime(context: Context,datetime: Long) {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putLong("time", datetime)
        editor.commit()
    }

    fun getAlarmCount(context: Context): Int {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        return sp.getInt("count", -1)
    }
    fun saveAlarmCount(context: Context,id: Int) {
        val sp = context.getSharedPreferences("alarm",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putInt("count", id)
        editor.commit()
    }

    companion object {
        const val TAG = "SystemReceiver"

        fun openActivity(context: Context) {
            LogUtil.w(TAG, "openActivity")

            val app = context.applicationContext as DataBindingApplication
            if(!app.isAppActived()) {
                LogUtil.d(context, TAG, "MainActivityを新規に起動")
                val serviceIntent = Intent(context, MainActivity::class.java)
                serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.applicationContext.startActivity(serviceIntent)
            } else if(!app.isAppForeground()) {
                LogUtil.d(context, TAG, "MainActivityをforegroundに移動")
                moveTaskToFront(context)
            } else {
                LogUtil.d(context, TAG, "MainActivityはforegroundで動作中")
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