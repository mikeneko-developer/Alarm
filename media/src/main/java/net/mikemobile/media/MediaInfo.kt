package net.mikemobile.media

import android.app.ActivityOptions
import android.content.*
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import java.util.HashMap

data class MediaInfo(
    var mediaType: Int,
    var mid: Long,
    var title: String,
    var artist: String,
    var album: String,
    var path: String,
    var time: String,
    var track: Int = 0,
    var data: Uri?,
    var album_id: String
) {


    constructor(item: HashMap<String, Any?>):this(
        mediaType = item[MediaInfo.KEY_MEDIA_TYPE] as Int,
        mid = (item[MediaInfo.KEY_MID] as Long?)!!,
        title = (item[MediaInfo.KEY_TITLE] as String?)!!,
        artist = (item[MediaInfo.KEY_ARTIST] as String?)!!,
        album = (item[MediaInfo.KEY_ALBUM] as String?)!!,
        path = (item[MediaInfo.KEY_PATH] as String?)!!,
        time = (item[MediaInfo.KEY_TIME] as String?)!!,
        track = item[MediaInfo.KEY_TRACK] as Int,
        data = (item[MediaInfo.KEY_DATA] as Uri?),
        album_id = (item[MediaInfo.KEY_ALBUM_ID] as String?)!!
    ) {}
    constructor(title: String):this(
        mediaType = 0,
        mid = 0,
        title = title,
        artist = "",
        album = "",
        path = "",
        time = "",
        track = 0,
        data = null,
        album_id = ""
    ) {}

    constructor(uri: Uri) : this(
        mediaType = 0,
        mid = 0,
        title = "",
        artist = "",
        album = "",
        path = "",
        time = "",
        track = 0,
        data = uri,
        album_id = ""
    ) {}


    companion object {
        val MEDIA_TYPE_MUSIC = 1
        val MEDIA_TYPE_PICT = 2

        val KEY_MEDIA_TYPE = "media_type"

        val KEY_TITLE = "title"
        val KEY_ARTIST = "artist"
        val KEY_ALBUM = "album"
        val KEY_PATH = "path"
        val KEY_TIME = "time"
        val KEY_IMAGE = "image"
        val KEY_TRACK = "track"

        val KEY_FAVORITE = "favorite"
        val KEY_EVALUATION = "evaluation"
        val KEY_COUNT = "count"

        val KEY_ID = "id"
        val KEY_ADD_DATE = "add_date"
        val KEY_UP_DATE = "up_date"

        val KEY_MID = "mid"
        val KEY_DATA = "data"

        val KEY_ALBUM_ID = "album_id"

        fun TrackParse(
            context: Context,
            cursor: Cursor
        ): MediaInfo {
            val item = HashMap<String, Any?>()
            item[KEY_MEDIA_TYPE] = MEDIA_TYPE_PICT

            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
            item[KEY_MID] = id
            item[KEY_PATH] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            item[KEY_TITLE] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            item[KEY_ALBUM] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            item[KEY_ARTIST] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            //item.put(KEY_IMAGE, cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Albums.ALBUM_ART)));
            item[KEY_TIME] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            item[KEY_DATA] =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            item[KEY_TRACK] =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
            item[KEY_ALBUM_ID] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID))

            //Log.i("LogList5","------------------------");
            //Log.i("LogList5","title" + ":" + MyHash.getString(item, KEY_TITLE));
            //Log.i("LogList5","album" + ":" + MyHash.getString(item, KEY_ALBUM));
            //Log.i("LogList5","artist" + ":" + MyHash.getString(item, KEY_ARTIST));
            //Log.i("LogList5","data" + ":h.getString(item, KEY_DATA));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            return MediaInfo(item)
        }


        fun VideoParse(
            context: Context,
            cursor: Cursor
        ): MediaInfo {
            val item = HashMap<String, Any?>()
            item[KEY_MEDIA_TYPE] = MEDIA_TYPE_PICT

            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
            item[KEY_MID] = id
            item[KEY_PATH] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
            item[KEY_TITLE] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
            item[KEY_ALBUM] =""
            item[KEY_ARTIST] =""
            item[KEY_TIME] =
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
            item[KEY_DATA] =
                ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            item[KEY_TRACK] = 0
            item[KEY_ALBUM_ID] = ""

            //Log.i("LogList5","------------------------");
            //Log.i("LogList5","title" + ":" + MyHash.getString(item, KEY_TITLE));
            //Log.i("LogList5","album" + ":" + MyHash.getString(item, KEY_ALBUM));
            //Log.i("LogList5","artist" + ":" + MyHash.getString(item, KEY_ARTIST));
            //Log.i("LogList5","data" + ":h.getString(item, KEY_DATA));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            return MediaInfo(item)
        }

        fun PictParse(
            context: Context,
            cursor: Cursor,
            idIndex: Int
        ): MediaInfo {
            val item = HashMap<String, Any?>()
            item[KEY_MEDIA_TYPE] = MEDIA_TYPE_MUSIC

            val imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor.getLong(idIndex)
            )

            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            item[KEY_MID] = id
            item[KEY_PATH] = ""
            item[KEY_TITLE] = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            item[KEY_ALBUM] = ""
            item[KEY_ARTIST] = ""
            item[KEY_TIME] = ""
            item[KEY_DATA] = imageUri
            item[KEY_TRACK] = 0
            item[KEY_ALBUM_ID] = ""

            //Log.i("LogList5","------------------------");
            //Log.i("LogList5","title" + ":" + MyHash.getString(item, KEY_TITLE));
            //Log.i("LogList5","album" + ":" + MyHash.getString(item, KEY_ALBUM));
            //Log.i("LogList5","artist" + ":" + MyHash.getString(item, KEY_ARTIST));
            //Log.i("LogList5","data" + ":h.getString(item, KEY_DATA));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            //Log.i("LogList5","path" + ":" + MyHash.getString(item, KEY_PATH));
            return MediaInfo(item)
        }
    }
}