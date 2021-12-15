package net.mikemobile.alarm.ui.clock

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.notification.NotiUtilAlarm
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment
import java.util.*


class ClockViewModel(private val dataBaseModel: DataBaseModel, private val dataRepository: DataRepository): ViewModel(),
    OnDatabaseListener{

    var navigator : ClockFragmentNavigator? = null
    val handler = Handler()

    var alarm: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}
    var datetime: MutableLiveData<String> = MutableLiveData<String>().apply{value = "----年--月--日(ー) --:--"}
    fun initialize(){
    }

    fun resume(fragment: BaseFragment){
        // ガイド終了時の処理
        dataBaseModel.setOnDatabaseClockListener(this)
        dataBaseModel.readAlarm()
    }
    fun pause(fragment: BaseFragment){
        // ガイド終了時の処理
        dataBaseModel.setOnDatabaseClockListener(null)
    }

    fun destroy(){

    }
    override fun onReadAlarmList(list: List<Alarm>) {
        if(list.size == 0) {
            alarm.postValue(false)
            datetime.postValue("----年--月--日(ー) --:--")
        }else {
            alarm.postValue(true)

            var alarm = list[0].datetime
            datetime.postValue(CustomDateTime.getDateTimeText(alarm))
        }
    }

    override fun onReadItem(item: Item?){}
    override fun onReadSunuzuList(list: ArrayList<SunuzuItem>){}
    override fun onReadItemList(list: List<ListItem>){}
    override fun onReadResultFindOnTimer(list: List<Alarm>){}
    override fun onSavedItem(item: Item){}
    override fun onDeleted(item: Item){}
    override fun onReadAlarm(item: Alarm){}
    override fun onDeleteAlarm(){}
    override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String){}
}