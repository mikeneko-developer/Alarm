package net.mikemobile.databindinglib.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import net.mikemobile.databindinglib.R
import org.koin.android.ext.android.inject


abstract class BaseFragment : Fragment() {
    val activityNavigator: ActivityNavigator by inject()


    inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    f()
                }
            }
        })
    }

    var BASE_LAYOUT_ID = R.layout.base_simple_fragment
    private var BASE_VIEW :View? = null
    var mContext: Context? = null


    open fun isDataBinding(): Boolean{
        return true
    }

    abstract fun onActivityCreate(savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(!isDataBinding()) {
            var layout_id = onContentView(BASE_LAYOUT_ID)

            BASE_VIEW = inflater.inflate(layout_id, container, false)

            if (layout_id == BASE_LAYOUT_ID) {
                setupLayout(savedInstanceState)
            }
            onCreateView(savedInstanceState)
        }else {
            return onCreateViewBinding(inflater, container, savedInstanceState)
        }

        return BASE_VIEW
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        android.util.Log.i("BaseFragment","onActivityCreated()")
        onActivityCreate(savedInstanceState)
    }

    override fun onStart(){
        super.onStart()
        android.util.Log.i("BaseFragment","onStart()")
    }
    override fun onStop(){
        super.onStop()
        android.util.Log.i("BaseFragment","onStop()")
    }
    override fun onDestroy(){
        super.onDestroy()
        android.util.Log.i("BaseFragment","onDestroy()")
    }


    open fun onBackStackResume() {

    }

    /**
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        activityNavigator.onActivityResulted(requestCode,resultCode,data)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    open fun onBack() {
        android.util.Log.i("BaseFragment","onBack()")
        activityNavigator.onBack()
    }

    open fun onContentView(base_layout_id :Int): Int {
        return base_layout_id
    }

    fun onCreateView(savedInstanceState: Bundle?) {

    }

    open fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return null
    }


    open fun findViewById(id:Int): View? {
        return BASE_VIEW?.findViewById(id)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun setupLayout(savedInstanceState: Bundle?){

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
}