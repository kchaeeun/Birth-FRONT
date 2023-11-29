package kr.ac.duksung.birth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.Retrofit.NumApiService
import kr.ac.duksung.birth.Retrofit.Serial
import kr.ac.duksung.birth.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val num = binding.editText.text.toString()  // edittext 값을 가져올 때는 text.toString()을 사용해준다.
            makeApiCall(num)
            Log.d("num",num)

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
                    if (serial != null) {
                        val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                        intent.putExtra("num", serialNumber)
                        intent.putExtra("apiCallResult", 1)
                        startActivity(intent)
                    }

                } else {
                    // 서버 응답이 실패한 경우의 처리
                    Log.e("Retrofit Error", "Error: " + response.message())

                    // Handle failure by sending "0" to BluetoothActivity
                    val intent = Intent(this@MainActivity, BluetoothActivity::class.java)
                    intent.putExtra("num", serialNumber)
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
}


