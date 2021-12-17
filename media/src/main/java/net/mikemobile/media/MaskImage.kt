package net.mikemobile.media

import android.R.attr
import android.content.Context
import android.graphics.*
import android.util.Log
import java.io.File
import android.graphics.PorterDuff

import android.graphics.PorterDuffXfermode


import android.graphics.Bitmap


class MaskImage(val context: Context) {
    companion object {
        const val TAG = "MaskImage"
    }
    /**
     *
     * @param path
     * @return
     */
    fun getImage(path: String): Bitmap? {
        val file = openFile(path)
        return if (!checkFile(file)) {
            null
        } else BitmapFactory.decodeFile(file.path)
    }
    /**
     * ファイルを開く
     * @param file_path ファイルパスを指定
     * @return 開いたFileクラスを返します
     */
    fun openFile(file_path: String?): File {
        return File(file_path)
    }

    fun checkFile(file: File): Boolean {
        return if (file.exists()) {
            Log.i(TAG, "ファイルが存在します")
            true
        } else {
            Log.e(TAG, "ファイルが存在しません")
            false
        }
    }

    fun maskImage(original_path: String, mask: Int): Bitmap? {
        val original = getImage(original_path)
        if (original == null) {
            return null
        }
        val maskImage = BitmapFactory.decodeResource(context.resources, mask)

        return maskImage(original, maskImage)
    }

    fun maskImage(original_path: String, mask_path: String): Bitmap? {
        val original = getImage(original_path)
        val maskImage = getImage(mask_path)
        if (original == null || maskImage == null) {
            return null
        }

        return maskImage(original, maskImage)
    }
    fun maskImage(original_path: String, maskImage: Bitmap): Bitmap? {
        val original = getImage(original_path)
        if (original == null) {
            return null
        }
        return maskImage(original, maskImage)
    }
    fun maskImage(original: Bitmap, mask: Int): Bitmap {
        val maskImage = BitmapFactory.decodeResource(context.resources, mask)
        return maskImage(original, maskImage)
    }

    fun maskImage(original: Bitmap, maskImage: Bitmap): Bitmap {
        val width = original.width
        val height = original.height

        val mask2 = Bitmap.createScaledBitmap(
            maskImage,
            width,
            height,
            false
        )

        val maskedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val tempCanvas = Canvas(maskedBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        tempCanvas.drawBitmap(original, 0f, 0f, null)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        tempCanvas.drawBitmap(mask2, 0f, 0f, paint)
        paint.xfermode = null

        return maskedBitmap

    }
}