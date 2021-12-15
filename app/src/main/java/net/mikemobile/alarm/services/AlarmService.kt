package net.mikemobile.alarm.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
import android.net.Uri
import android.os.*
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.notification.NotiUtilService
import net.mikemobile.alarm.repository.AlarmRepository
import net.mikemobile.alarm.setup.DataBindingApplication
import net.mikemobile.alarm.tools.LocalSave
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.alarm.util.sound.MyVolume
import net.mikemobile.android.music.MediaController
import net.mikemobile.sampletimer.music.MusicController
import org.koin.android.ext.android.inject
import java.util.*


interface OnAlarmListener{

}

class AlarmService : Service() {
    var TAG_TIME = ""
    companion object {
        const val TAG = "AlarmService"

        var ENABLE = false


        fun activeService(context: Context): Boolean{
            val manager: ActivityManager =
                context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (serviceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
                if (AlarmService::class.java.getName() == serviceInfo.service.getClassName()) {
                    // 実行中なら起動しない
                    return false
                }
            }
            return true
        }

        fun stop(context: Context) {
            val serviceIntent = Intent(context, AlarmService::class.java)
            context.applicationContext.stopService(serviceIntent)
        }

        fun start(@NonNull context: Context, event: String,
                  alarmId: Int,title: String, datetime: String, sound_path: String): Intent {
            val intent = Intent(context, AlarmService::class.java)
            intent.putExtra("event", event)
            intent.putExtra("alarmId", alarmId)
            intent.putExtra("title", title)
            intent.putExtra("datetime", datetime)
            intent.putExtra("sound_path", sound_path)

            Log.i(TAG, "start() ------------------------------")
            if(activeService(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("foregroundservice", true)
                    context.applicationContext.startForegroundService(intent)
                } else {
                    intent.putExtra("foregroundservice", false)
                    context.applicationContext.startService(intent)
                }
            } else {
                intent.putExtra("foregroundservice", false)
                context.applicationContext.startService(intent)
            }

            return intent
        }

    }


    private val dbModel: DataBaseModel by inject()
    private val alarmRepository: AlarmRepository by inject()

    // 通知を使いまわずためにフィールドに保持する
    private val notificationServer = NotiUtilService()


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        TAG_TIME = "-" + CustomDateTime.getDateTimeText(CustomDateTime.getJastTimeInMillis())
        android.util.Log.i(TAG + TAG_TIME, "onCreate() ------------------------------")
        ENABLE = true

        // 現在のアラームの数を取得する
        startPhoneCallback(this)
        TimeReceiver.setScreenUnLock(this)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        android.util.Log.i(TAG + TAG_TIME, "onStartCommand() ------------------------------")
        init()

        try {
            //dbModel.setOnDatabaseToServiceListener(this)
        }catch(e: Exception) {
            android.util.Log.e(TAG + TAG_TIME,"onStartCommand() >> " + e.toString())
        }
        var event = "none"
        var alarmId = -1
        var title = "none"
        var datetime = "none"
        var sound_path = "none"
        var foregroundservice = false

        intent?.let {intent ->
            foregroundservice = intent.getBooleanExtra("foregroundservice", false)

            event = if(intent.getStringExtra("event") == null){"none"}else ({
                intent.getStringExtra("event")
            }).toString()
            alarmId = intent.getIntExtra("alarmId", -1)
            title = if(intent.getStringExtra("title") == null){"none"}else ({intent.getStringExtra("title")})!!.toString()
            datetime = if(intent.getStringExtra("datetime") == null){"none"}else ({intent.getStringExtra("datetime")})!!.toString()
            sound_path = if(intent.getStringExtra("sound_path") == null){"none"}else ({intent.getStringExtra("sound_path")})!!.toString()
        }

        //android.util.Log.i(TAG, "event:" + event)
        //android.util.Log.i(TAG, "alarmId:" + alarmId)
        if (foregroundservice) {
            createNotification(title, datetime)
        }
        if (event == "alarm") {
            if (!foregroundservice) {
                createNotification(title, datetime)
            }
            readAlarm(alarmId)
        } else if (event == "sunuzu") {
            sunuzuAlarm()
        }else if (event == "stop") {
            stopAlarm()
        } else {
            checkAlarm()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG + TAG_TIME, "onDestroy()")

        stopPhoneCallback(this)
        unInit()

        dbModel.setOnDatabaseToServiceListener(null)

