package net.mikemobile.alarm.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.notification.NotiUtilAlarm
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.alarm.util.DateTime
import java.text.SimpleDateFormat
import java.util.*
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.util.sound.MyVolume
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.getSystemService

import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardLock
import android.content.*

import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.core.content.ContextCompat
import android.content.IntentFilter
import net.mikemobile.alarm.tools.LocalSave


class TimeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {context ->
            LogUtil.i(TAG,"------------------------------------")
            LogUtil.i(context, TAG,"通知がきました")

            intent?.let {
                LogUtil.i(context, TAG,"action:" + it.action)

                // ループ処理をチェックする
                if (it.action.equals("net.mikemobile.alarm.myaction.loop")) {
                    // TODO: write your code
                    MyVolume.saveSystemVolume(context)

                    if (!checkLoop(context)) {
                        LogUtil.i(context, TAG,"ループ処理は終了しています")
                    } else {
                        setNextTime(context)
                    }
                } else if (it.action == Intent.ACTION_TIME_CHANGED
                    || it.action == Intent.ACTION_TIMEZONE_CHANGED) run {
                    LogUtil.d(context, TAG, "時間変更")

                    if (!checkLoop(context)) {
                        LogUtil.i(context, TAG, "ループ処理は終了しています")
                    } else {
                        setNextTime(context)
                    }

                } else if (it.action == "android.intent.action.BOOT_COMPLETED") {
                    LogUtil.d(context, TAG, "本体起動完了")

                    saveNotificationTime(context, 0)
                    if (!checkLoop(context)) {
                        LogUtil.i(context, TAG, "ループ処理は終了しています")
                    } else {
                        setNextTime(context)
                    }

                } else if (it.action == "android.intent.action.PACKAGE_REPLACED") {
                    LogUtil.d(context, TAG, "アプリの再インストール・更新")

                    if (!checkLoop(context)) {
                        LogUtil.i(context, TAG, "ループ処理は終了しています")
                    } else {
                        setNextTime(context)
                    }

                } else {
                    LogUtil.i(context, TAG,"notification:" + it.extras?.getString("notification"))
                }
            }

