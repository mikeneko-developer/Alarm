package net.mikemobile.android.tools

import android.app.Activity
import android.app.Application

import android.os.Bundle
import net.mikemobile.alarm.MainActivity


class MyLifecycleHandler: Application.ActivityLifecycleCallbacks {
    /**
     * アプリが前面にいるかどうかを取得します.
     * @return Foregroundにいたら`true`,backgroundにいたら`false`をかえします
     */
    var isForeground = false
        private set

    var isActive = false
        private set

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity is MainActivity) {
            isActive = true
        }
    }
    override fun onActivityDestroyed(activity: Activity) {
        if (activity is MainActivity) {
            isActive = false
        }
    }
    override fun onActivityResumed(activity: Activity) {
        if (activity is MainActivity) {
            isForeground = true
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is MainActivity) {
            isForeground = false
        }
    }

}