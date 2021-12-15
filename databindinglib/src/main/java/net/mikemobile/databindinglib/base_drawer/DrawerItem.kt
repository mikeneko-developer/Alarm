package net.mikemobile.databindinglib.base_drawer

import android.graphics.drawable.Drawable

class DrawerItem(){

    fun setItem(_id:Int,_image_id:Int,_title:String,_text:String) : DrawerItem {
        id = _id
        image_id = _image_id
        title = _title
        text = _text
        return this
    }

    var id:Int = -1
    var image_id:Int = -1
    var title:String = ""
    var text:String = ""

    private var image: Drawable? = null

    companion object {
        const val TAG = "DrawerItem"
        fun newInstance() = DrawerItem().apply {
            /*   arguments = Bundle().apply {
            putString(ARGUMENT_TASK_ID, taskId)
            }*/
        }
    }



}