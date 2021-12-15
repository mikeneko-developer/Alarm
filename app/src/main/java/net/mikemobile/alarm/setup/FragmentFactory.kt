package net.mikemobile.alarm.setup

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.mikemobile.alarm.ui.alarm.ShowAlarmFragment
import net.mikemobile.alarm.ui.clock.ClockFragment
import net.mikemobile.alarm.ui.debug.DebugFragment
import net.mikemobile.alarm.ui.edit.EditFragment
import net.mikemobile.alarm.ui.edit.sunuzu.edit.SunuzuEditFragment
import net.mikemobile.alarm.ui.edit.sunuzu.list.SunuzuListFragment
import net.mikemobile.alarm.ui.list.ListFragment
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseFragmentFactory
import net.mikemobile.databindinglib.base_drawer.BaseNavigationDrawerFragmentFactory


class FragmentFactory : BaseFragmentFactory() {

    override fun create(tag: String, bundle : Bundle?) : BaseFragment {
        return when(tag) {
            ListFragment.TAG -> ListFragment.newInstance()
            EditFragment.TAG -> EditFragment.newInstance()
            SunuzuListFragment.TAG -> SunuzuListFragment.newInstance()
            SunuzuEditFragment.TAG -> SunuzuEditFragment.newInstance()
            ShowAlarmFragment.TAG -> ShowAlarmFragment.newInstance(bundle)
            ClockFragment.TAG -> ClockFragment.newInstance()
            DebugFragment.TAG -> DebugFragment.newInstance()
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
        @Volatile private var INSTANCE: FragmentFactory? = null


    }
    override fun getInstance() =
        INSTANCE ?: synchronized(FragmentFactory::class.java) {
            INSTANCE ?: FragmentFactory() .also { INSTANCE = it }
        }

    override fun destroyInstance() {
        INSTANCE = null
    }
}