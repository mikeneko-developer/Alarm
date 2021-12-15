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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.mikemobile.alarm.R;
import net.mikemobile.media.MediaInfo;
import net.mikemobile.media.MediaManager;
import net.mikemobile.media.MediaUtilityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikeneko on 2016/09/10.
 */
public class DialogMusicList extends DialogFragment {

    Dialog dialog;


    // ダイアログの横幅、高さ、表示位置を設定
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        lp.width = (int) (metrics.widthPixels * 0.9);//横幅を80%
        lp.height = (int) (metrics.heightPixels * 0.8);//高さを80%
        //lp.x = 100; //表示位置を指定した分、右へ移動
        //lp.y = 200; //表示位置を指定した分、下へ移動
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_music_list, null, false);


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

    private ListView listArtist;
    private ListView listAlbum;
    private ListView listMusic;
    private ListAdapter adapterArtist;
    private ListAdapter adapterAlbum;
    private ListAdapterMusic adapterMusic;

    private MediaInfo mediaData;
    private int selectButton = -1;

    private int position = -1;
    private void setView(View view){


        MediaManager mediaManager = MediaUtilityManager.Companion.getMediaManager(getContext());
        ArrayList<MediaInfo> musicList = mediaManager.onReadMusicList();
        ArrayList<MediaInfo> albumList = mediaManager.onReadAlbumList();
        ArrayList<MediaInfo> artistList = mediaManager.onReadArtistList();

        listMusic = (ListView)view.findViewById(R.id.list_music);
        listAlbum = (ListView)view.findViewById(R.id.list_album);
        listArtist = (ListView)view.findViewById(R.id.list_artist);

        adapterMusic = new ListAdapterMusic(getContext(), new ArrayList<MediaInfo>());
        showMusicList(musicList);

        adapterAlbum = new ListAdapter(1, getContext(),albumList);
        listAlbum.setAdapter(adapterAlbum);
        listAlbum.setOnItemClickListener(listenerAlbum);

        adapterArtist = new ListAdapter(0, getContext(),artistList);
        listArtist.setAdapter(adapterArtist);
        listArtist.setOnItemClickListener(listenerArtist);

        Button btnArtist = (Button)view.findViewById(R.id.button_artist);
        Button btnAlbum = (Button)view.findViewById(R.id.button_album);
        Button btnMusic = (Button)view.findViewById(R.id.button_music);
        Button btnNegative = (Button)view.findViewById(R.id.button_negative);

        btnArtist.setOnClickListener(clickListener);
        btnAlbum.setOnClickListener(clickListener);
        btnMusic.setOnClickListener(clickListener);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        if(position != -1)listMusic.setSelection(position);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MediaManager mediaManager = MediaUtilityManager.Companion.getMediaManager(getContext());
            if (view.getId() == R.id.button_artist) {
                listMusic.setVisibility(View.INVISIBLE);
                listAlbum.setVisibility(View.INVISIBLE);
                listArtist.setVisibility(View.VISIBLE);
                selectButton = view.getId();
            } else if (view.getId() == R.id.button_album && selectButton != R.id.button_album) {
                adapterAlbum.setList(mediaManager.onReadAlbumList());
                listAlbum.setAdapter(adapterAlbum);

                listMusic.setVisibility(View.INVISIBLE);
                listAlbum.setVisibility(View.VISIBLE);
                listArtist.setVisibility(View.INVISIBLE);
                selectButton = view.getId();
            } else if (view.getId() == R.id.button_music && selectButton != R.id.button_music) {
                showMusicList(mediaManager.onReadMusicList());

                listMusic.setVisibility(View.VISIBLE);
                listAlbum.setVisibility(View.INVISIBLE);
                listArtist.setVisibility(View.INVISIBLE);
                selectButton = view.getId();
            }

        }
    };

    AdapterView.OnItemClickListener listenerArtist = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String artist = adapterArtist.list.get(i).getArtist();
            Toast.makeText(getContext(),"artist:"+artist, Toast.LENGTH_SHORT).show();

            MediaManager mediaManager = MediaUtilityManager.Companion.getMediaManager(getContext());

            List<MediaInfo> albumList = mediaManager.onReadArtistToAlbumList(artist);

            if (!albumList.get(0).getTitle().equals("戻る"))albumList.add(0, new MediaInfo("戻る"));

            adapterAlbum.setList( (ArrayList<MediaInfo>) albumList);
            listAlbum.setAdapter(adapterAlbum);
            listMusic.setVisibility(View.INVISIBLE);
            listAlbum.setVisibility(View.VISIBLE);
            listArtist.setVisibility(View.INVISIBLE);
            selectButton = -1;
        }
    };
    AdapterView.OnItemClickListener listenerAlbum = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String title = adapterAlbum.list.get(i).getTitle();
            String album = adapterAlbum.list.get(i).getAlbum();

            if (title.equals("戻る")) {
                listMusic.setVisibility(View.INVISIBLE);
                listAlbum.setVisibility(View.INVISIBLE);
                listArtist.setVisibility(View.VISIBLE);
                return;
            }

            Toast.makeText(getContext(),"album:"+album, Toast.LENGTH_SHORT).show();

            MediaManager mediaManager = MediaUtilityManager.Companion.getMediaManager(getContext());

            List<MediaInfo> musicList;

            if (adapterAlbum.list.get(0).getTitle().equals("戻る")) {
                String artist = adapterAlbum.list.get(i).getArtist();
                musicList = mediaManager.onReadArtistAndAlbumToMusicList(artist, album);
            } else {
                musicList = mediaManager.onReadAlbumToMusicList(album);
            }

            if (!musicList.get(0).getTitle().equals("戻る")) musicList.add(0, new MediaInfo("戻る"));

            showMusicList((ArrayList<MediaInfo>) musicList);

            listMusic.setVisibility(View.VISIBLE);
            listAlbum.setVisibility(View.INVISIBLE);
            listArtist.setVisibility(View.INVISIBLE);
        }
    };
    AdapterView.OnItemClickListener listenerMusic = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MediaInfo musicData = adapterMusic.list.get(i);

            if (musicData.getTitle().equals("戻る")) {
                listMusic.setVisibility(View.INVISIBLE);
                listAlbum.setVisibility(View.VISIBLE);
                listArtist.setVisibility(View.INVISIBLE);
                return;
            }
            Toast.makeText(getContext(),"title:"+musicData.getTitle(), Toast.LENGTH_SHORT).show();

            listener.selectMusic(musicData);
        }
    };



    public void showMusicList(ArrayList<MediaInfo> list) {
        int position = -1;

        if (mediaData != null && list.size() > 0) {
            for(int i=0;i<list.size();i++) {
                MediaInfo info = list.get(i);

                if(mediaData.getTitle().equals(info.getTitle())
                        && mediaData.getPath().equals(info.getPath())){
                    position = i;
                    break;
                }
            }
        }

        adapterMusic.setList((ArrayList<MediaInfo>) list);
        listMusic.setAdapter(adapterMusic);
        listMusic.setOnItemClickListener(listenerMusic);

        if(position != -1)listMusic.setSelection(position);
    }

    public void selectItem(MediaInfo string){
        mediaData = string;
    }

    private OnDialogMusicListListener listener;
    public void setOnItemClickListener(OnDialogMusicListListener l){
        listener = l;
    }

    public interface OnDialogMusicListListener {
        public abstract void selectMusic(MediaInfo mediaInfo);
    }

    public class ListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<MediaInfo> list;
        private int type = 0;
        public ListAdapter(int type, Context context,ArrayList<MediaInfo> list){
            this.context = context;
            this.list = list;
            this.type = type;
        }

        public void setList(ArrayList<MediaInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) return 0;
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
                convertView = layoutInflater_.inflate(R.layout.list_dialog_item_music, null);
            }

            TextView textView =(TextView)convertView.findViewById(R.id.textView2);
            if (type == 1) {
                textView.setText(list.get(posi).getAlbum());
            } else {
                textView.setText(list.get(posi).getArtist());
            }

            LinearLayout background = (LinearLayout)convertView.findViewById(R.id.background);

            if(mediaData != null && mediaData.getArtist().equals(list.get(posi))
                || mediaData != null && mediaData.getAlbum().equals(list.get(posi))){
                background.setBackgroundColor(Color.parseColor("#F6B448"));
            }else {
                background.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }
    }
    public class ListAdapterMusic extends BaseAdapter {

        private Context context;
        private ArrayList<MediaInfo> list;
        public ListAdapterMusic(Context context,ArrayList<MediaInfo> list){
            this.context = context;
            this.list = list;
        }

        public void setList(ArrayList<MediaInfo> list) {
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
                convertView = layoutInflater_.inflate(R.layout.list_dialog_item_music, null);
            }

            TextView textView =(TextView)convertView.findViewById(R.id.textView2);
            textView.setText(list.get(posi).getTitle());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView4);
            imageView.setImageURI(list.get(posi).getData());

            LinearLayout background = (LinearLayout)convertView.findViewById(R.id.background);

            if(mediaData != null && mediaData.getTitle().equals(list.get(posi).getTitle())
                    && mediaData.getPath().equals(list.get(posi).getPath())){
                background.setBackgroundColor(Color.parseColor("#F6B448"));
            }else {
                background.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }
    }
}
