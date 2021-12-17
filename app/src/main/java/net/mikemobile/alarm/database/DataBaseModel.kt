package net.mikemobile.alarm.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.core.AlarmCalc
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.Constant.Companion.SUNUZU_TIME_LIST
import net.mikemobile.alarm.util.CustomDateTime

class DataBaseModel(private val context: Context){

    val TAG = "DataBaseModel"

    var isUpdateItem = MutableLiveData<Boolean>().apply{value = false}
    var isUpdateNoti = MutableLiveData<Boolean>().apply{value = false}
    var dbModel = DataBaseManager.getInstance(context)


    fun init(){
    }

    fun getContext():Context {
        return context
    }

    var clockListener: OnDatabaseListener? = null
    fun setOnDatabaseClockListener(l: OnDatabaseListener?){
        clockListener = l
    }

    var serviceListener: OnDatabaseListener? = null
    fun setOnDatabaseToServiceListener(listener: OnDatabaseListener?){
        serviceListener = listener
    }

    var databaseModelListener = mutableMapOf<String, OnDatabaseListener?>()
    fun setOnDatabaseModelListener(key: String, listener: OnDatabaseListener?, kyousei: Boolean = false) {

        LogUtil.i(TAG + "","setOnDatabaseModelListener() size:" + databaseModelListener.size)
        LogUtil.i(TAG + "","setOnDatabaseModelListener() key:" + key)

        //classData::class.java.name
        databaseModelListener.put(key, listener)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DataBaseRepository内でのみ呼ばれる処理

    /**
     * IDからItemを取得する（アラーム実行時、スヌーズ判定での情報取得に使用する）
     */
    private fun readItemData(owner_id: Int): Item? {
        val item = dbModel.onItemDataDao().find(owner_id)
        return item
    }

    private fun readItemListData(): MutableList<ListItem> {
        var list = mutableListOf<ListItem>()

        var itemList = dbModel.onItemDataDao().findAll()
        var count = list.size

        for(item in itemList) {
            val sunuzuList =  dbModel.onSunuzuItemDataDao().findAll(item.id) as ArrayList<SunuzuItem>

            val listItem = ListItem(item, sunuzuList, Alarm(), false)

            dbModel.onAlarmDataDao().findIsOnOwner(item.id)?.let{
                Log.i(TAG + " ITEM_SAVE", "readItemListData() >> " + it)
                if(it.sunuzu_count >= -1){
                    listItem.alarm = it
                    listItem.alarmFlag = true
                }
            }

            list.add(listItem)
        }
        return list
    }

    private fun saveItemData(item: Item, list: ArrayList<SunuzuItem>?): Item {
        LogUtil.i(TAG + " ITEM_SAVE","saveItemData()")
        var res = 0
        if(item.id <= 0) {
            var id = dbModel.onItemDataDao().create(item)
            item.id = id.toInt()
        }else {
            dbModel.onItemDataDao().update(item)
        }

        list?.let{

            LogUtil.i(TAG + " ITEM_SAVE","saveItemData() sunuzu size:" + it.size)

            // スヌーズ設定を保存する
            dbModel.onSunuzuItemDataDao().delete(item.id)

            it.forEach{sunuzuItem ->
                sunuzuItem.owner_id = item.id
                dbModel.onSunuzuItemDataDao().create(sunuzuItem)
            }
        }

        return item
    }

    private fun deleteItemData(item: Item) {
        LogUtil.i(TAG,"deleteItemData()")
        dbModel.onItemDataDao().delete(item)
        dbModel.onSunuzuItemDataDao().delete(item.id)
        dbModel.onAlarmDataDao().deleteToOwnerItem(item.id)
    }

    /**
     * アイテム情報からアラームを有効・無効を切り替える
     */
    private fun saveItemToAlarm(item: Item) {
        LogUtil.i(TAG + " ITEM_SAVE","saveItemToAlarm() onoff:" + item.onoff)
        if(item.onoff){
            dbModel.onAlarmDataDao().deleteToOwnerItem(item.id)

            var alarm = AlarmCalc.calcAlarm(item)
            dbModel.onAlarmDataDao().create(alarm)
        }else {
            dbModel.onAlarmDataDao().deleteToOwnerItem(item.id)
        }
    }

    private fun readAlarmData(alarmId: Int): Alarm? {
        return dbModel.onAlarmDataDao().find(alarmId)
    }

    private fun readAlarmListData(): List<Alarm> {
        return dbModel.onAlarmDataDao().findAll()
    }

    private fun deleteAlarmData(alarm: Alarm) {
        dbModel.onAlarmDataDao().deleteToOwnerItem(alarm.owner_id)

    }

    private fun deleteAlarmData(owner_id: Int) {
        dbModel.onAlarmDataDao().deleteToOwnerItem(owner_id)

    }

    /**
     * 次のスヌーズセット用
     */
    private fun setNextSunuzu(alarm: Alarm, item: Item): Boolean {

        val nextAlarm = AlarmCalc.calcNextSunuzu(alarm, item)
        if (nextAlarm == null) {
            return false
        } else {
            dbModel.onAlarmDataDao().create(nextAlarm)
            return true
        }
    }

    /**
     * 次のカスタムスヌーズセット用
     */
    private fun setNextCustomSunuzu(alarm: Alarm, item: Item): Boolean {
        LogUtil.i(TAG, "setNextCustomSunuzu")

        val list = dbModel.onSunuzuItemDataDao().findAll(item.id)

        val nextAlarm = AlarmCalc.calcNextCustomSunuzu(alarm, item, list)

        if (nextAlarm == null) {
            return false
        } else {
            dbModel.onAlarmDataDao().create(nextAlarm)
            return true
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // アイテムリスト（リスト表示するアラームのデータ）

    /**
     * IDからItemを取得する（アラーム実行時、スヌーズ判定での情報取得に使用する）
     */
    fun readItem(id: Int) {
        var item: Item? = null
        var sunuzuList = ArrayList<SunuzuItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                item = dbModel.onItemDataDao().find(id)
                sunuzuList = dbModel.onSunuzuItemDataDao().findAll(id) as ArrayList<SunuzuItem>
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadItem(item)
                    databaseModelListener[key]?.onReadSunuzuList(sunuzuList)
                }
            },
            onError = {}
        )
    }

