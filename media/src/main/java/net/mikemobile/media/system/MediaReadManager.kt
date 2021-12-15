package net.mikemobile.media.system

import android.provider.MediaStore
import android.content.Intent
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import net.mikemobile.media.MediaInfo
import net.mikemobile.media.MediaManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

internal class MediaReadManager(val context: Context): MediaManager {


    companion object {
        const val TAG = "MediaReadManager"
        @kotlin.jvm.JvmField
        var musicList: List<MediaInfo>? = null
        @kotlin.jvm.JvmField
        var albumList: ArrayList<MediaInfo>? = null
        @kotlin.jvm.JvmField
        var albumToMusicList: HashMap<String, List<MediaInfo>>? = null
        @kotlin.jvm.JvmField
        var artistList: ArrayList<MediaInfo>? = null
        @kotlin.jvm.JvmField
        var artistToAlbumList: HashMap<String, List<MediaInfo>>? = null
        @kotlin.jvm.JvmField
        var artistToAlbumToMusicList: HashMap<String, List<MediaInfo>>? = null

        @kotlin.jvm.JvmField
        var favoriteList: ArrayList<MediaInfo>? = null

        @kotlin.jvm.JvmField
        var videoList: List<MediaInfo>? = null

        fun clearData() {
            musicList = null
            albumList = null
            albumToMusicList = null
            artistList = null
            artistToAlbumList = null
            artistToAlbumToMusicList = null
            videoList = null
            favoriteList = null
        }

        const val KEY_TITLE = "title"
        const val KEY_ARTIST = "artist"
        const val KEY_ALBUM = "album"
        const val KEY_PATH = "path"
        const val KEY_TIME = "time"
        const val KEY_IMAGE = "image"
        const val KEY_TRACK = "track"
        const val KEY_FAVORITE = "favorite"
        const val KEY_EVALUATION = "evaluation"
        const val KEY_COUNT = "count"
        const val KEY_ID = "id"
        const val KEY_ADD_DATE = "add_date"
        const val KEY_UP_DATE = "up_date"
        const val KEY_MID = "mid"
        const val KEY_DATA = "data"
        const val KEY_ALBUM_ID = "album_id"
        private val COLUMNS = arrayOf(
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
    }


    ///////////////////////////////////////////////////////////////////////////////
    // 共通処理
    ///////////////////////////////////////////////////////////////////////////////

    fun sort(list : Array<Any>): ArrayList<String> {
        Arrays.sort(list)

        var newList = ArrayList<String>()
        for (i in list.indices) {
            if (!newList.contains(list[i])) {
                newList.add(list[i] as String)
            }
        }
        return newList
    }



    /**
     * 曲データを取得する
     */
    fun getItem(path: String): MediaInfo? {
        context.grantUriPermission(
            "mikeneko.tableclock.AddAlarm",
            Uri.parse("content://media/external/audio/media"),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val selection = MediaStore.Audio.Media.DATA + " = ?"
        val selectionArgs = arrayOf(
            path
        )
        val tracks = ArrayList<MediaInfo>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            COLUMNS,
            selection,
            selectionArgs,
            null
        )
        var count = 1
        while (cursor!!.moveToNext()) {
            if (cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) < 3000) {
                continue
            }
            tracks.add(MediaInfo.TrackParse(context, cursor))
            count++
        }
        cursor.close()
        context.revokeUriPermission(
            Uri.parse("content://media/external/audio/media"),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        return if (tracks.size == 0) {
            null
        } else tracks[0]
    }


    ///////////////////////////////////////////////////////////////////////////////
    // 音楽データ
    ///////////////////////////////////////////////////////////////////////////////
    fun AddFavoriteList(info: MediaInfo) {
        favoriteList!!.add(info)
    }

    fun RemoveFavoriteList(info: MediaInfo) {
        for(i in 0 until favoriteList!!.size) {
            val musicData = favoriteList!![i]
            if (musicData.mid == info.mid) {
                favoriteList!!.removeAt(i)
                return
            }
        }
    }

    fun CheckFavoriteList(info: MediaInfo): Boolean {
        for(i in 0 until favoriteList!!.size) {
            val musicData = favoriteList!![i]
            if (musicData.mid == info!!.mid) {
                return true
            }
        }

        return false
    }

    fun FavoriteListToJsonString(): String {
        var favoriteListString = ""
        try {

            var jsonArray = JSONArray()

            for(musicData in favoriteList!!) {
                var jsonObject = JSONObject()
                jsonObject.put("mediaType", musicData.mediaType)
                jsonObject.put("mid", musicData.mid)
                jsonObject.put("title", musicData.title)
                jsonObject.put("artist", musicData.artist)
                jsonObject.put("album", musicData.album)
                jsonArray.put(jsonObject)
            }

            favoriteListString = jsonArray.toString()
        } catch(e: Exception) {
            Log.e(TAG, "FavoriteListToJsonString " + e.toString())
        }


        Log.i(TAG, "FavoriteListToJsonString : " + favoriteListString)

        return favoriteListString
    }

    fun ReadFavoriteListData(favoriteListText: String){
        Log.i(TAG, "ReadFavoriteListData : " + favoriteListText)

        if (favoriteList == null) {
            favoriteList = ArrayList<MediaInfo>()
        }

        if (favoriteListText != "") {
            var jsonArray = JSONArray(favoriteListText)

            for (i in 0 until jsonArray.length()) {
                val jsonData = jsonArray.getJSONObject(i)

                var mediaType = jsonData.getInt("mediaType")
                var mid = jsonData.getLong("mid")
                var title = jsonData.getString("title")
                var artist = jsonData.getString("artist")
                var album = jsonData.getString("album")

                Log.i(TAG, "ReadFavoriteListData > mid:" + mid)
                for(musicData in musicList!!) {
                    Log.i(TAG, "ReadFavoriteListData > musicData.mid:" + musicData.mid + " " + (musicData.mid == mid))
                    if (musicData.mid == mid) {
                        favoriteList!!.add(musicData)
                        break
                    }
                }
            }
        }
    }

    fun ReadMusicData() {
        Log.i(TAG,"ReadMusicData()")
        if (musicList == null) {
            if (artistList == null) {
                artistList = ArrayList<MediaInfo>()
            }
            if (artistToAlbumList == null) {
                artistToAlbumList = HashMap<String, List<MediaInfo>>()
            }
            if (artistToAlbumToMusicList == null) {
                artistToAlbumToMusicList = HashMap<String, List<MediaInfo>>()
            }

            if (albumList == null) {
                albumList = ArrayList<MediaInfo>()
            }
            if (albumToMusicList == null) {
                albumToMusicList = HashMap<String, List<MediaInfo>>()
            }

            musicList = MediaReadUtil().getMusicList(context)

            var musicNameList = ArrayList<String>()
            var albumNameList = ArrayList<String>()
            var artistNameList = ArrayList<String>()

            for (i in 0 until musicList!!.size) {
                val musicItem = musicList!!.get(i)
                musicNameList.add(musicItem.title)
            }

            musicNameList = sort(musicNameList.toTypedArray() as Array<Any>)

            var new_musicList = ArrayList<MediaInfo>()
            for (i in 0 until musicNameList.size) {
                var name = musicNameList.get(i)

                //Log.i(TAG,"music : " + name)
                for(item in musicList!!) {
                    if (name.equals(item.title)) {
                        new_musicList.add(item)
                        break
                    }
                }
            }
            musicList = new_musicList

            for (i in 0 until musicList!!.size) {
                val musicItem = musicList!!.get(i)

                if (!checkAlbum(albumList!!, musicItem)) {
                    albumList!!.add(musicItem)
                    albumNameList.add(musicItem.album)
                }

                if (true) {
                    var list = ArrayList<MediaInfo>()
                    if (albumToMusicList!!.containsKey(musicItem.album)) {
                        list = albumToMusicList!![musicItem.album] as ArrayList<MediaInfo>
                    }
                    list.add(musicItem)
                    albumToMusicList!!.put(musicItem.album, sortAlbum(list))
                }

                if (!checkArtist(artistList!!, musicItem)) {
                    artistList!!.add(musicItem)
                    artistNameList.add(musicItem.artist)
                }

                if (true) {
                    var list = ArrayList<MediaInfo>()
                    if (artistToAlbumList!!.containsKey(musicItem.artist)) {
                        list = artistToAlbumList!!.get(musicItem.artist) as ArrayList<MediaInfo>
                    }


                    if (!checkAlbum(list, musicItem)) {
                        list.add(musicItem)
                    }

                    artistToAlbumList!!.put(musicItem.artist, list)
                }

                if (true) {
                    val keyName = musicItem.artist + "-" + musicItem.album

                    var list = ArrayList<MediaInfo>()
                    if (artistToAlbumToMusicList!!.containsKey(keyName)) {
                        list = artistToAlbumToMusicList!!.get(keyName) as ArrayList<MediaInfo>
                    }

                    list.add(musicItem)

                    artistToAlbumToMusicList!!.put(keyName, sortAlbum(list))
                }
            }

            // アルバム名をソートして並び替える
            albumNameList = sort(albumNameList.toTypedArray() as Array<Any>)
            val new_albumList = ArrayList<MediaInfo>()
            for(name in albumNameList) {
                for(data in albumList!!) {
                    if (name == data.album) {
                        new_albumList.add(data)
                        break
                    }
                }

            }
            albumList = new_albumList

            // アーティスト名をソートして並び替える
            artistNameList = sort(artistNameList.toTypedArray() as Array<Any>)
            val new_artistList = ArrayList<MediaInfo>()
            for(name in artistNameList) {
                for(data in artistList!!) {
                    if (name == data.artist) {
                        new_artistList.add(data)
                        break
                    }
                }
            }
            artistList = new_artistList

            /**
             * ログ出力
             */
            for(artist in artistList!!) {
                val albumList = artistToAlbumList!![artist.artist]

                Log.i(TAG,"artist:"+artist.artist+ " " + albumList!!.size + " --------------")
                var c = 1
                for(album in albumList!!) {
                    //Log.i(TAG,""+c+" album:"+album)
                    c++

                    var m = 1
                    val musicList = albumToMusicList!![album.album]
                    for(musicData in musicList!!) {
                        //Log.i(TAG,""+m+" title:"+musicData.title)
                        m++
                    }
                }
            }

        }

        Log.i(TAG,"ReadMusicData() END")
    }
    fun checkAlbum(list: ArrayList<MediaInfo>, info: MediaInfo): Boolean {
        for(item in list) {
            if(item.album == info.album) {
                return true
            }
        }
        return false
    }
    fun checkArtist(list: ArrayList<MediaInfo>, info: MediaInfo): Boolean {
        for(item in list) {
            if(item.artist == info.artist) {
                return true
            }
        }
        return false
    }

    fun sortAlbum(list: ArrayList<MediaInfo>): ArrayList<MediaInfo> {
        var newList = ArrayList<MediaInfo>()
        for (info in list) {
            var add = true
            for(j in newList.indices) {
                if (info.path == newList[j].path) {
                    add = false
                    break
                }
            }

            if (add) {
                for (j in newList.indices) {

                    if (info.track < newList[j].track) {
                        newList.add(info)
                        add = false
                        break
                    }
                }
            }
            if(add)newList.add(info)
        }
        return newList
    }

    ///////////////////////////////////////////////////////////////////////////////
    // 動画データ
    ///////////////////////////////////////////////////////////////////////////////

    fun ReadMovieData() {
        videoList = MediaReadUtil().getMovieList(context)
    }


    ///////////////////////////////////////////////////////////////////////////////
    // Interface
    ///////////////////////////////////////////////////////////////////////////////
    override fun onReadMusicList(): ArrayList<MediaInfo> {
        ReadMusicData()
        return (musicList as ArrayList<MediaInfo>?)!!
    }

    override fun onReadArtistList(): ArrayList<MediaInfo> {
        ReadMusicData()
        return (artistList as ArrayList<MediaInfo>?)!!
    }

    override fun onReadAlbumList(): ArrayList<MediaInfo> {
        ReadMusicData()
        return (albumList as ArrayList<MediaInfo>?)!!
    }

    override fun onReadArtistToAlbumList(artist: String): ArrayList<MediaInfo> {
        ReadMusicData()
        return (artistToAlbumList!![artist] as ArrayList<MediaInfo>?)!!
    }

    override fun onReadArtistAndAlbumToMusicList(
        artist: String,
        album: String
    ): ArrayList<MediaInfo> {
        ReadMusicData()


        return (artistToAlbumToMusicList!![artist + "-" + album] as ArrayList<MediaInfo>?)!!
    }

    override fun onReadAlbumToMusicList(album: String): ArrayList<MediaInfo> {
        ReadMusicData()
        return (albumToMusicList!![album] as ArrayList<MediaInfo>?)!!
    }

    override fun onReadMovieList(): ArrayList<MediaInfo> {
        ReadMovieData()
        return (videoList as ArrayList<MediaInfo>?)!!
    }

    override fun onClearData() {
        clearData()
    }

    override fun onReadMusicData(path: String): MediaInfo? {
        var item = getItem(path)
        return item
    }
}