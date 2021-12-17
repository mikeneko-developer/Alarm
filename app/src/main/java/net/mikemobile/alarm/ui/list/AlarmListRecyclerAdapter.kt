package net.mikemobile.alarm.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.mikemobile.alarm.R
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.databinding.ListItemBinding


class AlarmListRecyclerAdapter() : RecyclerView.Adapter<AlarmListViewHolder>() {
    lateinit var listener: OnItemClickListener

    private var items = ArrayList<ListItem>()

    constructor(l: OnItemClickListener) : this() {
        setOnItemClickListener(l)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmListViewHolder {
        //setOnItemClickListener(listener)

        // DataBinding
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        val item = items[position]

        // データセット
        holder.binding.setItem(item)

        if (item.alarm != null && item.alarm.sunuzu && item.alarm.sunuzu_count > 0) {
            holder.binding.listitemClicklayout.setBackgroundResource(R.drawable.frame_list_item_select)
        } else {
            holder.binding.listitemClicklayout.setBackgroundResource(R.drawable.frame_list_item)
        }

        //ClickListenerのセットはココ！
        holder.binding.listitemClicklayout.setOnClickListener {
            //処理はRecordModel#itemClickに実装
            listener.onItemClick(it, position, item)
        }

        holder.binding.listitemAlarmSwitch.setOnClickListener {
            //処理はRecordModel#itemClickに実装
            val isChecked = holder.binding.listitemAlarmSwitch.isChecked
            listener.onItemChecked(it, position, isChecked, item)
        }

        holder.binding.listItemAlarmStop.setOnClickListener {
            //処理はRecordModel#itemClickに実装
            val isChecked = holder.binding.listitemAlarmSwitch.isChecked
            listener.onItemAlarmStopClick(it, position, item)
        }

        holder.binding.listItemAlarmSkip.setOnClickListener {
            //処理はRecordModel#itemClickに実装
            listener.onItemAlarmSkipClick(it, position, item)
        }

        // Viewへの反映を即座に行う
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, data: ListItem)
        fun onItemAlarmSkipClick(view: View, position: Int, data: ListItem)
        fun onItemAlarmStopClick(view: View, position: Int, data: ListItem)
        fun onItemChecked(view: View, position: Int, isChecked: Boolean, data: ListItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    fun setList(items: ArrayList<ListItem>) {
        this.items = items
        this.notifyDataSetChanged()
    }


    private var deleteItem: ListItem? = null
    private var deletePosition: Int = -1
    fun deleteItem(fromPos: Int): Item {
        var item = items[fromPos]
        items.removeAt(fromPos)

        deleteItem = item
        deletePosition = fromPos

        notifyItemRemoved(fromPos)

        return item.data
    }

    fun deleteCancel() {
        if(deletePosition == -1) {
            return
        }

        if(deleteItem == null) {
            return
        }

        items.add(deletePosition, deleteItem!!)
        notifyItemInserted(deletePosition)

        deleteComplete()
    }

    fun deleteComplete() {
        deleteItem = null
        deletePosition = -1
    }

}