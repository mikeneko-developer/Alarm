package net.mikemobile.alarm.database

import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem

interface OnDatabaseListener{
    fun onReadItem(item: Item?)
    fun onReadSunuzuList(list: ArrayList<SunuzuItem>)

    fun onReadItemList(list: List<ListItem>)
    fun onReadResultFindOnTimer(list: List<Alarm>)

    fun onSavedItem(item: Item)
    fun onDeleted(item: Item)



    ///////////
    fun onReadAlarm(item: Alarm)
    fun onReadAlarmList(list: List<Alarm>)
    fun onDeleteAlarm()


    fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String)
}