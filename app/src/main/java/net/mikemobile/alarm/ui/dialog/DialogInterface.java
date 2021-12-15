package net.mikemobile.alarm.ui.dialog;

/**
 * Created by mikeneko on 2016/07/24.
 */
public class DialogInterface {

    public interface TimePickerSelect {
        public abstract void TimePickerSelect(int hour, int minute);
    }
    public interface DatePickerSelect {
        public abstract void DatePickerSelect(int year, int month, int day);
    }
}