            // アラームチェックの処理を呼び出す
            checkNextAlarmNotification(context)
            checkAlarm(context)
        }
    }


    fun checkNextAlarmNotification(context: Context) {
        LogUtil.d(TAG, "checkNextAlarmNotification")

        val dbModels = DataBaseModel(context)
        dbModels.setOnDatabaseModelListener("TimeReceiver-checkNextAlarmNotification", object: OnDatabaseListener {
            override fun onReadItem(item: Item?) {}
            override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
            override fun onReadItemList(list: List<ListItem>) {}
            override fun onReadResultFindOnTimer(list: List<Alarm>) {}
            override fun onSavedItem(item: Item) {}
            override fun onDeleted(item: Item) {}
            override fun onReadAlarm(item: Alarm) {}
            override fun onDeleteAlarm() {}
            override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
            override fun onReadAlarmList(list: List<Alarm>) {
                LogUtil.d(TAG, "checkNextAlarmNotification >> onReadAlarmList()")
                dbModels.setOnDatabaseModelListener("TimeReceiver-checkNextAlarmNotification",null)

                val notificationAlarm = NotiUtilAlarm()
                if (list.size > 0) {
                    LogUtil.d(TAG, "checkNextAlarmNotification >> list.size:" + list.size)
                    val prevId = getAlarmId(context)
                    val id = list[0].id

                    val prevDateTime = getNotificationTime(context)
                    val dateTime = CustomDateTime.getTimeInMillis()

                    val sabunDateTime = dateTime - prevDateTime

                    val prevCount = getAlarmCount(context)

                    if (!notificationAlarm.enableNotification(context)) {
                        // 通知がない
                        LogUtil.d(TAG, "checkNextAlarmNotification >> Notificationがない")
                    } else if(prevId != -1 && id == prevId && sabunDateTime <= (1000 * 60 * 60) && prevCount == list!!.size) {

                        LogUtil.d(TAG, "checkNextAlarmNotification >> 同じ情報のため、通知なし")
                        LogUtil.d(TAG, "checkNextAlarmNotification >> prevId:" + prevId)
                        LogUtil.d(TAG, "checkNextAlarmNotification >> id:" + id)
                        LogUtil.d(TAG, "checkNextAlarmNotification >> sabunDateTime:" + sabunDateTime)
                        LogUtil.d(TAG, "checkNextAlarmNotification >> limit_time:" + (1000 * 60 * 60))
                        LogUtil.d(TAG, "checkNextAlarmNotification >> listCount:" + list!!.size)
                        LogUtil.d(TAG, "checkNextAlarmNotification >> prevCount:" + prevCount)

                        return
                    }

                    saveAlarmId(context, id)
                    saveNotificationTime(context, dateTime)
                    saveAlarmCount(context, list.size)

                    LogUtil.d(TAG, "checkNextAlarmNotification >> 次のアラーム")
                    LogUtil.d(TAG, "checkNextAlarmNotification >> id:" + id)
                    LogUtil.d(TAG, "checkNextAlarmNotification >> owner_id:" + list[0].owner_id)
                    LogUtil.d(TAG, "checkNextAlarmNotification >> title:" + list[0].title)
                    LogUtil.d(TAG,"checkNextAlarmNotification >> date:" + CustomDateTime.getTimeText(list[0].datetime))


                    val title = "次のアラーム"
                    val message = "" + CustomDateTime.getDateTimeText(list[0].datetime) + " : " + list[0].title

                    notificationAlarm.setChannel(context)
                    notificationAlarm.show(context, title, message)
                } else {
                    LogUtil.d(TAG, "checkNextAlarmNotification >> アラームを削除します")
                    notificationAlarm.delete(context)
                    notificationAlarm.clearChannel(context)
                }
            }
        })

        dbModels.readAlarm()
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
                val localSave = LocalSave(context)

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

                        localSave.saveAlarmActive(true)
                        localSave.saveAlarmId(list!![0].id)
                    }
                } else {
                    LogUtil.e(TAG, "checkAlarm >> アラームはありません")

                    if (AlarmService.activeService(context.applicationContext)) {
                        AlarmService.stop(context.applicationContext)
                    }
                    localSave.saveAlarmActive(false)
                    localSave.saveAlarmId(-1)

                    TimeReceiver.clearScreenUnLock(context)
                }
            },
            onError = {
                LogUtil.e(TAG, "checkAlarm onError : " + it.toString())
            }
        )
    }

    fun openActivity(context: Context) {
        LogUtil.w(TAG, "openActivity")


        //以下に処理を書く
        //キーガード解除
        LockRelease(context)

        val i = Intent(context, MainActivity::class.java)
        i.putExtra("alarm", 1)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    @SuppressLint("WakelockTimeout", "MissingPermission")
    private fun LockRelease(context: Context) {
        val keylock: KeyguardLock?
        val wakelock: WakeLock
        //キーガード解除
        var powerManager = (context.getSystemService(Context.POWER_SERVICE) as PowerManager?)
        powerManager?.let {
            wakelock = it
                .newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            or PowerManager.ACQUIRE_CAUSES_WAKEUP
                            or PowerManager.ON_AFTER_RELEASE, "vib:disableLock"
                )
            wakelock.acquire()
            wakelock.release()
        }


        //ロック解除
        val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        keylock = keyguard!!.newKeyguardLock("disableLock")
        keylock?.disableKeyguard()
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
        const val TAG = "TimeReceiver"

        fun actionReciever(context: Context, text: String) {
            LogUtil.i(TAG, "actionReciever >> " + text)

            setLoopEnable(context, true)
            val intent = Intent("net.mikemobile.alarm.myaction.loop")
            intent.setPackage("net.mikemobile.alarm")
            context.applicationContext.sendBroadcast(intent)
        }

        fun checkLoopEnabled(context: Context): Boolean {
            val sp = context.getSharedPreferences("timer",Context.MODE_PRIVATE)
            return sp.getBoolean("ScheduledTime", false)
        }

        fun setLoopEnable(context: Context, bool: Boolean) {
            val sp = context.getSharedPreferences("timer",Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.putBoolean("ScheduledTime", bool)
            editor.commit()

        }

        fun setReceiver(context: Context, bool: Boolean) {
            setLoopEnable(context, bool)
            if (bool) {

                setNextTime(context)
            } else {
                clearNextTime(context)
            }
        }


        @RequiresApi(Build.VERSION_CODES.O)
        private fun getPaddingIntent(context: Context): PendingIntent {
            val intent = Intent(context, TimeReceiver::class.java)
            intent.action = "net.mikemobile.alarm.myaction.loop"
            intent.putExtra("notification","AlarmManager")
            intent.setPackage("net.mikemobile.alarm")

            // PendingIntent.FLAG_CANCEL_CURRENT    // 現在設定されているものがあるなら、それをキャンセルし新しく設定する
            // PendingIntent.FLAG_UPDATE_CURRENT    // 存在していればそれを使う。新しい設定で置き換えない
            // PendingIntent.FLAG_NO_CREATE         // 存在していないなら、戻り値にnullを返す（新規に作成しない）
            // PendingIntent.FLAG_ONE_SHOT          // 一度だけ利用する
            return PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun checkLoop(context: Context): Boolean {
            val intent = Intent(context, TimeReceiver::class.java)
            intent.action = "net.mikemobile.alarm.myaction.loop"
            intent.putExtra("notification","AlarmManager")
            intent.setPackage("net.mikemobile.alarm")

            var pi = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_NO_CREATE
            )

            if (pi == null) {
                return false
            } else {
                return true
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setNextTime(context: Context, nextAlarmTime: Long = -1) {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val minute = DateTime.get(Calendar.MINUTE, calendar)
            val plus = DateTime.calcSecond(minute, 1)

            //calendar.add(Calendar.HOUR_OF_DAY, plus)
            calendar.add(Calendar.MINUTE, plus)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val nextSecond = DateTime.get(Calendar.MINUTE, calendar)
            val nextTime = calendar.timeInMillis

            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val result = sdf.format(nextTime)

            LogUtil.i(TAG,"MINUTE : " + minute + " / Next MINUTE : " + nextSecond + " / Next TIME : " + result)

            //val notification = NotiUtilTest()
            //notification.setChannel(context)
            //notification.show(context, "次の時間", "" + result)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent =
                getPaddingIntent(context)

            /**
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                nextTime,
                pendingIntent
            )*/

            var info = AlarmManager.AlarmClockInfo(
                nextTime, null
            )
            alarmManager.setAlarmClock(info, pendingIntent)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun clearNextTime(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(getPaddingIntent(context))

            //val notification = NotiUtilTest()
            //notification.delete(context)
            //notification.clearChannel(context)
        }



        var bbr: SystemReceiver = SystemReceiver()
        fun setScreenUnLock(context: Context) {
            if (bbr.isOrderedBroadcast) {
                LogUtil.i(TAG,"SystemReceiverは登録済み")
                clearScreenUnLock(context)
            }
            LogUtil.i(TAG,"SystemReceiver登録")
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            context.applicationContext.registerReceiver(bbr, filter)
        }
        fun clearScreenUnLock(context: Context) {
            if (!bbr.isOrderedBroadcast) {
                return
            }
            LogUtil.i(TAG,"SystemReceiver解除")
            try {
                context.applicationContext.unregisterReceiver(bbr)
            }catch(e: Exception) {
                LogUtil.e(TAG,"SystemReceiver解除 " + e.toString())
            }
        }
    }
}