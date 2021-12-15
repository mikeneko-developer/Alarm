package net.mikemobile.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore


class ThumbnailManager {

    companion object {
        const val TAG = "ThumbnailManager"

        var list = HashMap<String, Bitmap>()

        fun reset() {
            for(key in list.keys) {
                val image = list[key]
                image?.recycle()
            }
            list.clear()
        }

        fun setImage(key: String, image: Bitmap) {
            list[key] = image
        }

        fun getImage(key: String) : Bitmap? {
            if (!list.containsKey(key)) {
                return null
            }

            return list[key]
        }

        fun getThumbnail(filePath: String): Bitmap? {
            var thumbnail = ThumbnailUtils.createVideoThumbnail(
                filePath,
                MediaStore.Images.Thumbnails.MICRO_KIND
            )

            return thumbnail
        }

        fun getArtWork(filePath: String): Bitmap? {
            var bm: Bitmap? = null
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            val data = mmr.embeddedPicture

            // 画像が無ければnullになる
            if (null != data) {
                bm = BitmapFactory.decodeByteArray(data, 0, data.size)
            }
            return bm
        }

        private fun resizeRead(filePath: String, outputWidth: Int, outputHeight: Int): Bitmap? {
            val ops = BitmapFactory.Options()
            ops.inPreferredConfig = Bitmap.Config.RGB_565 /* Bitmap.Config.ARGB_8888; */
            ops.inJustDecodeBounds = true

            BitmapFactory.decodeFile(filePath, ops)
            if (ops.outWidth < ops.outHeight) {
                val scaleWidth: Int = calculateInSampleSize(ops.outWidth, outputHeight)
                val scaleHeight: Int = calculateInSampleSize(ops.outHeight, outputWidth)
                ops.inSampleSize = Math.min(scaleWidth, scaleHeight)
            } else {
                val scaleWidth: Int = calculateInSampleSize(ops.outWidth, outputWidth)
                val scaleHeight: Int = calculateInSampleSize(ops.outHeight, outputHeight)
                ops.inSampleSize = Math.min(scaleWidth, scaleHeight)
            }
            ops.inJustDecodeBounds = false
            var destBmp = BitmapFactory.decodeFile(filePath, ops)
            if (destBmp == null) {
                return destBmp
            }
            if (Bitmap.Config.RGB_565 != destBmp.config) {
                destBmp.recycle()
                destBmp = null
            }
            return destBmp
        }

        private fun calculateInSampleSize(src: Int, dest: Int): Int {
            var i = 2
            while (i < 256) {
                if (src / i <= dest) {
                    return i
                }
                i *= 2
            }
            return 256
        }
    }


}