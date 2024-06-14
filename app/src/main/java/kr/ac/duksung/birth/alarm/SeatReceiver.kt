package kr.ac.duksung.birth.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import kr.ac.duksung.birth.R
import kr.ac.duksung.birth.SeatActivity

class SeatReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "seat_change") {
            val changeColor = intent.getBooleanExtra("changeSeatColor", false)
            Log.d("changeColor", changeColor.toString())
            Log.d("context_check", context.toString())
            if (changeColor && context is SeatActivity) {
                // 해당 이미지뷰의 색상 변경 작업 수행
                val imageView5 = context.findViewById<ImageView>(R.id.imageView5)
                imageView5.setBackgroundResource(R.drawable.rotate_seat_red) // 색상 리소스 변경
            }
        }
    }
}
