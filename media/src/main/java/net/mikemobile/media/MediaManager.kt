package net.mikemobile.media

import android.content.Context

interface MediaManager {

    fun onReadMusicList(): ArrayList<MediaInfo>
    fun onReadArtistList(): ArrayList<MediaInfo>
    fun onReadAlbumList(): ArrayList<MediaInfo>
    fun onReadArtistToAlbumList(artist: String): ArrayList<MediaInfo>
    fun onReadArtistAndAlbumToMusicList(artist: String, album: String): ArrayList<MediaInfo>
    fun onReadAlbumToMusicList(album: String): ArrayList<MediaInfo>

    fun onReadMovieList(): ArrayList<MediaInfo>

    fun onClearData()

    fun onReadMusicData(path: String): MediaInfo?
}