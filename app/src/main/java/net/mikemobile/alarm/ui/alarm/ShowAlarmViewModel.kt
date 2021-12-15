package net.mikemobile.alarm.ui.alarm

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import io.reactivex.subjects.BehaviorSubject
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.repository.AlarmDataListener
import net.mikemobile.alarm.repository.AlarmRepository
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment
import java.util.*
import kotlin.collections.ArrayList


class ShowAlarmViewModel(
    private val dataRepository: DataRepository,
    private val databaseModel: DataBaseModel,
    private val alarmRepository: AlarmRepository): ViewModel() {

    var navigator : ShowAlarmFragmentNavigator? = null

    var titleTextView: MutableLiveData<String> = MutableLiveData<String>().apply{value = ""}
    var timeTextView: MutableLiveData<String> = MutableLiveData<String>().apply{value = ""}
    var dateTextView: MutableLiveData<String> = MutableLiveData<String>().apply{value = ""}

    var alarmItem:MutableLiveData<Alarm?> = MutableLiveData<Alarm?>().apply{value = null}

    var finishButtonVisible: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }
    var bitButtonTextSunuzu: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "スヌーズ"
    }
    var bitButtonText: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "停止"
    }


    fun setObserver(fragment: BaseFragment){
    }

    fun removeObserver(){
    }

    fun readAlarm() {
        android.util.Log.i("ShowAlarmViewModel","readAlarm()")
        dataRepository.readAlarm(object: AlarmDataListener {
            override fun onRead(alarm: Alarm?) {
                if (alarm != null) {
                    initialize(alarm)
                }
            }

            override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {

            }

        })
    }

    fun initialize(alarm: Alarm){
        android.util.Log.i("ShowAlarmViewModel","initialize() >> id:" + alarm.id)
        alarmItem.postValue(alarm)

        val year = CustomDateTime.getYearText(alarm.year)
        val month = CustomDateTime.getMonthText(alarm.month)
        val day = CustomDateTime.getDayText(alarm.day)
        val hour = CustomDateTime.getHourText(alarm.hour)
        val minute = CustomDateTime.getMinuteText(alarm.minute)

        android.util.Log.i("ShowAlarmViewModel","initialize() >> title:" + alarm.title)
        android.util.Log.i("ShowAlarmViewModel","initialize() >> hour:" + hour)
        android.util.Log.i("ShowAlarmViewModel","initialize() >> minute:" + minute)

        titleTextView.postValue(alarm.title)
        timeTextView.postValue(hour + ":" + minute)
        dateTextView.postValue(year + "/" + month + "/" + day)

        var finishButton = true
        var bigButtonSunuzu = "スヌーズ"
        var bigButton = "停止"
        if (!alarm.sunuzu) {
            finishButton = false
            bigButtonSunuzu = ""
            bigButton = "終了"
        } else if (alarm.stopMode) {
            finishButton = false
        }
        finishButtonVisible.postValue(finishButton)
        bitButtonText.postValue(bigButton)
        bitButtonTextSunuzu.postValue(bigButtonSunuzu)

    }

    fun clickStop(){
        navigator?.onStopAlarm()
    }

    fun clickSunuzu(){
        if (alarmItem.value == null) {
            navigator?.onStopSunuzu()
            return
        }

        var sunuzu = alarmItem.value!!.sunuzu
        if (!sunuzu){
            navigator?.onStopAlarm()
        } else {
            navigator?.onStopSunuzu()
        }
    }

    fun clickField() {}


}