package kr.ac.duksung.birth.alarm

import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class NoActionReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("NoActionReceiver", "아니오 버튼이 클릭되었습니다.")

        // 데이터를 브로드캐스트로 전달
        val broadcastIntent = Intent("kr.ac.duksung.birth.DATA_ACTION")
        broadcastIntent.putExtra("no-action", 2)
        p0?.sendBroadcast(broadcastIntent)

        val notificationManager = NotificationManagerCompat.from(p0!!)
        notificationManager.cancel(CheckAlarmReceiver.NOTIFICATION_ID)
    }
}