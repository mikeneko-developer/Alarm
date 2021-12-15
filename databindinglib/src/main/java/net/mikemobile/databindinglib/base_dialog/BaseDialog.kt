package net.mikemobile.databindinglib.base_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.databinding.DataBindingUtil
import net.mikemobile.databindinglib.R


abstract class BaseDialog : DialogFragment() {
    abstract fun onCreateDialogView(savedInstanceState: Bundle?,dialog: Dialog)

    private var mContext: Context? = null


    private val BASE_DIALOG_LAYOUT = R.layout.base_dialog_layout
    private var dialogView:View? = null
    private var contentView:View? = null
    private var dataBinding = false

    open fun onTitleViewVisibility(bool:Boolean): Boolean{
        return bool
    }

    open fun onFullScreen(bool:Boolean): Boolean{
        return bool
    }

    open fun onDialogLayout(layout_id:Int): Int{
        return layout_id
    }

    open fun setContentView(input_layout_id:Int): Int{
        return input_layout_id
    }

    open fun onDataBindingView():View? {
        return null
    }

    open fun onBackgroundDrawable(color:Int):Drawable?{
        return ColorDrawable(color)
    }
    open fun onBackgroundDrawable(drawable:Drawable?):Drawable?{
        return drawable
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val factory = LayoutInflater.from(requireActivity())
        val dialog = Dialog(requireActivity())
        mContext = activity

        val bool2 = onTitleViewVisibility(false)
        val bool1 = onFullScreen(false)

        if(bool1 || bool2) {
            // タイトル非表示
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        if(bool1) {
            // フルスクリーン
            dialog.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
        }

        if(dataBinding){
            dialogView = onDataBindingView()
            if(dialogView != null){
                dialog.setContentView(dialogView!!)
            }

        }else {
            dialogView = factory.inflate(onDialogLayout(BASE_DIALOG_LAYOUT), null)
            dialog.setContentView(dialogView!!)

            var drawable = onBackgroundDrawable(Color.TRANSPARENT)
            if(drawable == null){
                drawable = onBackgroundDrawable(null)
            }

            if(drawable != null) {//背景を好きに変更したい場合はこちらを使用する

                // 背景を透明にする
                dialog.window!!.setBackgroundDrawable(drawable)
            }
        }

        var input_layout_id = setContentView(R.id.dialog_content_view)
        if(input_layout_id != 0 && input_layout_id != -1){
            contentView = dialog.findViewById(input_layout_id)
        }

        onCreateDialogView(savedInstanceState,dialog)

        return dialog
    }

    fun findViewById(layout_id:Int): View? {
        if(contentView != null){
            contentView?.let{
                return it.findViewById(layout_id)
            }
        }
        if(dialogView != null){
            dialogView?.let{
                return it.findViewById(layout_id)
            }
        }
        return null
    }

    fun open(activity: AppCompatActivity,tag:String) {
        this.show(activity.supportFragmentManager!!,tag)
    }

    fun close() {
        this.dismiss()

    }


    companion object {
        const val TAG = "BaseDialog"

        //fun newInstance(bundle : Bundle?) = BaseDialog().apply {
        //    arguments = bundle
        //}
    }
}