package net.mikemobile.alarm.ui.alarm

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator

import androidx.databinding.DataBindingUtil
import net.mikemobile.alarm.R
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.databinding.FragmentShowAlarmBinding
import net.mikemobile.alarm.setup.hideAlarmFragment
import net.mikemobile.alarm.setup.onStopAlarm
import net.mikemobile.alarm.setup.onStopSunuzu
import org.koin.android.viewmodel.ext.android.viewModel

interface ShowAlarmFragmentNavigator: BaseNavigator {
    fun onStopAlarm()
    fun onStopSunuzu()
}

class ShowAlarmFragment: BaseFragment(), ShowAlarmFragmentNavigator {

    private val viewModel: ShowAlarmViewModel by viewModel()

    companion object {
        const val TAG = "ShowAlarmFragment"
        fun newInstance(bundle:Bundle?) = ShowAlarmFragment().apply{
            arguments = bundle
        }
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val binding = DataBindingUtil.inflate<FragmentShowAlarmBinding>(inflater, R.layout.fragment_show_alarm, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        /////////////////////////////////////
        viewModel.setObserver(this)
        viewModel.readAlarm()


        return view

        return null
    }

    //
    override fun onActivityCreate(savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
        }
    }

    override fun onPause() {
        super.onPause()

    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onDestroy() {
        super.onDestroy()
        viewModel.removeObserver()
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
    }

    //
    override fun onBack() {

    }

    // ---------------------------------------------------------------------------------------------
    // BaseNavigatorのメソッド
    override fun onCloseFragment() {
        activityNavigator.onBack()
    }

    override fun onStopAlarm() {
        activityNavigator.onStopAlarm()
        activityNavigator.hideAlarmFragment()
    }

    override fun onStopSunuzu() {
        activityNavigator.onStopSunuzu()
        activityNavigator.hideAlarmFragment()
    }
}