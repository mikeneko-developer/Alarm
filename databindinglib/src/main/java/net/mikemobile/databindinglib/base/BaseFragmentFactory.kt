package net.mikemobile.databindinglib.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.DialogFragment


interface BaseFragmentFactoryInterface{
    fun createFragment(tag: String, bundle : Bundle?): BaseFragment?
    fun createDialogFragment(tag: String, bundle : Bundle?): DialogFragment?
}


open class BaseFragmentFactory  {

    open fun create(tag: String, bundle : Bundle?) : BaseFragment =
        when {
            else -> throw IllegalArgumentException("Unknown Fragment tag: ${tag}")
        }

    open fun createDialog(tag: String, bundle : Bundle?) : DialogFragment =
        when {
            else -> throw IllegalArgumentException("Unknown Fragment tag: ${tag}")
        }



    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: BaseFragmentFactory? = null


    }

    open fun getInstance() =
        INSTANCE
            ?: synchronized(BaseFragmentFactory::class.java) {
                INSTANCE
                    ?: BaseFragmentFactory()
                        .also { INSTANCE = it }
            }

    open fun destroyInstance() {
        INSTANCE = null
    }
}