        TimeReceiver.clearScreenUnLock(this)
        ENABLE = false


        val localSave = LocalSave(this)
        localSave.savePhoneActive(false)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun init() {
        startReceiver(this)
    }


    fun unInit() {
        stopReceiver(this)

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun destoryService(){
        Log.i(TAG + TAG_TIME, "destoryService()")

        stopReceiver(this)
        //RepeatTimeReceiver.onRepeatStop(this)
        //notificationServer.clearChannel(this)

        val serviceIntent = Intent(this, AlarmService::class.java)
        this.stopService(serviceIntent)

        TimeReceiver.actionReciever(this, "AlarmService destroyService()")
    }


    fun checkAlarm(){
        try {
            dbModel.setOnDatabaseToServiceListener(AlarmListener())
        }catch(e: Exception) {
            android.util.Log.e(TAG,"onCreate() >> " + e.toString())
        }
        // 次のアラームがあるかチェック
        dbModel.readAlarm(CustomDateTime.getJastTimeInMillis())
    }


    inner class AlarmListener : OnDatabaseListener {
        override fun onReadItemList(list: List<ListItem>) {}
        override fun onReadResultFindOnTimer(list: List<Alarm>) {}
        override fun onSavedItem(item: Item) {}
        override fun onDeleted(item: Item) {}
        override fun onReadItem(item: Item?) {}
        override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
        override fun onReadAlarm(item: Alarm) {}
        override fun onDeleteAlarm() {}
        override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
        override fun onReadAlarmList(list: List<Alarm>) {
            android.util.Log.i(TAG,"onReadAlarmList() >> " + list.size)
            if (list.size > 0) {
                showAlarm(list[0])
            } else {
                destoryService()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun readAlarm(alarmId: Int) {
        //android.util.Log.i(TAG, "showAlarm() alarmId:" + alarmId)
        try {
            dbModel.setOnDatabaseToServiceListener(StartAlarmListener())
        }catch(e: Exception) {
            android.util.Log.e(TAG,"onCreate() >> " + e.toString())
        }

        dbModel.readAlarm(alarmId)
    }


    inner class StartAlarmListener : OnDatabaseListener {
        override fun onReadItemList(list: List<ListItem>) {}
        override fun onReadResultFindOnTimer(list: List<Alarm>) {}
        override fun onSavedItem(item: Item) {}
        override fun onDeleted(item: Item) {}
        override fun onReadItem(item: Item?) {}
        override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
        override fun onReadAlarmList(list: List<Alarm>) {}
        override fun onDeleteAlarm() {}
        override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
        override fun onReadAlarm(item: Alarm) {
            showAlarm(item)
        }
    }

    fun showActivity(context: Context) {

        SystemReceiver.openActivity(context)
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    var alarmItem:Alarm? = null

    fun showAlarm(alarm: Alarm) {
        if(alarmItem != null) {
            Log.i(TAG, "既にアラームが有効状態 呼ばれたアラーム:" + alarm.title)

            // バイブ実行だけ、スリープ中は止まっちゃうので再実行
            startVib(alarm.vib)
            return
        }

        checkMusic(this)

        alarmItem = alarm

        // Notification　表示
        showNowAlarmNotification(alarm)

        // バイブ実行
        startVib(alarm.vib)

        // 音楽再生
        playMusic(alarm.sound_path, alarm.sound_volume)

        alarmRepository.setAlarmEnable(true)

        // アラームRepositoryに通知
        //alarmRepository.showAlarm(alarm)

        // Activityの起動
        showActivity(this)

        val intent = Intent("net.mikemobile.myaction.alarm.start")
        intent.setPackage("net.mikemobile.alarm")
        sendBroadcast(intent)

    }

    fun stopAlarm() {
        try {
            dbModel.setOnDatabaseToServiceListener(StopAlarmListener())
        }catch(e: Exception) {
            android.util.Log.e(TAG,"onCreate() >> " + e.toString())
        }

        // アラームデータ削除
        alarmItem?.let {
            dbModel.deleteAlarm(it)
        }

        // アラームの停止
        alarmItem = null

        // アラーム通知削除
        stopNowAlarmNotification()

        // バイブ削除
        stopVib()

        // 音楽停止
        stopMusic()

        // 音量をシステムボリュームに戻す
        MyVolume.resetSystemVolume(this)


    }

    inner class StopAlarmListener : OnDatabaseListener {
        override fun onReadItemList(list: List<ListItem>) {}
        override fun onReadResultFindOnTimer(list: List<Alarm>) {}
        override fun onSavedItem(item: Item) {}
        override fun onDeleted(item: Item) {}
        override fun onReadItem(item: Item?) {}
        override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
        override fun onReadAlarm(item: Alarm) {}
        override fun onDeleteAlarm() {
            // 次のアラームがあるかチェック
            dbModel.readAlarm(CustomDateTime.getJastTimeInMillis())
        }
        override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
        override fun onReadAlarmList(list: List<Alarm>) {
            //android.util.Log.i(TAG,"onReadAlarmList() >> " + list.size)
            if (list.size > 0) {
                showAlarm(list[0])
            } else {
                destoryService()
            }
        }
    }

    fun sunuzuAlarm() {
        try {
            dbModel.setOnDatabaseToServiceListener(SunuzuAlarmListener())
        }catch(e: Exception) {
            android.util.Log.e(TAG,"onCreate() >> " + e.toString())
        }

        // アラームデータ削除
        alarmItem?.let {
            dbModel.deleteAlarmToSunuzu(it)
        }

        // アラームの停止
        alarmItem = null

        // アラーム通知削除
        stopNowAlarmNotification()

        // バイブ削除
        stopVib()

        // 音楽停止
        stopMusic()

        // 音量をシステムボリュームに戻す
        //MyVolume.resetSystemVolume(this)


    }

    inner class SunuzuAlarmListener : OnDatabaseListener {
        override fun onReadItemList(list: List<ListItem>) {}
        override fun onReadResultFindOnTimer(list: List<Alarm>) {}
        override fun onSavedItem(item: Item) {}
        override fun onDeleted(item: Item) {}
        override fun onReadItem(item: Item?) {}
        override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
        override fun onReadAlarm(item: Alarm) {}
        override fun onDeleteAlarm() {
            // 次のアラームがあるかチェック
            dbModel.readAlarm(CustomDateTime.getJastTimeInMillis())
        }
        override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
        override fun onReadAlarmList(list: List<Alarm>) {
            android.util.Log.i(TAG,"onReadAlarmList() >> " + list.size)

            if (list.size > 0) {
                showAlarm(list[0])
            } else {
                destoryService()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createNotification(title: String, datetime: String) {
        notificationServer.setChannel(this)
        val buttons = ArrayList<ArrayList<String>>()
        buttons.add(arrayListOf("スヌーズ","net.mikemobile.myaction.alarm.sunuzu"))
        buttons.add(arrayListOf("停止","net.mikemobile.myaction.alarm.stop"))

        val notification = notificationServer.getNotification(this, title, datetime, buttons)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(notificationServer.NOTIFICATION_ID, notification)
        } else {
            startForeground(notificationServer.NOTIFICATION_ID, notification)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun showNowAlarmNotification(alarm: Alarm?) {

        var text = ""

        var title = "目覚まし！！！！！"
        val button = ""

        alarm?.let{
            title = "" + it.title

            if(title.equals("")){
                title = "目覚まし！！！！！"
            }

            text = "" + CustomDateTime.getDateTimeText(it.datetime)
        }

        val buttons = ArrayList<ArrayList<String>>()
        buttons.add(arrayListOf("スヌーズ","net.mikemobile.myaction.alarm.sunuzu"))
        buttons.add(arrayListOf("停止","net.mikemobile.myaction.alarm.stop"))

        notificationServer.show(this, title, text, buttons)
    }

    fun stopNowAlarmNotification() {
        notificationServer.delete(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    var vibrator: Vibrator? = null
    var vib = 0
    fun startVib(vib: Int) {
        if(vib == 0)return
        this.vib = vib

        if (phoneToMusicStop) {
            return
        }
        val vibType = Constant.VIB_LIST[vib]

        val power = ArrayList<Int>()
        for(i in 0 until vibType.size) {
            if(i % 2 == 0) {
                power.add(VibrationEffect.DEFAULT_AMPLITUDE)
            }else {
                power.add(0)
            }
        }

        val powerList = power.toIntArray()

        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            val vibratorEffect = VibrationEffect.createWaveform(vibType, powerList, 0)
            vibrator?.vibrate(vibratorEffect)
        }else {
            vibrator?.vibrate(vibType, -1)
        }
    }

    fun stopVib(){
        if(vibrator != null){
            vibrator?.cancel()
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    var musicPlayer: MediaController? = null
    var path = ""
    var volume = -1
    fun playMusic(path: String,volume: Int){
        stopMusic()

        if(path == "") {
            return
        }

        this.path = path
        this.volume = volume

        if (phoneToMusicStop) {
            return
        }

        if(volume == -1){
            MyVolume.resetSystemVolume(this)
        }else {
            MyVolume.setVolume(this, volume)
        }

        Log.i(TAG + TAG_TIME, "playMusic()")

        musicPlayer = MediaController(this)
        musicPlayer?.setLoop(true)
        musicPlayer?.onPlay(Uri.parse(path))
    }

    fun stopMusic(){
        Log.i(TAG + TAG_TIME, "stopMusic()")
        if(musicPlayer != null){
            musicPlayer!!.onStop()

            if(musicPlayer!!.isPlay()){
                Log.i(AlarmService.TAG, "音楽が止まりませんでした")
            }
        }
        MyVolume.resetSystemVolume(this)
        musicPlayer = null
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 次に予定しているアラーム情報をOffに変更する
    private fun deleteAlarm(){
        //Log.i(TAG, "deleteAlarm() >> ")
    }


    val reciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            android.util.Log.i(TAG + TAG_TIME,"通知がきました")

            context?.let {context ->
                intent?.let {
                    android.util.Log.i(TAG + TAG_TIME,"action:" + it.action)

                    // ループ処理をチェックする
                    if (it.action.equals("net.mikemobile.myaction.alarm.stop")) {
                        stopAlarm()
                    }

                    if (it.action.equals("net.mikemobile.myaction.alarm.sunuzu")) {
                        sunuzuAlarm()
                    }
                }
            }
        }
    }

    fun startReceiver(context: Context) {
        stopReceiver(context)

        val filter = IntentFilter()
        filter.addAction("net.mikemobile.myaction.alarm.start")
        filter.addAction("net.mikemobile.myaction.alarm.sunuzu")
        filter.addAction("net.mikemobile.myaction.alarm.stop")
        context.registerReceiver(reciever, filter)
    }

    fun stopReceiver(context: Context) {
        try {
            context.unregisterReceiver(reciever)
        }catch(e: Exception) {
            LogUtil.e(TAG, e.toString())
        }
    }


    var phoneToMusicStop = false
    val phoneListener = object : PhoneStateListener() {
        // PhoneStateListenerの`onCallStateChanged`をOverride
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            // パーミッションがない場合、incomingNumberは常に空
            val localSave = LocalSave(this@AlarmService)
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    /* 着信 */Log.i(TAG, "Call State Changed: CALL_STATE_RINGING")
                    //Toast.makeText(this@MainActivity, "呼び出し中", Toast.LENGTH_SHORT).show()
                    phoneToMusicStop = true
                    localSave.savePhoneActive(true)
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    /* 通話 */Log.i(TAG, "Call State Changed: CALL_STATE_OFFHOOK")
                    //Toast.makeText(this@MainActivity, "通話中", Toast.LENGTH_SHORT).show()
                    phoneToMusicStop = true
                    localSave.savePhoneActive(true)
                }

                TelephonyManager.CALL_STATE_IDLE -> {
                    /* 待受 */Log.i(TAG, "Call State Changed: CALL_STATE_IDLE")
                    //Toast.makeText(this@MainActivity, "待受（切断）", Toast.LENGTH_SHORT).show()
                    if (phoneToMusicStop) {
                        phoneToMusicStop = false
                        playMusic(path, volume)
                        startVib(vib)
                        localSave.savePhoneActive(false)
                    }
                }
            }
        }
    }

    fun startPhoneCallback(context: Context) {
        // TelephonyManagerを取得
        // listenerを登録
        val service = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        service.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun stopPhoneCallback(context: Context) {
        val service = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        service.listen(phoneListener, PhoneStateListener.LISTEN_NONE)

    }

    fun checkMusic(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.isMusicActive) {
            // 再生中!!

            val intent = Intent("com.android.music.musicservicecommand")
            // play: 再生, pause: 停止, next: 次へ, preview: 前へ
            intent.putExtra("command", "pause")
            context.sendBroadcast(intent)
        } else if(audioManager.isMicrophoneMute) {

        }

        //audioManager.adjustStreamVolume(AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE, AudioManager.ADJUST_MUTE, 0)
    }

    fun clearCheckMusic(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}