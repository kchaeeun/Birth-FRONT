package kr.ac.duksung.birth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat


class TestService : Service() {
    private val mHandler = Handler(Looper.getMainLooper())
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        startSendingMessages()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "MyBackgroundService Channel",
                NotificationManager.IMPORTANCE_DEFAULT // 여기를 IMPORTANCE_DEFAULT로 수정
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }


    private fun buildNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MyBackgroundService")
            .setContentText("Running in the background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }

    private fun startSendingMessages() {
        mHandler.postDelayed(object : Runnable {
            override fun run() {

                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val savedInfo = sharedPreferences.getString("num", null)
                // 메시지 전송 코드
                //sendMessage(savedInfo); // 메시지를 보낼 메서드 호출

                // 1초 후에 다시 호출
                mHandler.postDelayed(this, MESSAGE_SEND_INTERVAL.toLong())
            }
        }, MESSAGE_SEND_INTERVAL.toLong())
    }

    companion object {
        private const val CHANNEL_ID = "MyBackgroundServiceChannel"
        private const val NOTIFICATION_ID = 123
        private const val MESSAGE_SEND_INTERVAL = 1000 // 1초
    }
}
