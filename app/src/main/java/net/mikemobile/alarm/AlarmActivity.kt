package net.mikemobile.alarm

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("AlarmActivity","onCreate()")
        setContentView(R.layout.activity_alarm)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i("AlarmActivity","onNewIntent()")
    }

    override fun onUserLeaveHint() {
        /** HOMEボタンが押されたときの処理  */
    }
}