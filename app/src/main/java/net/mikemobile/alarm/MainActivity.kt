package net.mikemobile.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.provider.AlarmClock
import android.util.Log
import androidx.databinding.DataBindingUtil
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.databinding.ActivityBaseMainBinding
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.services.AlarmService
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.setup.DataBindingApplication
import net.mikemobile.alarm.setup.FragmentFactory
import net.mikemobile.alarm.tools.LocalSave
import net.mikemobile.alarm.ui.alarm.ShowAlarmFragment
import net.mikemobile.alarm.ui.list.ListFragment
import net.mikemobile.databindinglib.BaseActivityApplication
import net.mikemobile.databindinglib.base.BaseActivity
import net.mikemobile.databindinglib.base.BaseFragmentFactory
import net.mikemobile.media.MediaUtilityManager
import net.mikemobile.media.MediaUtilityManager.Companion.getMediaManager
import net.mikemobile.media.ThumbnailManager
import net.mikemobile.sampletimer.music.MusicController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

interface MainActivityNavigator {
    fun showAlarmFragment()
    fun hideAlarmFragment()
}

class MainActivity : BaseActivity(), MainActivityNavigator {
    var onIntentFlg = false
    val handler = Handler()
    private val viewModel: MainViewModel by viewModel()

    private val dbModel: DataBaseModel by inject()

    companion object {
        const val TAG: String = "MainActivity"
    }

    override fun setActivityApplication(): BaseActivityApplication {
        return application as DataBindingApplication
    }

    override fun onFragmentFactory(): BaseFragmentFactory {
        return FragmentFactory().getInstance()
    }

    override fun onCreateView(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Alarm)

        onIntentFlg = false
        val binding = DataBindingUtil.setContentView<ActivityBaseMainBinding>(this, R.layout.activity_base_main)
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        viewModel.init(this)

        setToolbarEnabled(false)

        //
        replaceFragmentInContentFrame(ListFragment.TAG, DEFAULT_CONTENT_VIEW_MAIN, null)

        if (intent != null && intent.getIntExtra("alarm", 0) == 1) {
            showAlarmFragment()
        } else if(!AlarmService.activeService(this)){
            showAlarmFragment()
        } else {
            onUpdateTimeReceiver()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        android.util.Log.i(TAG,"onNewIntent()")

        val localSave = LocalSave(this)
        if (localSave.getAlarmActive()) {
            android.util.Log.i(TAG,"onNewIntent()")
            showAlarmFragment()
        } else {
            onUpdateTimeReceiver()
        }
    }

    override fun onResume(){
        super.onResume()
        android.util.Log.i(TAG,"onResume()")
        if (!onIntentFlg) {
        }
        startReceiver(this)
        onIntentFlg = false
    }

    override fun onPause(){
        super.onPause()
        android.util.Log.i(TAG,"onPause()")

        stopReceiver(this)
    }

    override fun onDestroy(){
        super.onDestroy()
        viewModel.unInit()
        val mediaManager = getMediaManager(this)
        mediaManager.onClearData()
        ThumbnailManager.reset()
    }

    override fun onBack() {
        super.onBack()
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    override fun showAlarmFragment() {
        if (viewModel.getShowAlarm()) {
            android.util.Log.i(TAG,"showAlarmFragment() 表示済み")
            return
        }
        android.util.Log.i(TAG,"showAlarmFragment()")
        viewModel.setShowAlarmVisibility(true)

        replaceFragmentInContentFrame(ShowAlarmFragment.TAG, DEFAULT_CONTENT_VIEW_ALART, null)
    }

    override fun hideAlarmFragment() {
        android.util.Log.i(TAG,"hideAlarmFragment()")

        removeFragment(ShowAlarmFragment.TAG)
        viewModel.setShowAlarmVisibility(false)

    }


    /////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////



    fun onUpdateTimeReceiver() {
        android.util.Log.i(TAG,"onUpdateTimeReceiver()")
        TimeReceiver.actionReciever(this, "MainActivity onUpdateTimeReceiver()")
    }

    fun onStopAlarm() {
        android.util.Log.i(TAG,"onStopAlarm()")

        val intent = Intent("net.mikemobile.myaction.alarm.stop")
        intent.setPackage("net.mikemobile.alarm")
        sendBroadcast(intent)

        //val intent1 = Intent(this, AlarmStopReceiver::class.java)
        //intent1.action = Constant.INTENT_STOP_ALARM
        //sendBroadcast(intent1)
    }

    fun onStopSunuzu() {
        android.util.Log.i(TAG,"onStopSunuzu()")

        val intent = Intent("net.mikemobile.myaction.alarm.sunuzu")
        intent.setPackage("net.mikemobile.alarm")
        sendBroadcast(intent)

        //val intent1 = Intent(this, AlarmStopReceiver::class.java)
        //intent1.action = Constant.INTENT_SUNUZU_ALARM
        //sendBroadcast(intent1)
    }

    //////////////////////////////////////////////////////////////////////////////////


    val reciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            android.util.Log.i(TAG,"通知がきました")
            context?.let {context ->
                intent?.let {
                    android.util.Log.i(TAG,"action:" + it.action)

                    if (it.action.equals("net.mikemobile.myaction.alarm.start")) {
                        showAlarmFragment()
                    }else if (it.action.equals("net.mikemobile.myaction.alarm.stop")) {
                        hideAlarmFragment()
                    }else if (it.action.equals("net.mikemobile.myaction.alarm.sunuzu")) {
                        hideAlarmFragment()
                    }
                }
            }
        }
    }

    fun startReceiver(context: Context) {
        if (reciever.isOrderedBroadcast) {
            return
        }

        val filter = IntentFilter()
        filter.addAction("net.mikemobile.myaction.alarm.start")
        filter.addAction("net.mikemobile.myaction.alarm.sunuzu")
        filter.addAction("net.mikemobile.myaction.alarm.stop")
        applicationContext.registerReceiver(reciever, filter)
    }

    fun stopReceiver(context: Context) {
        if (!reciever.isOrderedBroadcast) {
            return
        }
        try {
            context.unregisterReceiver(reciever)
        }catch(e: Exception) {
            LogUtil.e(TAG, e.toString())
        }
    }
}
