package net.mikemobile.alarm.ui.list

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.repository.*
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment


class ListViewModel(
    private val context: Context,
    private val dataRepository: DataRepository
): ViewModel(),
    AlarmListRecyclerAdapter.OnItemClickListener {

    companion object {
        const val TAG = "ListViewModel"
    }
    var navigator : ListFragmentNavigator? = null
    val handler = Handler()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    val readItemListObserver = Observer<MutableList<ListItem>> { value ->
        value?.let {
            LogUtil.i(TAG + " ITEM_SAVE","リスト更新")
            handler.post {
                adapter.setList(value as ArrayList<ListItem>)

                adapter.deleteCancel()
                adapter.notifyDataSetChanged()
                listView?.let{
                    //it.invalidate()
                }
            }

            navigator?.onUpdateAlarm()
        }
    }

    fun initialize(){
    }

    fun resume(fragment: BaseFragment){
        LogUtil.i(TAG,"resume()")
        dataRepository.readItemList.observe(fragment, readItemListObserver)

        // ガイド終了時の処理
        dataRepository.readList()
    }
    fun pause(fragment: BaseFragment){
        LogUtil.i(TAG,"pause()")
        dataRepository.readItemList.removeObserver(readItemListObserver)
    }

    fun destroy(){

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun leftClick() {
        //
        navigator?.onOpenClock()
    }

    fun rightClick() {
        //
        navigator?.onAddItem(null)

    }

    fun clickAddItem(){
    }

    fun clickOpenClock() {
    }

    /**
     * クリックイベント
     * スイッチ
     */
    fun clickChangeSwitch(isChecked: Boolean, listItem: ListItem) {
        listItem.data.onoff = isChecked
        dataRepository.saveItem(listItem.data)

    }

    /**
     * クリックイベント
     * アラーム停止
     */
    fun clickAlarmStop(item: ListItem) {
        // アラーム終了ボタン押下
        dataRepository.stopAlarm(item.data, item.alarm.datetime)
    }

    /**
     * クリックイベント
     * 現在のアラームをスキップする
     */
    fun clickSkipAlarm(item: ListItem) {
        Log.i("TESTTEST","clickSkipAlarm")
        // アラームデータ削除
        dataRepository.skipAlarm(item.alarm)
    }

    fun clickPrevSkipAlarm(item: ListItem) {
        dataRepository.skipPrevAlarm(item.alarm)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun updateList(){
        handler.post(object:Runnable{
            override fun run() {
                adapter.deleteCancel()
                listView?.let{
                    it.invalidate()
                }
            }
        })
    }

    fun deleteListItem(item: Item) {
        dataRepository.deleteItem(item)
        adapter.deleteComplete()
    }


    fun clickDebug() {
        navigator?.onDebug()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    val adapter = AlarmListRecyclerAdapter(this)
    var listView: RecyclerView? = null
    fun setRecyclerView(view: RecyclerView, context: Context?) {
        listView = view
        listView?.setLayoutManager(LinearLayoutManager(context))
        listView?.setAdapter(adapter)
    }

    val itemTouchCallbackListener = object:ItemTouchCallbackListener{
        override fun onMove(fromPos: Int, toPos: Int) {
            moveItem(fromPos,toPos)
        }

        override fun onSwiped(fromPos: Int) {
            deleteItem(fromPos)
        }
    }

    val itemTouchCallBack = ItemTouchCallback(this.itemTouchCallbackListener)
    val mIth = ItemTouchHelper(itemTouchCallBack)

    private fun moveItem(fromPos: Int, toPos: Int){
        adapter.notifyItemMoved(fromPos, toPos)

    }

    private fun deleteItem(fromPos: Int){
        val item = adapter.deleteItem(fromPos)
        navigator?.onListItemDelete(fromPos, item)
    }

    override fun onItemClick(view: View, position: Int, data: ListItem) {
        LogUtil.i(TAG,"onItemClick")
        navigator?.onAddItem(data)
    }

    /**
     * アラームをスキップする
     */
    override fun onItemAlarmSkipClick(view: View, position: Int, data: ListItem) {
        clickSkipAlarm(data)
    }

    override fun onItemPrevAlarmSkipClick(view: View, position: Int, data: ListItem) {
        clickPrevSkipAlarm(data)
    }

    override fun onItemAlarmStopClick(view: View, position: Int, data: ListItem) {
        // アラーム終了ボタン押下
        clickAlarmStop(data)
    }

    override fun onItemChecked(view: View, position: Int, isChecked: Boolean, data: ListItem) {
        clickChangeSwitch(isChecked, data)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}