    /**
     * リスト取得用
     */
    fun readItemList() {
        var list = mutableListOf<ListItem>()
        Completable.fromCallable {
            dbModel.runInTransaction {
                list = readItemListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadItemList(list)
                }
            },
            onError = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Itemリスト読み込みエラー")
                }
            }
        )
    }

    /**
     * 新規アイテム保存、上書き保存用
     */
    fun saveItem(item: Item) {
        saveItem(item, null)
    }
    fun saveItem(item: Item?, list: java.util.ArrayList<SunuzuItem>?) {
        Log.i(TAG + " ITEM_SAVE","saveItem")
        if (item == null) {
            for(key in databaseModelListener.keys) {
                databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.SAVE, "Itemデータがありません")
            }
            return
        }

        var readItemList = mutableListOf<ListItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                val saveItem = saveItemData(item, list)

                // onoffフラグがTrueの場合、アラームアイテムとして登録する。falseの場合は解除する
                saveItemToAlarm(saveItem)

                //データが更新されたのでリストを再取得する
                readItemList = readItemListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                Log.i(TAG + " ITEM_SAVE","onComplete " + databaseModelListener.keys.size)
                for(key in databaseModelListener.keys) {
                    Log.i(TAG + " ITEM_SAVE","onComplete key:" + key)
                    databaseModelListener[key]?.onSavedItem(item)
                    databaseModelListener[key]?.onReadItemList(readItemList)
                }
            },
            onError = {
                Log.i(TAG + " ITEM_SAVE","onError")
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.SAVE, "Item保存エラー")
                }
            }
        )
    }

    /**
     * アラーム削除用
     */
    fun deleteItem(item: Item) {
        var readItemList = mutableListOf<ListItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                deleteItemData(item)

                //データが更新されたのでリストを再取得する
                readItemList = readItemListData()

            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onDeleted(item)
                    databaseModelListener[key]?.onReadItemList(readItemList)
                }
            },
            onError = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.SAVE, "Item削除エラー")
                }
            }
        )
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Serviceで実際にアラームとして有効になった場合のアラームの情報（基本、Itemと1対1）

    /**
     * IDからItemを取得する（アラーム実行時、スヌーズ判定での情報取得に使用する）
     */
    fun readAlarm(id: Int) {
        var item: Alarm? = null
        Completable.fromCallable {
            dbModel.runInTransaction {
                item = readAlarmData(id)
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                if (item != null) {
                    serviceListener?.onReadAlarm(item!!)
                }
            },
            onError = {}
        )
    }
    fun readAlarm() {
        var list:List<Alarm>? = null
        Completable.fromCallable {
            dbModel.runInTransaction {
                list = readAlarmListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                if(list == null){
                    list = ArrayList<Alarm>()
                }
                clockListener?.onReadAlarmList(list!!)
                serviceListener?.onReadAlarmList(list!!)

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadAlarmList(list!!)
                }
            },
            onError = {
                clockListener?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")
                serviceListener?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")
                }
            }
        )
    }


    fun readAlarm(time: Long) {
        var list:List<Alarm>? = null
        Completable.fromCallable {
            dbModel.runInTransaction {
                list = dbModel.onAlarmDataDao().findIsOnTimer(time)
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                if(list == null){
                    list = ArrayList<Alarm>()
                }

                clockListener?.onReadAlarmList(list!!)
                serviceListener?.onReadAlarmList(list!!)

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadAlarmList(list!!)
                }
            },
            onError = {
                clockListener?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")
                serviceListener?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.READ, "Alarm List 読み込みエラー")
                }
            }
        )
    }

    fun deleteAlarm(alarm: Alarm) {
        var readItemList = mutableListOf<ListItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                deleteAlarmData(alarm)

                val item = readItemData(alarm.owner_id)

                item?.let{
                    if(item.type == Constant.Companion.AlarmType.OneTime.id){
                        item.onoff = false
                        val saveItemData = saveItemData(item, null)

                    }else if(item.type == Constant.Companion.AlarmType.LoopWeek.id) {

                        var nextAlarm = AlarmCalc.calcAlarm(item, 0, CustomDateTime.getTimeInMillis())
                        dbModel.onAlarmDataDao().create(nextAlarm)
                    }
                }

                //データが更新されたのでリストを再取得する
                readItemList = readItemListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                serviceListener?.onDeleteAlarm()

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadItemList(readItemList)
                    databaseModelListener[key]?.onDeleteAlarm()
                }
            },
            onError = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.DELETE, "ALARM 削除失敗")
                }
            }
        )
    }

    fun deleteAlarm(owner_id: Int, start_datetime: Long = 0L) {
        Log.i(TAG,"deleteAlarm")
        var readItemList = mutableListOf<ListItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                deleteAlarmData(owner_id)

                val item = readItemData(owner_id)

                Log.i(TAG,"item = " + (item != null))
                item?.let{
                    if(item.type == Constant.Companion.AlarmType.OneTime.id){
                        Log.i(TAG,"OneTime")
                        item.onoff = false
                        val saveItemData = saveItemData(item, null)

                    }else if(item.type == Constant.Companion.AlarmType.LoopWeek.id) {
                        Log.i(TAG,"LoopWeek")
                        var nextAlarm = AlarmCalc.calcAlarm(item, 0, start_datetime)
                        dbModel.onAlarmDataDao().create(nextAlarm)
                    }
                }

                //データが更新されたのでリストを再取得する
                readItemList = readItemListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                Log.i(TAG,"onComplete")
                serviceListener?.onDeleteAlarm()

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onDeleteAlarm()
                    databaseModelListener[key]?.onReadItemList(readItemList)
                }
            },
            onError = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.DELETE, "ALARM 削除失敗")
                }
            }
        )
    }


    fun deleteAlarmToSunuzu(alarm: Alarm) {
        var readItemList = mutableListOf<ListItem>()

        Completable.fromCallable {
            dbModel.runInTransaction {
                val owner_id = alarm.owner_id

                // アラームデータを削除する
                deleteAlarmData(alarm)

                // 元データを取得する
                val item = readItemData(owner_id)

                item?.let{

                    val finish : Boolean
                    if(it.sunuzu_custom){
                        // カスタムスヌーズ
                        finish = setNextCustomSunuzu(alarm, it)

                    }else {
                        // 通常スヌーズ
                        finish = setNextSunuzu(alarm, it)
                    }

                    if (!finish) {
                        // スヌーズが終わったため、次の処理
                        if (it.type == 0) {
                            // １回だけなので、終了としフラグを落とす
                            it.onoff = false

                            saveItemData(it, null)
                        }
                    }
                }

                //データが更新されたのでリストを再取得する
                readItemList = readItemListData()
            }
        }.subscribeOn(Schedulers.io()).subscribeBy(
            onComplete = {
                serviceListener?.onDeleteAlarm()

                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onReadItemList(readItemList)
                    databaseModelListener[key]?.onDeleteAlarm()
                }
            },
            onError = {
                for(key in databaseModelListener.keys) {
                    databaseModelListener[key]?.onError(DataBaseManager.Companion.ERROR_TYPE.DELETE, "ALARM 削除失敗")
                }
            }
        )
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


}