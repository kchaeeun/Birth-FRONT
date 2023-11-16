package kr.ac.duksung.birth

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kr.ac.duksung.birth.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val num = intent.getStringExtra("num")

//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("num", num.toString())
//        editor.apply()

        val info: List<String>? = num?.split("-")

        binding.textView2.text = info!![0] + " 임산부님 환영합니다."

        // 날짜 전처리
        val year = info[1].substring(0,4)
        val month = info[1].substring(4,6)
        val day = info[1].substring(6)
        val date = "{$year}년 {$month}월 {$day}일"
        binding.textView4.append(date)



        val intent = Intent(this, TestService::class.java)
        startService(intent)

        val messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val test = intent.getStringExtra("test")
                println(test)
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter("intent_action"))

    }
}



