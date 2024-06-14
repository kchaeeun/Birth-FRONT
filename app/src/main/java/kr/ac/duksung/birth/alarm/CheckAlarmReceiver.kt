package kr.ac.duksung.birth.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.ac.duksung.birth.R
import java.util.*

class CheckAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

//            val receivedValue = intent?.getIntExtra("seat-value",-1);
//            Log.d("alarmReceive", receivedValue.toString())
//
//            if (receivedValue == 0) {
            Log.d("alarmReceive", "success")
            // 채널 등록
            setupNotificationChannel(context)
            // 알림 등록
            showCustomNotification(context)
            // 알림 서비스 시작
//            }
        }
    }

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_ID = 1
        const val REQUEST_NOTIFICATION_PERMISSION = 123

        fun setupNotificationChannel(context: Context) {
            Log.d("Alarm setup", "setupNotificationChannel 호출됨")
            val name = "Birth"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCustomNotification(context: Context) {
        Log.d("Alarm setup", "showCustomNotification 호출됨")

        val yesIntent = Intent(context, YesActionReceiver::class.java)
        val yesPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noIntent = Intent(context, NoActionReceiver::class.java)
        val noPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            noIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_layout)

        notificationLayout.setOnClickPendingIntent(R.id.layout_yes, yesPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.layout_no, noPendingIntent)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.ic_alarm_preg)
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        // 권한이 있거나 S 이하 버전인 경우 알림 표시
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}
    class AlarmManagerUtil {
    companion object {
        fun setRepeatingAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, CheckAlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }

//            val calendar: Calendar = Calendar.getInstance().apply {
////                timeInMillis = System.currentTimeMillis() + 5000
////                // 여기서 시간을 설정합니다
////                set(Calendar.HOUR_OF_DAY, 4)    // 시
////                set(Calendar.MINUTE, 32)    // 분
////                set(Calendar.SECOND, 0)
////                Log.d("Alarm setup", timeInMillis.toString())
//                timeInMillis = System.currentTimeMillis() + 10000 // 알림 뜨는 시간 1초 뒤로 설정
//                Log.d("Alarm setup", timeInMillis.toString())
//            }

            // 24시간 마다 반복
//            val interval = 24 * 60 * 60 * 1000L
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                interval,
//                alarmIntent
//            )

            val now = System.currentTimeMillis()
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, now, alarmIntent)

            Log.d("Alarm setup", "PendingIntent 생성됨: $alarmIntent")
        }
    }
}