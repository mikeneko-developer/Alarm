package net.mikemobile.databindinglib.base_drawer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseFragmentFactory

open class BaseNavigationDrawerFragmentFactory : BaseFragmentFactory() {

    override fun create(tag: String, bundle : Bundle?) : BaseFragment {
        return when(tag) {
            //ProgressBarFragment.TAG -> ProgressBarFragment.newInstance(bundle)
            else -> super.create(tag,bundle)
        }
    }
    override fun createDialog(tag: String, bundle : Bundle?) : DialogFragment {
        return when(tag) {
            //ProgressBarFragment.TAG -> ProgressBarFragment.newInstance(bundle)
            else -> super.createDialog(tag,bundle)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: BaseNavigationDrawerFragmentFactory? = null


    }
    override fun getInstance() =
        INSTANCE ?: synchronized(BaseNavigationDrawerFragmentFactory::class.java) {
            INSTANCE ?: BaseNavigationDrawerFragmentFactory() .also { INSTANCE = it }
        }

    override fun destroyInstance() {
        INSTANCE = null
    }
}