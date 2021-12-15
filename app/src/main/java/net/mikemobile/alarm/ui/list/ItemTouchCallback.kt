package net.mikemobile.alarm.ui.list

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface ItemTouchCallbackListener{
    fun onMove(fromPos:Int,toPos:Int)
    fun onSwiped(fromPos:Int)
}

class ItemTouchCallback(private val listener : ItemTouchCallbackListener): ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        var fromPos = viewHolder.adapterPosition

        var dragFlags = ItemTouchHelper.DOWN or ItemTouchHelper.UP
        var swipeFrags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT


        Log.i("ItemTouchCallback","getMovementFlags() >> dragFlags:" + dragFlags)
        Log.i("ItemTouchCallback","getMovementFlags() >> swipeFrags:" + swipeFrags)

        return makeMovementFlags(dragFlags, swipeFrags)
    }


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition

        Log.i("ItemTouchCallback","onMove() >> fromPos:" + fromPos)
        Log.i("ItemTouchCallback","onMove() >> toPos:" + toPos)

        listener.onMove(fromPos,toPos)

        return true// true if moved, false otherwise
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val fromPos = viewHolder.adapterPosition

        Log.i("ItemTouchCallback","onSwiped() >> direction:" + direction)
        Log.i("ItemTouchCallback","onSwiped() >> fromPos:" + fromPos)


        listener.onSwiped(fromPos)
        //adapter.deletePoint(fromPos)
        //adapter.notifyItemRemoved(fromPos)
    }

    /**
     * ドラッグアンドドロップ使用切り替え（長押し）
     */
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    /**
     * Swipeを使用切り替え
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

}