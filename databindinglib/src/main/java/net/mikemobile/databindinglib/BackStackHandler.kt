package net.mikemobile.databindinglib

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import java.util.*


/**
 * pause状態ではない場合に格納されたイベントもしくは引数に渡された情報を返すためのリスナー
 */
interface BackStackHandlerListener{
    fun onProcess(msg: Message, fragment: Fragment?)
}

/**
 * fragmentを渡す場合に一時的にMessageクラスのObject変数に格納するためのクラス
 */
data class DataFragment(var fragment: Fragment){}

class BackStackHandler: PauseHandler() {

    companion object {
        const val TAG = "BackStackHandler"

        const val ADD_FRAGMENT = 1
        const val REPLACE_FRAGMENT = 2

        const val ADD_FRAGMENT_IN_BACKSTACK = 10
        const val REPLACE_FRAGMENT_IN_BACKSTACK = 11

        const val REMOVE_FRAGMENT = 20

        const val SHOW_DIALOG = 30
        const val DISMISS_DIALOG = 31

        //シングルトン
        private var instance : BackStackHandler? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: BackStackHandler().also { instance = it }
        }
    }

    var listener:BackStackHandlerListener? = null
    fun setOnBackStackHandlerListener(l: BackStackHandlerListener){
        listener = l
    }

    fun safetyRun(what: Int, bundle: Bundle){
        val message = obtainMessage(what,bundle)
        message.obj = null
        sendMessage(message)
    }

    fun safetyRun(what: Int, bundle: Bundle, _fragment: Fragment){
        val message = obtainMessage(what,bundle)
        message.obj = DataFragment(_fragment)
        sendMessage(message)
    }

    override fun storeMessage(message: Message): Boolean {
        return true
    }

    override fun processMessage(message: Message) {
        if(listener != null) {

            if(message.obj != null){
                listener!!.onProcess(message,(message.obj as DataFragment).fragment)
            }else {
                listener!!.onProcess(message,null)
            }
        }
    }

}

abstract class PauseHandler: Handler() {
    companion object {
        const val TAG = "PauseHandler"
    }

    /**
     */
    private val messageQueueBuffer = Vector<Message>()

    /**
     */
    private var paused: Boolean = false

    /**
     */
    fun resume() {
        paused = false
        while (messageQueueBuffer.size > 0) {
            val msg = messageQueueBuffer.elementAt(0)
            messageQueueBuffer.removeElementAt(0)
            sendMessage(msg)
        }
    }

    /**
     */
    open fun pause() {
        paused = true
    }

    /**
     */
    protected abstract fun storeMessage(message: Message): Boolean

    /**
     */
    protected abstract fun processMessage(message: Message)

    /** {@inheritDoc}  */
    override fun handleMessage(msg: Message) {
        if (paused) {
            if (storeMessage(msg)) {
                val msgCopy = Message()
                msgCopy.copyFrom(msg)
                messageQueueBuffer.add(msgCopy)
            }
        } else {
            processMessage(msg)
        }
    }

    fun obtainMessage(what: Int, bundle :Bundle?): Message {
        val message = obtainMessage(what)
        if (bundle != null) {
            message.data = bundle
        }

        return message
    }
}