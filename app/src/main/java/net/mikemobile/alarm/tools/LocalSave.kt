package net.mikemobile.alarm.tools

import android.content.Context
import net.mikemobile.alarm.util.PreferenceUtil

class LocalSave(val context: Context) {

    fun saveAlarmActive(bool: Boolean) {
        PreferenceUtil.setBoolean(context, "alarm_data","alarmActive", bool)
    }
    fun getAlarmActive(): Boolean {
        return PreferenceUtil.getBoolean(context, "alarm_data","alarmActive", false)
    }

    fun saveAlarmId(id: Int) {
        PreferenceUtil.setInteger(context, "alarm_data","alarmId", id)
    }
    fun getAlarmId(): Int {
        return PreferenceUtil.getInteger(context, "alarm_data","alarmId", -1)
    }

    fun savePhoneActive(bool: Boolean) {
        PreferenceUtil.setBoolean(context, "alarm_data","phoneActive", bool)
    }
    fun getPhoneActive(): Boolean {
        return PreferenceUtil.getBoolean(context, "alarm_data","phoneActive", false)
    }
}