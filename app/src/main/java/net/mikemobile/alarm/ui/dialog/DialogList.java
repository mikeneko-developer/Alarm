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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.mikemobile.alarm.R;

import java.util.ArrayList;

/**
 * Created by mikeneko on 2016/09/10.
 */
public class DialogList extends DialogFragment {

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
        View view = inflater.inflate(R.layout.dialog_list, null, false);


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

    private ListView listView;
    private ListAdapter adapter;
    private ArrayList<String> list;
    private String title = null;
    private int position = -1;
    private void setView(View view){
        listView = (ListView)view.findViewById(R.id.listView2);

        adapter = new ListAdapter(getContext(),list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(listener);
        if(position != -1)listView.setSelection(position);
    }

    public void setList(ArrayList<String> list){
        this.list = list;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void addItem(String string){
        list.add(string);
        adapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener listener;
    public void setOnItemClickListener(AdapterView.OnItemClickListener l){
        listener = l;
    }

    public class ListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> list;
        public ListAdapter(Context context,ArrayList<String> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int posi, View convertView, ViewGroup parent) {

            if (null == convertView) {
                LayoutInflater layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater_.inflate(R.layout.list_dialog_item, null);
            }

            TextView textView =(TextView)convertView.findViewById(R.id.textView2);
            textView.setText(list.get(posi));

            LinearLayout background = (LinearLayout)convertView.findViewById(R.id.background);

            if(position == posi){
                background.setBackgroundColor(Color.parseColor("#F6B448"));
            }else {
                background.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }
    }
}
