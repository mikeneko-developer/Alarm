package net.mikemobile.alarm.ui.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.mikemobile.alarm.util.MyDate;

/**
 * Created by mikeneko on 2016/07/24.
 */
public class CDialogTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    Dialog dialog;

    public static CDialogTimePicker newInstance(long time) {
        CDialogTimePicker fragment = new CDialogTimePicker();

        // ダイアログに渡すパラメータはBundleにまとめる
        Bundle arguments = new Bundle();
        arguments.putLong("time", time);
        fragment.setArguments(arguments);

        return fragment;
    }



    // ダイアログの横幅、高さ、表示位置を設定
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        lp.width = (int) (metrics.widthPixels * 0.8);//横幅を80%
        //lp.height = (int) (metrics.heightPixels * 0.8);//高さを80%
        //lp.x = 100; //表示位置を指定した分、右へ移動
        //lp.y = 200; //表示位置を指定した分、下へ移動
        dialog.getWindow().setAttributes(lp);
         */
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_time_picker, null, false);

        //ダイアログの作成
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        */

        long time = getArguments().getLong("time");

        int hour = MyDate.getHour(time);
        int minute = MyDate.getMinute(time);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getActivity(),this, hour, minute,true);

        return timePickerDialog;
    }

    private void Alert(){
        new AlertDialog.Builder(getActivity())
                .setMessage("")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(listener != null)listener.TimePickerSelect(hourOfDay,minute);
    }

    private DialogInterface.TimePickerSelect listener;
    public void setOnListener(DialogInterface.TimePickerSelect listener){
        this.listener = listener;
    }
}
