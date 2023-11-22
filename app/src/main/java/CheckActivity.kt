//package kr.ac.duksung.birth
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kr.ac.duksung.birth.Retrofit.NumApiService
//import kr.ac.duksung.birth.Retrofit.Serial
//import kr.ac.duksung.birth.databinding.ActivityCheckBinding
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.IOException
//import java.time.LocalDateTime
//
//class CheckActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityCheckBinding
//
//    companion object {
//        private const val BASE_URL = "http://10.0.0.2:8080/"
//        private var retrofit: Retrofit? = null
//
//        fun getRetrofitInstance(): Retrofit {
//            if (retrofit == null) {
//                retrofit = Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//            }
//            return retrofit!!
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityCheckBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val num = intent.getStringExtra("num")
//
//        if (num != null) {
//            makeApiCall(num)
//        }
//
////        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
////        val editor = sharedPreferences.edit()
////        editor.putString("num", num.toString())
////        editor.apply()
//
//        val info: List<String>? = num?.split("-")
//
//
//
////        val retrofitClient = RetrofitClient.getInstance()
////        serialRetrofitInterface = RetrofitClient.getSerialRetrofitInterface()
////
////        call = serialRetrofitInterface.serial
////        call.clone().enqueue(object : Callback<SerialDTO> {
////            override fun onResponse(call: Call<SerialDTO>, response: Response<SerialDTO>) {
////                if (response.isSuccessful) {
////                    binding.textView2.text = response.body()?.name + " 임산부님 환영합니다."
////                    binding.textView4.append(response.body()?.expire_date.toString())
////                }
////            }
//
////            override fun onFailure(call: Call<SerialDTO>, t: Throwable) {
////                Log.e("retrofit 연동", "실패")
////                t.printStackTrace()
////            }
////        })
//
//        // 날짜 전처리
////        val year = info[1].substring(0,4)
////        val month = info[1].substring(4,6)
////        val day = info[1].substring(6)
////        val date = "{$year}년 {$month}월 {$day}일"
////        binding.textView4.append(date)
//
//
//
//        val intent = Intent(this, TestService::class.java)
//        startService(intent)
//
//        val messageReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val test = intent.getStringExtra("test")
//                println(test)
//            }
//        }
//
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(messageReceiver, IntentFilter("intent_action"))
//
//    }
//
//    private fun makeApiCall(serialNumber: String) {
//        val apiService = CheckActivity.getRetrofitInstance().create(NumApiService::class.java)
//        val call = apiService.getBySerial(serialNumber)
//
//        call.enqueue(object : Callback<Serial> {
//            override fun onResponse(call: Call<Serial>, response: Response<Serial>) {
//                if (response.isSuccessful) {
//                    val serial: Serial? = response.body()
//                    // Handle the data
//                    serial?.let {
//                        // Process the list
//                        val name: String = it.name
//                        val expireDate: LocalDateTime = it.expireDate
//
//                        runOnUiThread {
//                            binding.textView2.text = "$name 임산부님 환영합니다."
//                            binding.textView4.append(expireDate.toString())
//                        }
//                    }
//                } else {
//                    // Handle error
//                    Log.e("Retrofit Error", "Error: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Serial>, t: Throwable) {
//                // Handle failure
//                Log.e("Retrofit Error", "Failure: ${t.message}")
//            }
//        })
//    }
//}