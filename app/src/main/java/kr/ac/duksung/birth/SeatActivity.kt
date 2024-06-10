package kr.ac.duksung.birth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.Spinner.HoseonAdapter
import kr.ac.duksung.birth.Spinner.HoseonModel
import kr.ac.duksung.birth.Spinner.SeatAdapter
import kr.ac.duksung.birth.Spinner.SeatModel
import kr.ac.duksung.birth.Spinner.StationAdapter
import kr.ac.duksung.birth.Spinner.StationModel
import kr.ac.duksung.birth.Spinner.TimeAdapter
import kr.ac.duksung.birth.Spinner.TimeModel
import kr.ac.duksung.birth.alarm.SeatReceiver
import kr.ac.duksung.birth.databinding.ActivitySeatBinding


class SeatActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivitySeatBinding
    private lateinit var hoseonAdapter: HoseonAdapter
    private val listofHoseon = ArrayList<HoseonModel>()
    private lateinit var stationAdapter: StationAdapter
    private val listofStation = ArrayList<StationModel>()
    private lateinit var timeAdapter: TimeAdapter
    private val listofTime = ArrayList<TimeModel>()
    private lateinit var seatAdapter: SeatAdapter
    private val listofSeat = ArrayList<SeatModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회전 설정
        binding.seatLayout2.rotation = 180f

        binding.include.constraint11.setOnClickListener {
            finish()
            Log.d("버튼 눌림 확인", "눌림")
        }

        binding.include.constraintLayout10.setOnClickListener {
            Log.d("버튼 눌림 확인", "눌림")
            val intent = Intent(this, SirenActivity::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("seat-change", Context.MODE_PRIVATE)
        Log.d("shared", sharedPreferences.toString())

        retrieveStoredValues()

        // 스피너 설정
        setupSpinnerHoseon()
        setupSpinnerStation()
        setupSpinnerTime()
        setupSpinnerSeat()
        setupSpinnerHandler()
        // SharedPreferences 변경 리스너 설정
    }

    private fun retrieveStoredValues() {
        // Example: Retrieve a stored string value with key "storedSeat" and set it to a TextView
        // Example: Retrieve a stored boolean value with key "changeSeatColor"
        val changeSeatColor = sharedPreferences.getBoolean("changeSeatColor", false)
        Log.d("아 제발 요 ㅠㅠㅠㅠ", changeSeatColor.toString())

        if (changeSeatColor) {
            // Change the background of imageView5 to red
            binding.imageView5.setBackgroundResource(R.drawable.ic_red_seat)
        } else {
            binding.imageView5.setBackgroundResource(R.drawable.rotate_pink_seat)
        }
    }
//
//    private val seatReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val changeColor = intent.getBooleanExtra("changeSeatColor", false)
//            Log.d("changeColor", changeColor.toString())
//            if (changeColor) {
//                // 해당 이미지뷰의 색상 변경 작업 수행
//               binding.imageView5.setBackgroundResource(R.drawable.rotate_seat_red)// 색상 리소스 변경
//            }
//        }
//    }

    private fun setupSpinnerHoseon() {
        val hoseons = resources.getStringArray(R.array.hoseon)

        for (i in hoseons.indices) {
            val hoseon = HoseonModel(hoseons[i])
            listofHoseon.add(hoseon)
        }
        hoseonAdapter = HoseonAdapter(this, R.layout.item_spinner, listofHoseon)
        binding.spinner1.adapter = hoseonAdapter
    }

    private fun setupSpinnerStation() {
        val stations = resources.getStringArray(R.array.station)

        for (i in stations.indices) {
            val station = StationModel(stations[i])
            listofStation.add(station)
        }
        stationAdapter = StationAdapter(this, R.layout.item_spinner2, listofStation)
        binding.spinner2.adapter = stationAdapter
    }

    private fun setupSpinnerTime() {
        val times = resources.getStringArray(R.array.time)

        for (i in times.indices) {
            val time = TimeModel(times[i])
            listofTime.add(time)
        }
        timeAdapter = TimeAdapter(this, R.layout.item_spinner3, listofTime)
        binding.spinner3.adapter = timeAdapter
    }

    private fun setupSpinnerSeat() {
        val seats = resources.getStringArray(R.array.seat)
        for (i in seats.indices) {
            val seat = SeatModel(seats[i])
            listofSeat.add(seat)
        }
        seatAdapter = SeatAdapter(this, R.layout.item_spinner4, listofSeat)
        binding.spinner4.adapter = seatAdapter
    }

    private fun setupSpinnerHandler() {
        binding.spinner4.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val seat = binding.spinner4.getItemAtPosition(p2) as SeatModel
                if (seat.seat != "열차칸") {
                    binding.tv1.text = seat.seat[0] + "-2"
                    binding.tv2.text = seat.seat[0] + "-2"
                    binding.tv3.text = seat.seat[0] + "-3"
                    binding.tv4.text = seat.seat[0] + "-3"
                }

                if (p2 == 0) { // Check if the first item is selected
                    // Change imageView5 background to red drawable
                    binding.imageView5.setBackgroundResource(R.drawable.ic_red_seat)
                } else {
                    // Change imageView5 background to pink drawable for other selections
                    binding.imageView5.setBackgroundResource(R.drawable.rotate_pink_seat)
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 선택되지 않았을 때의 동작
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(seatReceiver)
//    }

}
