package net.mikemobile.alarm.ui.debug

import android.annotation.SuppressLint
import android.content.Context.POWER_SERVICE
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_clock.view.*
import net.mikemobile.alarm.R
import net.mikemobile.alarm.databinding.FragmentClockBinding
import net.mikemobile.alarm.databinding.FragmentDebugBinding
import net.mikemobile.alarm.ui.util.MyClock
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


interface DebugFragmentNavigator: BaseNavigator {
}

class DebugFragment: BaseFragment(),DebugFragmentNavigator {

    private val viewModel: DebugViewModel by viewModel()

    var clockView: MyClock? = null

    companion object {
        const val TAG = "DebugFragment"
        fun newInstance() = DebugFragment()
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val binding = DataBindingUtil.inflate<FragmentDebugBinding>(inflater, R.layout.fragment_debug, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        /////////////////////////////////////

        viewModel.initialize()

        clockView = view.clock_view

        var tollbarTitle = binding.includeToolbar.findViewById<TextView>(R.id.toolbar_textview)
        tollbarTitle.setText("ログ画面")


        return view
    }

    //
    @SuppressLint("InvalidWakeLockTag")
    override fun onActivityCreate(savedInstanceState: Bundle?) {

        activity?.let{activity ->
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume(this)
        activity?.let {
            //it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
        }
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause(this)
        stopTimer()
    }

    @SuppressLint("InvalidWakeLockTag", "SourceLockedOrientationActivity")
    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
        activity?.let {
            //it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }


        activity?.let{activity ->
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    //
    override fun onBack() {

    }

    // ---------------------------------------------------------------------------------------------
    // BaseNavigatorのメソッド
    override fun onCloseFragment() {

    }

    // ---------------------------------------------------------------------------------------------

    //タイマー処理を行うオブジェクト
    private var mTimer: Timer? = null

    private var count = 0
    private var prevTime = 0L
    private val handler = Handler()

    private fun startTimer() {
        // タイマーの設定 1秒毎にループ
        var time = CustomDateTime.getTimeInMillis()
        var startTimer = true

        if(startTimer) {
            stopTimer()

            mTimer = Timer()

            val jastTime = CustomDateTime.getJastTimeInMillis()
            val firstDate = Date()
            firstDate.time = jastTime

            mTimer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    handler.post(object:Runnable{
                        override fun run() {
                            updateClock()
                        }
                    })
                }
            }, firstDate, 1000)
        }
    }

    private fun updateClock() {
        prevTime = CustomDateTime.getTimeInMillis()
        clockView?.UpdateDraw()
    }

    private fun stopTimer() {
        mTimer?.let{
            it.cancel()
        }
        mTimer = null
    }
}