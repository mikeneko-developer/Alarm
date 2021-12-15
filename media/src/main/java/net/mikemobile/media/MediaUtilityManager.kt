package net.mikemobile.media

import android.content.Context
import net.mikemobile.media.system.MediaReadManager

class MediaUtilityManager {

    companion object {
        fun getMediaManager(context: Context): MediaManager {
            return MediaReadManager(context)
        }
    }
}