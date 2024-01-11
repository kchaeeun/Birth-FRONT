package kr.ac.duksung.birth.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NoActionReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("NoActionReceiver", "아니오 버튼이 클릭되었습니다.")
    }
}