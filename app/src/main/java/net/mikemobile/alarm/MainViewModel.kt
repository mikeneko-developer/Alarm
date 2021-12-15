package net.mikemobile.alarm

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.repository.AlarmRepository
import net.mikemobile.alarm.repository.DataRepository
import org.koin.android.ext.android.inject

class MainViewModel(val dbModel: DataBaseModel,
                    private val dataRepository: DataRepository): ViewModel() {

    var navigator: MainActivityNavigator? = null
    val shoAlarmViewVisibility = MutableLiveData<Boolean>().apply{value = false}

    fun getShowAlarm(): Boolean {
        var bool = shoAlarmViewVisibility.value
        if (bool == null) return false
        return bool
    }

    fun setShowAlarmVisibility(bool: Boolean) {
        android.util.Log.i("!!!!!!","setShowAlarmVisibility() >> bool:" + bool)
        shoAlarmViewVisibility.postValue(bool)

    }

    fun onResume(activity: MainActivity) {
        unInit()
        init(activity)
    }

    fun init(activity: MainActivity) {

    }


    fun unInit() {
        dbModel.setOnDatabaseModelListener("DataRepository", null)
    }




}