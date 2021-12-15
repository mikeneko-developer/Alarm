package net.mikemobile.alarm.setup

import net.mikemobile.alarm.MainActivity
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.databindinglib.base.ActivityNavigator



fun ActivityNavigator.onUpdateAlarm(){
    activity?.let{
        (it as MainActivity).onUpdateTimeReceiver()
    }
}

fun ActivityNavigator.onStopAlarm() {
    activity?.let{
        (it as MainActivity).onStopAlarm()
    }
}

fun ActivityNavigator.onStopSunuzu() {
    activity?.let{
        (it as MainActivity).onStopSunuzu()
    }
}

fun ActivityNavigator.hideAlarmFragment() {
    activity?.let{
        (it as MainActivity).hideAlarmFragment()
    }
}


