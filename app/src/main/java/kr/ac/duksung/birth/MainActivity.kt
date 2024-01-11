package kr.ac.duksung.birth

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kr.ac.duksung.birth.Retrofit.NumApiService
import kr.ac.duksung.birth.Retrofit.Serial
import kr.ac.duksung.birth.alarm.AlarmManagerUtil
import kr.ac.duksung.birth.alarm.CheckAlarmReceiver
import kr.ac.duksung.birth.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CheckAlarmReceiver.setupNotificationChannel(this)
        AlarmManagerUtil.setRepeatingAlarm(this)

        // 알림 권한
        checkNotificationPermission()

        binding.button.setOnClickListener {
            val num = binding.editText.text.toString()  // edittext 값을 가져올 때는 text.toString()을 사용해준다.
            makeApiCall(num)
            Log.d("num",num)

        }
    }

    private fun isBeforeToday(expireDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            // 현재 날짜를 가져옴
            val calendarToday = Calendar.getInstance()
            val today = calendarToday.time

            // expireDate를 Date 형태로 변환
            val expireDateDate = dateFormat.parse(expireDate)

            // 현재 날짜와 비교
            expireDateDate.before(today)
        } catch (e: Exception) {
            e.printStackTrace()
            false // 날짜 변환 실패 시 false 반환
        }
    }

    private fun makeApiCall(serialNumber: String) {
        val apiService = BluetoothActivity.getRetrofitInstance().create(
            NumApiService::class.java
        )
        val call = apiService.getBySerial(serialNumber)
        call.enqueue(object : Callback<Serial?> {
            override fun onResponse(call: Call<Serial?>, response: Response<Serial?>) {
                if (response.isSuccessful) {
                    val serial = response.body()
                    val expireDate = serial?.expireDate
                    if (serial != null && expireDate != null) {
                        if (!isBeforeToday(expireDate)) {
                            val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                            intent.putExtra("num", serialNumber)
                            intent.putExtra("apiCallResult", 1)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                            intent.putExtra("num", serialNumber)
                            intent.putExtra("apiCallResult", 0)
                            startActivity(intent)
                        }
                    }

                } else {
                    // 서버 응답이 실패한 경우의 처리
                    Log.e("Retrofit Error", "Error: " + response.message())

                    // Handle failure by sending "0" to BluetoothActivity
                    val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
//                    intent.putExtra("num", serialNumber)
                    intent.putExtra("apiCallResult", 0) // Failure
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Serial?>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "임산부 인증에 실패하였습니다.",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Retrofit Error", "Failure: " + t.message)

                // Handle failure by sending "0" to BluetoothActivity
                val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                intent.putExtra("num", serialNumber)
                intent.putExtra("apiCallResult", "0") // Failure
                startActivity(intent)
            }
        })
    }

    private fun checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // 권한 없을시 요청
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                CheckAlarmReceiver.REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }
}


