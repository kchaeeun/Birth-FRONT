package kr.ac.duksung.birth.alarm

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.ac.duksung.birth.R
import java.util.*

class CheckAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // 채널 등록
            setupNotificationChannel(context)
            // 알림 등록
            showCustomNotification(context)
        }
    }

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_ID = 1
        const val REQUEST_NOTIFICATION_PERMISSION = 123

        fun setupNotificationChannel(context: Context) {
            Log.d("Alarm setup", "setupNotificationChannel 호출됨")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Birth"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCustomNotification(context: Context) {

        Log.d("Alarm setup", "showCustomNotification 호출됨")
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_layout)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.ic_alram)
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // ResourcesCompat.getFont에서 반환된 폰트를 적용할 수 있도록 확장 함수 생성
    private fun Typeface?.toFontResponse(): Int {
        return when (this) {
            null -> 0 // 폰트를 불러올 수 없는 경우 0을 반환
            else -> 1 // 원하는 폰트 로딩 성공
        }
    }
}

class AlarmManagerUtil {
    companion object {
        fun setRepeatingAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, CheckAlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }

            val calendar: Calendar = Calendar.getInstance().apply {
//                timeInMillis = System.currentTimeMillis() + 5000
//                // 여기서 시간을 설정합니다
//                set(Calendar.HOUR_OF_DAY, 4)    // 시
//                set(Calendar.MINUTE, 32)    // 분
//                set(Calendar.SECOND, 0)
//                Log.d("Alarm setup", timeInMillis.toString())
                timeInMillis = System.currentTimeMillis() + 1000 // 알림 뜨는 시간 1초 뒤로 설정
                Log.d("Alarm setup", timeInMillis.toString())
            }

            // 24시간 마다 반복
            val interval = 24 * 60 * 60 * 1000L

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                interval,
                alarmIntent
            )

            Log.d("Alarm setup", "PendingIntent 생성됨: $alarmIntent")
        }
    }
}