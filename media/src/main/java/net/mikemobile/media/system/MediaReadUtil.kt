package net.mikemobile.media.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.mikemobile.media.MediaInfo

/**
 *
 * 音楽リスト取得
 * var list = MediaReadManager().getMusicList(this)
 *
 *
 *
 *
 *
 */


class MediaReadUtil {

    companion object {
        const val TAG = "MediaReadManager"
        const val REQUEST_CODE = 1010101
    }
    fun checkPermission(activity: FragmentActivity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            // 許可されている時の処理
            return true
        } else {
            //許可されていない時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
            } else {
                //まだ許可を求める前の時、許可を求めるダイアログを表示します。
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            }
            return false
        }
    }

    fun getMovieList(context: Context): List<MediaInfo> {
        val tracks: ArrayList<MediaInfo> = ArrayList<MediaInfo>()

        val MUSIC_COLUMNS = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION
        )

        //val selection = MediaStore.Audio.Media.DATA + " = ?"
        val selection = null

        val selectionArgs = null
        val sortOrder = null

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            MUSIC_COLUMNS,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.let {
            var count = 1
            while (cursor.moveToNext()) {
                tracks.add(MediaInfo.VideoParse(context, cursor))
                count++
            }

            Log.i(TAG,"video tracks.size:" + tracks.size)
        }
        return tracks
    }

    fun getMusicList(context: Context): List<MediaInfo> {
        val tracks: ArrayList<MediaInfo> = ArrayList<MediaInfo>()

        val MUSIC_COLUMNS = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Albums.ALBUM_ID
        )

        //val selection = MediaStore.Audio.Media.DATA + " = ?"
        //val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            MUSIC_COLUMNS,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.let {
            var count = 1
            while (cursor.moveToNext()) {
                if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) < 3000) {
                    continue
                }
                tracks.add(MediaInfo.TrackParse(context, cursor))
                count++
            }

            Log.i(TAG,"tracks.size:" + tracks.size)
        }

        return tracks
    }

}