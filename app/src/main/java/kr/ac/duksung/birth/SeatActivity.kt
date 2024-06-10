package kr.ac.duksung.birth

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
<<<<<<< HEAD
import android.view.View
import android.widget.AdapterView
=======
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
>>>>>>> 11e26fc188ff1d501a49322d89ea9023843af1b0
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.Spinner.HoseonAdapter
import kr.ac.duksung.birth.Spinner.HoseonModel
import kr.ac.duksung.birth.Spinner.SeatAdapter
import kr.ac.duksung.birth.Spinner.SeatModel
import kr.ac.duksung.birth.Spinner.StationAdapter
import kr.ac.duksung.birth.Spinner.StationModel
import kr.ac.duksung.birth.Spinner.TimeAdapter
import kr.ac.duksung.birth.Spinner.TimeModel
<<<<<<< HEAD
//import kr.ac.duksung.birth.alarm.SeatReceiver
=======
>>>>>>> 11e26fc188ff1d501a49322d89ea9023843af1b0
import kr.ac.duksung.birth.databinding.ActivitySeatBinding

class SeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBinding
    private lateinit var hoseonAdapter: HoseonAdapter
    private val listofHoseon = ArrayList<HoseonModel>()
    private lateinit var stationAdapter: StationAdapter
    private val listofStation = ArrayList<StationModel>()
    private lateinit var timeAdapter: TimeAdapter
    private val listofTime = ArrayList<TimeModel>()
    private lateinit var seatAdapter: SeatAdapter
    private val listofSeat = ArrayList<SeatModel>()
<<<<<<< HEAD

//    private val broadcastReceiver = SeatReceiver()
=======
>>>>>>> 11e26fc188ff1d501a49322d89ea9023843af1b0
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

//        val seatFilter = IntentFilter("kr.ac.duksung.birth.UPDATE_IMAGEVIEW_COLOR")
//        registerReceiver(SeatReceiver(), seatFilter)

        // 스피너 설정
        setupSpinnerHoseon()
        setupSpinnerStation()
        setupSpinnerTime()
        setupSpinnerSeat()
        setupSpinnerHandler()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // BroadcastReceiver 해제
//        unregisterReceiver(broadcastReceiver)
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
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }
}
