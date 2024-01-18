package kr.ac.duksung.birth.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NoActionReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("NoActionReceiver", "아니오 버튼이 클릭되었습니다.")
        val notificationManager = NotificationManagerCompat.from(p0!!)
        notificationManager.cancel(CheckAlarmReceiver.NOTIFICATION_ID)
    }
}