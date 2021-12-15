package net.mikemobile.alarm.log

import android.content.Context
import android.widget.Toast
import net.mikemobile.alarm.util.CustomDateTime
import java.io.*

class LogUtil {
    companion object {
        private const val DEBUG = true
        private const val TAG = "LogUtil"

        fun i(tag: String, message: String) {
            android.util.Log.i(TAG + " " + tag, message)
        }
        fun e(tag: String, message: String) {
            android.util.Log.e(TAG + " " + tag, message)
        }
        fun d(tag: String, message: String) {
            android.util.Log.d(TAG + " " + tag, message)
        }
        fun w(tag: String, message: String) {
            android.util.Log.w(TAG + " " + tag, message)
        }
        fun i(context: Context,tag: String, message: String) {
            android.util.Log.i(TAG + " " + tag, message)
            if (!DEBUG) return
            saveFile(context,  CustomDateTime.getDateTimeText() + " " + tag + " / i:" + message)
        }
        fun e(context: Context,tag: String, message: String) {
            android.util.Log.e(TAG + " " + tag, message)
            if (!DEBUG) return
            saveFile(context,  CustomDateTime.getDateTimeText() + " " + tag + " / e:" + message)
        }
        fun d(context: Context,tag: String, message: String) {
            android.util.Log.d(TAG + " " + tag, message)
            if (!DEBUG) return
            saveFile(context,  CustomDateTime.getDateTimeText() + " " + tag + " / d:" + message)
        }
        fun w(context: Context,tag: String, message: String) {
            android.util.Log.w(TAG + " " + tag, message)
            if (!DEBUG) return
            saveFile(context, CustomDateTime.getDateTimeText() + " " + tag + " / w:" + message)
        }
        fun toast(context: Context,message: String) {
            if (!DEBUG) return
            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
        }

        // ファイルを保存
        fun saveFile(context: Context, str: String) {
            var readText = "\n" + str

            var date = CustomDateTime.getDateText()



            try {
                context.openFileOutput("Log_" + date + ".txt", Context.MODE_APPEND).use {
                    it.write(readText.toByteArray())
                }
            }catch(e: Exception) {

            }
        }

        fun clearFile(context: Context) {
            var readText = ""

            var date = CustomDateTime.getDateText()
            try {
                context.openFileOutput("Log_" + date + ".txt", Context.MODE_PRIVATE).use {
                    it.write(readText.toByteArray())
                }
            }catch(e: Exception) {

            }
        }

        // ファイルを読み出し
        fun readFile(context: Context): String {
            var text: String = ""

            var date = CustomDateTime.getDateText()
            val file = File(context.getFilesDir(), "Log_" + date + ".txt")
            if(file.exists()){
                text = file.bufferedReader().use(BufferedReader::readText)
            } else {
                android.util.Log.e(TAG, "readFile() ファイルがありません")
            }

            return text
        }
    }
}