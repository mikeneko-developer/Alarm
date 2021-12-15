package net.mikemobile.alarm.setup

import android.app.Activity
import android.os.Bundle
import net.mikemobile.alarm.MainViewModel
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.repository.AlarmRepository
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.ui.alarm.ShowAlarmViewModel
import net.mikemobile.alarm.ui.clock.ClockViewModel
import net.mikemobile.alarm.ui.debug.DebugViewModel
import net.mikemobile.alarm.ui.edit.EditViewModel
import net.mikemobile.alarm.ui.edit.sunuzu.edit.SunuzuEditViewModel
import net.mikemobile.alarm.ui.edit.sunuzu.list.SunuzuListViewModel
import net.mikemobile.alarm.ui.list.ListViewModel
import net.mikemobile.android.music.MediaController
import net.mikemobile.android.music.OnMediaListener
import net.mikemobile.android.tools.MyLifecycleHandler
import net.mikemobile.databindinglib.BaseActivityApplication
import net.mikemobile.databindinglib.base.ActivityNavigator
import net.mikemobile.databindinglib.base.BaseFragmentFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module





class DataBindingApplication : BaseActivityApplication() {
    lateinit var lifecycleHandler: MyLifecycleHandler

    fun isAppForeground(): Boolean {
        return lifecycleHandler.isForeground
    }
    fun isAppActived(): Boolean {
        return lifecycleHandler.isActive
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleHandler = MyLifecycleHandler()
    }

    override fun onFragmentFractory(): BaseFragmentFactory {
        return FragmentFactory()
    }

    override fun onViewModule(): Module {
        return module {
            viewModel { MainViewModel(get(),get()) }
            viewModel { ListViewModel(this.androidContext(), get()) }
            viewModel { EditViewModel(this.androidContext(), get(), get(), get()) }
            viewModel { SunuzuListViewModel(get(), get()) }
            viewModel { SunuzuEditViewModel(get(), get(),get(),get()) }
            viewModel { ShowAlarmViewModel(get(), get(), get()) }
            viewModel { ClockViewModel(get(), get()) }
            viewModel { DebugViewModel(this.androidContext()) }
        }
    }

    override fun onModelModule(): Module {
        return module {
            single { DataBaseModel(this.androidContext()) }
            single { DataRepository(get()) }
            single { AlarmRepository(this.androidContext(), get()) }
        }
    }

    override fun onNavigatorModule(): Module {
        return module {
            single { ActivityNavigator() }
        }
    }

    override fun onOtherModule(): Module {
        return module {
            single { MediaController(this.androidContext()) as OnMediaListener }
            //single { NetworkController() as INetworkController }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

}