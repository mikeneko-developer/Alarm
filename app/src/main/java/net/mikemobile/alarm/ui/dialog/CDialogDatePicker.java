package net.mikemobile.alarm.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.mikemobile.alarm.util.CustomDateTime;
import net.mikemobile.alarm.util.MyDate;

/**
 * Created by mikeneko on 2016/07/24.
 */
public class CDialogDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Dialog dialog;

    public static CDialogDatePicker newInstance(long date) {
        CDialogDatePicker fragment = new CDialogDatePicker();

        // ダイアログに渡すパラメータはBundleにまとめる
        Bundle arguments = new Bundle();
        arguments.putLong("date", date);
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

        long date = getArguments().getLong("date");

        int year = MyDate.getYear(date);
        int month = MyDate.getMonth(date) - 1;
        int dayOfMonth = MyDate.getDay(date);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getActivity(),this, year, month, dayOfMonth);

        return datePickerDialog;

    }

    private void Alert(){
        new AlertDialog.Builder(getActivity())
                .setMessage("")
                .setPositiveButton("OK", null)
                .show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(listener != null)listener.DatePickerSelect(year,monthOfYear + 1,dayOfMonth);
    }

    private DialogInterface.DatePickerSelect listener;
    public void setOnListener(DialogInterface.DatePickerSelect listener){
        this.listener = listener;
    }
}
