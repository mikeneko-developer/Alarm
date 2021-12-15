package net.mikemobile.alarm.ui.debug

import android.content.Context
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment


class DebugViewModel(private val context: Context): ViewModel(){

    var navigator : DebugFragmentNavigator? = null
    val handler = Handler()

    var debugText: MutableLiveData<String> = MutableLiveData<String>()

    fun initialize(){
        val text = LogUtil.readFile(context)
        debugText.postValue(text)
    }

    fun resume(fragment: BaseFragment){
        // ガイド終了時の処理
    }
    fun pause(fragment: BaseFragment){
        // ガイド終了時の処理
    }

    fun destroy(){

    }

    fun clickClear() {
        LogUtil.clearFile(context)
        initialize()
    }

    fun clickReload() {
        initialize()
    }


}