package net.mikemobile.alarm.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.mikemobile.alarm.R;
import net.mikemobile.alarm.database.entity.Item;

import java.util.ArrayList;

/**
 * Created by mikeneko on 2016/09/10.
 */
public class DialogMessage extends DialogFragment {

    Dialog dialog;


    // ダイアログの横幅、高さ、表示位置を設定
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        lp.width = (int) (metrics.widthPixels * 0.8);//横幅を80%
        //lp.height = (int) (metrics.heightPixels * 0.8);//高さを80%
        //lp.x = 100; //表示位置を指定した分、右へ移動
        //lp.y = 200; //表示位置を指定した分、下へ移動
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message, null, false);


        setView(view);

        //ダイアログの作成
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return dialog;
    }

    private void Alert(){
        new AlertDialog.Builder(getActivity())
                .setMessage("")
                .setPositiveButton("OK", null)
                .show();
    }

    public void close(){
        dialog.dismiss();

    }

    private int position = -1;
    private Item item = new Item();

    private TextView tv_message;
    private Button btn_negative;
    private Button btn_positive;

    private String message = "";
    private String negative = "Cancel";
    private String positive = "OK";

    private OnDialogMessageListener listener;

    private void setView(View view){
        tv_message = (TextView)view.findViewById(R.id.dialog_message_text);
        btn_negative = (Button)view.findViewById(R.id.dialog_message_button_negative);
        btn_positive = (Button)view.findViewById(R.id.dialog_message_button_positive);

        tv_message.setText(message);
        btn_negative.setText(negative);
        btn_positive.setText(positive);

        btn_negative.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClickNegative(position, item);
                }
                close();
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClickPositive(position, item);
                }
                close();
            }
        });
    }

    public void setMessage(String str) {
        message = str;
    }
    public void setNegativeButton(String str) {
        negative = str;
    }
    public void setPositiveButton(String str) {
        positive = str;
    }

    public void setListsItem(int posi, Item data) {
        position = posi;
        item = data;
    }

    public void setOnDialogMessageListener(OnDialogMessageListener l) {
        listener = l;
    }

    public interface OnDialogMessageListener {
        public abstract void onClickPositive(int position, Item item);
        public abstract void onClickNegative(int position, Item item);
    }
}
