package net.mikemobile.alarm.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil


interface DataSaveListener {
    fun onComplete(item: Item)
    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}

interface AlarmDataListener {
    fun onRead(alarm: Alarm?)
    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}

interface AlarmDeleteListener {
    fun onComplete()
    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}

interface ItemListListener {
    fun onRead(list: MutableList<ListItem>)
    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}

interface NextAlarmListener {
    fun onComplete()
    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}

abstract class DataRepositoryListener: OnDatabaseListener {
    override fun onReadItem(item: Item?) {}
    override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {}
    override fun onReadItemList(list: List<ListItem>) {}
    override fun onReadResultFindOnTimer(list: List<Alarm>) {}
    override fun onSavedItem(item: Item) {}
    override fun onDeleted(item: Item) {}
    override fun onReadAlarm(item: Alarm) {}
    override fun onReadAlarmList(list: List<Alarm>) {}
    override fun onDeleteAlarm() {}
    override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {}
}



class DataRepository(var databaseModel: DataBaseModel)  {
    companion object {
        const val TAG = "DataRepository"
    }

    init {
        LogUtil.i(TAG,"init")
        databaseModel.setOnDatabaseModelListener(TAG, object: OnDatabaseListener {
            override fun onReadItem(item: Item?) {
                LogUtil.i(TAG,"onReadItem()")
                editItem.postValue(item)

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadItem(item)
                }
            }

            override fun onReadItemList(list: List<ListItem>) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onReadItemList()")

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadItemList(list)
                }

                readItemList.postValue(list as MutableList<ListItem>)
            }

