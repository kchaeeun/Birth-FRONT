package kr.ac.duksung.birth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.Retrofit.NumApiService
import kr.ac.duksung.birth.Retrofit.Serial
import kr.ac.duksung.birth.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


//    private fun Context.customToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_layout))
//
//        val textView = layout.findViewById<TextView>(R.id.textViewToastMessage)
//        textView.text = message
//
//        val toast = Toast(this)
//        toast.duration = duration
//        toast.view = layout
//        toast.show()
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//        Log.d("text", message)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        CheckAlarmReceiver.setupNotificationChannel(this)
//        AlarmManagerUtil.setRepeatingAlarm(this)
//
//        // 알림 권한
//        checkNotificationPermission()
//        setupToolbarButton()

        binding.include.constraint11.setOnClickListener {
            Log.d("버튼 눌림 확인", "눌림")
            val intent = Intent(this, SirenActivity::class.java)
            startActivity(intent)
        }
        binding.include.imageButton.visibility = View.INVISIBLE

        binding.button.setOnClickListener {
//            val intent = Intent(this, BluetoothActivity::class.java)
//            startActivity(intent)
            val num = binding.editText.text.toString()  // edittext 값을 가져올 때는 text.toString()을 사용해준다.
            makeApiCall(num)
            Log.d("num",num)
//            Toast.makeText(this, "rne", Toast.LENGTH_SHORT).show()
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
        val apiService = BluetoothActivity.getRetrofitInstance()?.create(
            NumApiService::class.java
        )
        val call = apiService?.getBySerial(serialNumber)
        call?.enqueue(object : Callback<Serial?> {
            override fun onResponse(call: Call<Serial?>, response: Response<Serial?>) {
                if (response.isSuccessful) {
//                    Toast.makeText(this@MainActivity, "임산부 인증이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    val serial = response.body()
                    val expireDate = serial?.expireDate
                    if (serial != null && expireDate != null) {
                        if (!isBeforeToday(expireDate)) {
                            val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                            intent.putExtra("num", serialNumber)
                            intent.putExtra("apiCallResult", 1)
                            Log.d("apiPermission","checkPlease")
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
//                showCustomToast("임산부 인증이 완료되었습니다.")
//                Toast.makeText(
//                    this@MainActivity,
//                    "임산부 인증에 실패하였습니다.",
//                    Toast.LENGTH_LONG
//                ).show()
                Log.e("Retrofit Error", "Failure: " + t.message)

                // Handle failure by sending "0" to BluetoothActivity
                val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                intent.putExtra("num", serialNumber)
                intent.putExtra("apiCallResult", "0") // Failure
                startActivity(intent)
            }
        })
    }

//    private fun checkNotificationPermission() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // 권한 없을시 요청
//            ActivityCompat.requestPermissions(
//                this as Activity,
//                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
//                CheckAlarmReceiver.REQUEST_NOTIFICATION_PERMISSION
//            )
//        }
//    }
}


