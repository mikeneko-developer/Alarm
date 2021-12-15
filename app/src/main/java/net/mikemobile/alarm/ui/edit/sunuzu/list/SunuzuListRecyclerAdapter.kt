package net.mikemobile.alarm.ui.edit.sunuzu.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.databinding.ListSunuzuItemBinding
import net.mikemobile.alarm.util.CustomDateTime

class SunuzuListRecyclerAdapter() : RecyclerView.Adapter<SunuzuListSunuzuViewHolder>() {
    lateinit var listener: OnItemClickListener

    private var item: Item = Item()
    fun setItem(data: Item) {
        item = data
    }

    private var items = ArrayList<SunuzuItem>()
    fun setList(items: ArrayList<SunuzuItem>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    constructor(l: OnItemClickListener): this(){
        setOnItemClickListener(l)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SunuzuListSunuzuViewHolder {
        //setOnItemClickListener(listener)

        // DataBinding
        val binding = ListSunuzuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SunuzuListSunuzuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SunuzuListSunuzuViewHolder, position: Int) {
        val data = items[position]

        // データセット
        holder.binding.setItem(data)

        item?.let{
            var dateText = CustomDateTime.getTimeText(CustomDateTime.getNextMinute(it.getDateTime(), data.plusMinute))
            holder.binding.listitemAlarmTime.setText(dateText)
        }



        //ClickListenerのセットはココ！
        holder.binding.listitemClicklayout.setOnClickListener{
            //処理はRecordModel#itemClickに実装
            listener.onItemClick(it, position, data)
        }

        holder.binding.listitemButtonDelete.setOnClickListener{
            //処理はRecordModel#itemClickに実装
            listener.onItemDeleteClick(it, position, data)
        }

        holder.binding.listitemAlarmSwitch.setOnClickListener{
            //処理はRecordModel#itemClickに実装
            val isChecked = holder.binding.listitemAlarmSwitch.isChecked
            listener.onItemChecked(it, position, isChecked, data)
        }

        // Viewへの反映を即座に行う
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position:Int, data: SunuzuItem)
        fun onItemDeleteClick(view: View, position:Int, data: SunuzuItem)
        fun onItemChecked(view: View, position:Int, isChecked:Boolean, data: SunuzuItem)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}