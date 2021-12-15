package net.mikemobile.alarm.repository

import android.content.Context
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.util.Constant
import net.mikemobile.android.music.OnMediaListener

class AlarmRepository(private val context: Context, private val mediaController: OnMediaListener) {

    var alarm = false


    var alarmEnable = MutableLiveData<Boolean>().apply {
        //value = false
        this.postValue(false)
    }



    val alarmData = MutableLiveData<Alarm>().apply {
        this.postValue(null)
    }
    var alarmStopType = MutableLiveData<Constant.Companion.AlarmStopType>().apply{
        //value = Constant.Companion.AlarmStopType.NONE
        this.postValue(Constant.Companion.AlarmStopType.NONE)
    }

    fun setAlarmData(alarm: Alarm?) {
        alarmData.postValue(alarm)
    }

    fun getAlarmData(): Alarm? {
        return alarmData.value
    }

    fun isAlarm(): Boolean {
        if(alarmData.value != null) {
            return true
        }
        return false
    }

    fun setAlarmEnable(boolean: Boolean){
        alarmEnable.postValue(boolean)
    }

}