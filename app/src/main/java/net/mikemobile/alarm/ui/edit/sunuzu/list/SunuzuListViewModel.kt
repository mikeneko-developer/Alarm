package net.mikemobile.alarm.ui.edit.sunuzu.list

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.databindinglib.base.BaseFragment


class SunuzuListViewModel(private val databaseModel: DataBaseModel, private val dataRepository: DataRepository): ViewModel(),
    SunuzuListRecyclerAdapter.OnItemClickListener {

    var navigator : SunuzuListFragmentNavigator? = null

    val handler = Handler()

    val adapter = SunuzuListRecyclerAdapter(this)
    var listView: RecyclerView? = null

    var viewDateTime : MutableLiveData<String> = MutableLiveData<String>().apply{value = ""}

    fun setRecyclerView(view: RecyclerView, context: Context?) {
        listView = view
        listView?.setLayoutManager(LinearLayoutManager(context))
        listView?.setAdapter(adapter)
    }



    fun initialize(){
        dataRepository.editItem.value?.let{
            viewDateTime.postValue("" + it.getAlartDateTime())
        }
    }

    fun resume(fragment: BaseFragment){
        dataRepository.editSunuzuList.value?.let{
            loadAlarmList(it)
        }

    }

    fun destroy(){

    }


    fun updateList(){
        //val items = getList()
        //adapter.setList(items)
    }

    fun clickAddItem(){
        setNextTime()

        dataRepository.editSunuzuPosition = -1
        navigator?.onAddItem()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    private fun loadAlarmList(list:ArrayList<SunuzuItem>){
        Log.i("SunuzuListViewModel","loadAlarmList() >> list size:" + list.size)
        dataRepository.editItem.value?.let{
            adapter.setItem(it)
        }

        adapter.setList(list)

        handler.post(object:Runnable{
            override fun run() {
                listView?.invalidate()
                adapter.notifyDataSetChanged()

            }
        })
    }

    private fun setNextTime() {
        dataRepository.editSunuzuNextMinute = 0
        dataRepository.editSunuzuList.value?.let{
            for(item in it){
                if(dataRepository.editSunuzuNextMinute <= item.plusMinute ) {
                    dataRepository.editSunuzuNextMinute = item.plusMinute
                }
            }

        }
        dataRepository.editSunuzuNextMinute += 5
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onItemClick(view: View, position: Int, data: SunuzuItem) {
        setNextTime()
        dataRepository.editSunuzuPosition = position

        dataRepository.editSunuzuTimeItemPosition = position
        //dataRepository.editSunuzuTimeItem.postValue(data)
        navigator?.onAddItem()
    }

    override fun onItemDeleteClick(view: View, position: Int, data: SunuzuItem) {
        dataRepository.editSunuzuList.value?.let{
            for(i in 0 until it.size){
                var item = it.get(i)

                if(item.id == data.id){
                    it.removeAt(i)
                    break
                }
            }

            loadAlarmList(it)
        }
    }

    override fun onItemChecked(view: View, position: Int, isChecked: Boolean, data: SunuzuItem) {
        dataRepository.editSunuzuList.value?.let{
            for(i in 0 until it.size){
                var item = it.get(i)

                if(item.id == data.id){
                    item.onoff = isChecked
                    it.set(i,item)
                    break
                }
            }
        }
        //data.onoff = isChecked
        //dbRepository.saveItem(data,false)
    }
}