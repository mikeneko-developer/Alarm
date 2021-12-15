package net.mikemobile.alarm.ui.dialog

import android.app.Dialog
import android.content.Context
import net.mikemobile.media.MediaUtilityManager.Companion.getMediaManager
import net.mikemobile.media.ThumbnailManager.Companion.getArtWork
import android.os.Bundle
import android.util.DisplayMetrics
import net.mikemobile.alarm.R
import android.graphics.drawable.ColorDrawable
import net.mikemobile.alarm.ui.dialog.DialogMusicList.ListAdapterMusic
import net.mikemobile.media.MediaInfo
import net.mikemobile.media.MediaUtilityManager
import net.mikemobile.alarm.ui.dialog.DialogMusicList.OnDialogMusicListListener
import android.graphics.Bitmap
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.mikemobile.media.MaskImage
import net.mikemobile.media.ThumbnailManager
import java.util.ArrayList

/**
 * Created by mikeneko on 2016/09/10.
 */
class DialogMusicList : DialogFragment() {
    var TAG = "DialogMusicList"

    // ダイアログの横幅、高さ、表示位置を設定
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val lp = dialog?.window!!.attributes
        val metrics = resources.displayMetrics
        lp.width = (metrics.widthPixels * 0.9).toInt() //横幅を80%
        lp.height = (metrics.heightPixels * 0.8).toInt() //高さを80%
        //lp.x = 100; //表示位置を指定した分、右へ移動
        //lp.y = 200; //表示位置を指定した分、下へ移動
        dialog?.window!!.attributes = lp
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_music_list, null, false)
        setView(view)

        //ダイアログの作成
        var dialog = Dialog(requireActivity())
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun Alert() {
        AlertDialog.Builder(requireActivity())
            .setMessage("")
            .setPositiveButton("OK", null)
            .show()
    }

    fun close() {
        dialog!!.dismiss()
    }

    private var listArtist: ListView? = null
    private var listAlbum: ListView? = null
    private var listMusic: ListView? = null
    private var adapterArtist: ListAdapter? = null
    private var adapterAlbum: ListAdapter? = null
    private var adapterMusic: ListAdapterMusic? = null
    private var mediaData: MediaInfo? = null
    private var selectButton = -1
    private val position = -1
    private fun setView(view: View) {
        val mediaManager = getMediaManager(
            requireContext()
        )
        val musicList = mediaManager.onReadMusicList()
        val albumList = mediaManager.onReadAlbumList()
        val artistList = mediaManager.onReadArtistList()
        listMusic = view.findViewById<View>(R.id.list_music) as ListView
        listAlbum = view.findViewById<View>(R.id.list_album) as ListView
        listArtist = view.findViewById<View>(R.id.list_artist) as ListView
        adapterMusic = ListAdapterMusic(requireContext(), ArrayList())
        showMusicList(musicList)
        adapterAlbum = ListAdapter(1, requireContext(), albumList)
        listAlbum!!.adapter = adapterAlbum
        listAlbum!!.onItemClickListener = listenerAlbum
        adapterArtist = ListAdapter(0, requireContext(), artistList)
        listArtist!!.adapter = adapterArtist
        listArtist!!.onItemClickListener = listenerArtist
        val btnArtist = view.findViewById<View>(R.id.button_artist) as Button
        val btnAlbum = view.findViewById<View>(R.id.button_album) as Button
        val btnMusic = view.findViewById<View>(R.id.button_music) as Button
        val btnNegative = view.findViewById<View>(R.id.button_negative) as Button
        btnArtist.setOnClickListener(clickListener)
        btnAlbum.setOnClickListener(clickListener)
        btnMusic.setOnClickListener(clickListener)
        btnNegative.setOnClickListener { close() }
        if (position != -1) listMusic!!.setSelection(position)
    }

    private val clickListener = View.OnClickListener { view ->
        val mediaManager = getMediaManager(
            requireContext()
        )
        if (view.id == R.id.button_artist) {
            listMusic!!.visibility = View.INVISIBLE
            listAlbum!!.visibility = View.INVISIBLE
            listArtist!!.visibility = View.VISIBLE
            selectButton = view.id
        } else if (view.id == R.id.button_album && selectButton != R.id.button_album) {
            adapterAlbum!!.setList(mediaManager.onReadAlbumList())
            listAlbum!!.adapter = adapterAlbum
            listMusic!!.visibility = View.INVISIBLE
            listAlbum!!.visibility = View.VISIBLE
            listArtist!!.visibility = View.INVISIBLE
            selectButton = view.id
        } else if (view.id == R.id.button_music && selectButton != R.id.button_music) {
            showMusicList(mediaManager.onReadMusicList())
            listMusic!!.visibility = View.VISIBLE
            listAlbum!!.visibility = View.INVISIBLE
            listArtist!!.visibility = View.INVISIBLE
            selectButton = view.id
        }
    }
    var listenerArtist = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val artist = adapterArtist!!.list[i].artist
        Toast.makeText(requireContext(), "artist:$artist", Toast.LENGTH_SHORT).show()
        val mediaManager = getMediaManager(
            requireContext()
        )
        val albumList: ArrayList<MediaInfo> = mediaManager.onReadArtistToAlbumList(artist)
        if (albumList[0].title != "戻る") albumList.add(0, MediaInfo("戻る"))
        adapterAlbum!!.setList(albumList as ArrayList<MediaInfo>)
        listAlbum!!.adapter = adapterAlbum
        listMusic!!.visibility = View.INVISIBLE
        listAlbum!!.visibility = View.VISIBLE
        listArtist!!.visibility = View.INVISIBLE
        selectButton = -1
    }
    var listenerAlbum = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val title = adapterAlbum!!.list!![i].title
        val album = adapterAlbum!!.list!![i].album
        if (title == "戻る") {
            listMusic!!.visibility = View.INVISIBLE
            listAlbum!!.visibility = View.INVISIBLE
            listArtist!!.visibility = View.VISIBLE
            return@OnItemClickListener
        }
        Toast.makeText(context, "album:$album", Toast.LENGTH_SHORT).show()
        val mediaManager = getMediaManager(
            requireContext()
        )
        val musicList: ArrayList<MediaInfo>
        musicList = if (adapterAlbum!!.list!![0].title == "戻る") {
            val artist = adapterAlbum!!.list!![i].artist
            mediaManager.onReadArtistAndAlbumToMusicList(artist, album)
        } else {
            mediaManager.onReadAlbumToMusicList(album)
        }
        if (musicList[0].title != "戻る") musicList.add(0, MediaInfo("戻る"))
        showMusicList(musicList)
        listMusic!!.visibility = View.VISIBLE
        listAlbum!!.visibility = View.INVISIBLE
        listArtist!!.visibility = View.INVISIBLE
    }
    var listenerMusic = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val musicData = adapterMusic!!.list[i]
        if (musicData.title == "戻る") {
            listMusic!!.visibility = View.INVISIBLE
            listAlbum!!.visibility = View.VISIBLE
            listArtist!!.visibility = View.INVISIBLE
            return@OnItemClickListener
        }
        Toast.makeText(requireContext(), "title:" + musicData.title, Toast.LENGTH_SHORT).show()
        listener!!.selectMusic(musicData)
    }

    fun showMusicList(list: ArrayList<MediaInfo>) {
        var position = -1
        if (mediaData != null && list.size > 0) {
            for (i in list.indices) {
                val (_, _, title, _, _, path) = list[i]
                if (mediaData!!.title == title && mediaData!!.path == path) {
                    position = i
                    break
                }
            }
        }
        adapterMusic!!.setList(list)
        listMusic!!.adapter = adapterMusic
        listMusic!!.onItemClickListener = listenerMusic
        if (position != -1) listMusic!!.setSelection(position)
    }

    fun selectItem(string: MediaInfo?) {
        mediaData = string
    }

    private var listener: OnDialogMusicListListener? = null
    fun setOnItemClickListener(l: OnDialogMusicListListener?) {
        listener = l
    }

    interface OnDialogMusicListListener {
        fun selectMusic(mediaInfo: MediaInfo?)
    }

    inner class ListAdapter(
        private val type: Int,
        private val context: Context,
        var list: ArrayList<MediaInfo>
    ) : BaseAdapter() {

        val maskImage = MaskImage(context)

        @JvmName("setList1")
        fun setList(list: ArrayList<MediaInfo>) {
            this.list = list
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(posi: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            if (null == convertView) {
                val layoutInflater_ =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = layoutInflater_.inflate(R.layout.list_dialog_item_music, null)
            }
            val textView = convertView.findViewById<View>(R.id.textView2) as TextView
            if (type == 1) {
                textView.text = list!![posi].album
            } else {
                textView.text = list!![posi].artist
            }
            val background = convertView.findViewById<View>(R.id.background) as LinearLayout

            val imageView = convertView.findViewById<View>(R.id.imageView4) as ImageView
            if (list[posi].title == "戻る") {
                imageView.setImageResource(net.mikemobile.media.R.drawable.ic_arrow_back)
                imageView.setColorFilter(Color.rgb(200, 200, 200))
            } else {

                val key = list[posi].album
                var artwork = getArtWork(key, list[posi].path)
                if (artwork != null && ThumbnailManager.getImage(key) == null) {
                    artwork = maskImage.maskImage(artwork, net.mikemobile.media.R.drawable.mask)
                    ThumbnailManager.setImage(key, artwork)
                }

                if (artwork == null) {
                    imageView.setImageResource(net.mikemobile.media.R.drawable.ic_music_note)
                    imageView.setColorFilter(Color.rgb(200, 200, 200))
                } else {
                    imageView.colorFilter = null
                    imageView.setImageBitmap(artwork)
                }
            }

            if (mediaData != null && mediaData!!.artist == list!![posi].artist
                || mediaData != null && mediaData!!.album == list!![posi].album
            ) {
                background.setBackgroundColor(Color.parseColor("#F6B448"))
            } else {
                background.setBackgroundColor(Color.WHITE)
            }
            return convertView
        }
    }

    inner class ListAdapterMusic(private val context: Context, var list: ArrayList<MediaInfo>) :
        BaseAdapter() {

        val maskImage = MaskImage(context)

        @JvmName("setList1")
        fun setList(list: ArrayList<MediaInfo>) {
            this.list = list
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(posi: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            if (null == convertView) {
                val layoutInflater_ =
                    context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = layoutInflater_.inflate(R.layout.list_dialog_item_music, null)
            }
            val textView = convertView.findViewById<View>(R.id.textView2) as TextView
            textView.text = list[posi].title

            val imageView = convertView.findViewById<View>(R.id.imageView4) as ImageView
            if (list[posi].title == "戻る") {
                imageView.setImageResource(net.mikemobile.media.R.drawable.ic_arrow_back)
                imageView.setColorFilter(Color.rgb(200, 200, 200))
            } else {

                val key = list[posi].path
                var artwork = getArtWork(key, list[posi].path)
                if (artwork != null && ThumbnailManager.getImage(key) == null) {
                    artwork = maskImage.maskImage(artwork, net.mikemobile.media.R.drawable.mask)
                    ThumbnailManager.setImage(key, artwork)
                }

                if (artwork == null) {
                    imageView.setImageResource(net.mikemobile.media.R.drawable.ic_music_note)
                    imageView.setColorFilter(Color.rgb(200, 200, 200))
                } else {
                    imageView.colorFilter = null
                    imageView.setImageBitmap(artwork)
                }
            }

            //imageView.setImageURI(list.get(posi).getData());
            val background = convertView.findViewById<View>(R.id.background) as LinearLayout
            if (mediaData != null && mediaData!!.title == list[posi].title && mediaData!!.path == list[posi].path) {
                background.setBackgroundColor(Color.parseColor("#F6B448"))
            } else {
                background.setBackgroundColor(Color.WHITE)
            }
            return convertView
        }
    }
}