            override fun onReadResultFindOnTimer(list: List<Alarm>) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onReadResultFindOnTimer()")

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadResultFindOnTimer(list)
                }
            }

            override fun onSavedItem(item: Item) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onSavedItem()")
                Log.i("TESTTEST","onSavedItem")

                for(key in dataRepositoryListener.keys) {
                    Log.i("TESTTEST","onSavedItem > " + key)
                    dataRepositoryListener[key]?.onSavedItem(item)
                }

            }

            override fun onDeleted(item: Item) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onDeleted()")

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onDeleted(item)
                }
            }

            override fun onReadSunuzuList(list: ArrayList<SunuzuItem>) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onReadSunuzuList()")

                editSunuzuList.postValue(list)

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadSunuzuList(list)
                }
            }

            override fun onReadAlarm(item: Alarm) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onReadAlarm()")

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadAlarm(item)
                }
            }

            override fun onReadAlarmList(list: List<Alarm>) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onReadAlarmList()")
                var alarm: Alarm? = null
                if (list.size > 0) {
                    alarm = list[0]
                }
                alarmListener?.onRead(alarm)
                alarmListener = null

                readAlarmList.postValue(list)
                readAlarm.postValue(alarm)

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onReadAlarmList(list)
                }
            }

            override fun onDeleteAlarm() {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onDeleteAlarm()")

                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onDeleteAlarm()
                }
            }

            override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {
                LogUtil.i(TAG,"setOnDatabaseListener() >>> onError()")
                alarmListener?.onError(type, error)
                alarmListener = null


                for(key in dataRepositoryListener.keys) {
                    dataRepositoryListener[key]?.onError(type, error)
                }
            }

        })
    }

    var readItemList = MutableLiveData<MutableList<ListItem>>()
    var readAlarmList = MutableLiveData<List<Alarm>>()
    var readAlarm = MutableLiveData<Alarm?>()

    var editItem = MutableLiveData<Item?>()
    var editSunuzuList = MutableLiveData<ArrayList<SunuzuItem>?>().apply {
        this.postValue(ArrayList<SunuzuItem>())
    }

    /**
     * DataRepositoryListener登録用メソッドと変数
     */
    var dataRepositoryListener = mutableMapOf<String, DataRepositoryListener?>()
    fun setOnDatabaseModelListener(key: String, listener: DataRepositoryListener?) {
        dataRepositoryListener.put(key, listener)
    }

    fun clearListener() {
        for(key in dataRepositoryListener.keys) {
            dataRepositoryListener.remove(key)
        }
    }

    /**
     * ListViewModel　アイテム取得、アイテム削除
     */
    fun setOnItemlistListener (listener: DataRepositoryListener?) {
        LogUtil.i(TAG,"setOnItemlistListener() " + (listener == null))
        setOnDatabaseModelListener("itemListListener", listener)
    }
    fun readList(){
        LogUtil.i(TAG,"readList()")
        databaseModel.readItemList()
    }
    fun deleteItem(item: Item) {
        LogUtil.i(TAG,"deleteItem()")
        databaseModel.deleteItem(item)
    }
    fun readItem(item_id: Int) {
        databaseModel.readItem(item_id)
    }

    /**
     * ListViewModel　アラーム停止
     */
    fun stopAlarm(item: Item, startDateTime: Long) {
        databaseModel.deleteAlarm(item.id, startDateTime)
    }
    fun stopAlarm(item: Item, listener: DataRepositoryListener?) {
        setOnDatabaseModelListener("alarmDeleteListener", listener)
        if (listener != null)databaseModel.deleteAlarm(item.id)
    }

    /**
     * ListViewModel　OnOff変更
     */
    fun saveItem(item: Item) {
        LogUtil.i(TAG + " ITEM_SAVE","saveItem()")

        var list = editSunuzuList.value
        databaseModel.saveItem(item, list)
    }
    fun saveItemListener(item: Item, listener: DataRepositoryListener?) {
        LogUtil.i(TAG + " ITEM_SAVE","saveItemListener()")

        var list = editSunuzuList.value
        list?.let {
            LogUtil.i(TAG + " ITEM_SAVE","list.size:" + list.size)
        }

        setOnDatabaseModelListener("saveItemListener", listener)
        if (listener != null)databaseModel.saveItem(item, editSunuzuList.value)
    }





    var alarmListener: AlarmDataListener? = null
    fun readAlarm(listener: AlarmDataListener){
        alarmListener = listener
        databaseModel.readAlarm()
    }

    fun readAlarm(){
        databaseModel.readAlarm()
    }

    fun readAlarm(time: Long){
        databaseModel.readAlarm(time)
    }

    fun skipAlarm(alarm: Alarm) {
        Log.i("TESTTEST","skipAlarm")
        databaseModel.deleteAlarm(alarm.owner_id, alarm.datetime)
    }

    ///////////////////////////////////////////////////////////////////////////////
    var editSunuzuPosition: Int = -1
    var editSunuzuNextMinute: Int = 5

    fun addSunuzuItem(item: SunuzuItem) {
        var list = ArrayList<SunuzuItem>()

        editSunuzuList.value?.let {
            list = it as ArrayList<SunuzuItem>
        }

        if (list.size == 0) {
            list.add(item)
        }else {
            var next = -1
            for(i in 0 until list.size){
                if(list[i].plusMinute > item.plusMinute) {
                    next = i
                    break
                }
            }

            if(next == -1) {
                list.add(item)
            }else {
                list.add(next, item)
            }
        }

        editSunuzuList.postValue(list)
    }

    fun setSunuzuItem(position: Int, item: SunuzuItem) {
        var list = ArrayList<SunuzuItem>()

        editSunuzuList.value?.let {
            list = it as ArrayList<SunuzuItem>
        }


        list.set(position, item)

        var newList = ArrayList<SunuzuItem>()

        list.forEach {item->
            if(newList.size == 0){
                newList.add(item)
            }else {
                var input = false
                for(i in 0 until newList.size){
                    if(newList[i].plusMinute > item.plusMinute) {
                        input = true
                        newList.add(i, item)
                        break
                    }
                }

                if(!input){
                    newList.add(item)
                }
            }
        }

        list = newList.clone() as ArrayList<SunuzuItem>

        editSunuzuList.postValue(list)
    }



    var editTimeItem = MutableLiveData<Item>().apply{value = null}


    var editSunuzuTimeItem = MutableLiveData<Item>().apply{value = null}
    var editSunuzuTimeItemPosition = -1



}