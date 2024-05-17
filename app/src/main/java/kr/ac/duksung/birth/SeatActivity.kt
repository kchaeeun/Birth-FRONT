package kr.ac.duksung.birth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.Spinner.HoseonAdapter
import kr.ac.duksung.birth.Spinner.HoseonModel
import kr.ac.duksung.birth.databinding.ActivitySeatBinding

class SeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatBinding
    private lateinit var hoseonAdapter: HoseonAdapter
    private val listofHoseon = ArrayList<HoseonModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회전 설정
        binding.seatLayout2.rotation = 180f

        binding.include.imageButton.setOnClickListener {
            finish()
            Log.d("버튼 눌림 확인", "눌림")

        }

        binding.include.imageButton2.setOnClickListener {
            Log.d("버튼 눌림 확인", "눌림")
            val intent = Intent(this, SirenActivity::class.java)
            startActivity(intent)
        }

        // 스피너 설정
        setupSpinnerHoseon()
//        setupSpinnerStation()
//        setupSpinnerTime()
//        setupSpinnerSeat()
    }

    private fun setupSpinnerHoseon() {
        val hoseons = resources.getStringArray(R.array.hoseon)

        for (i in hoseons.indices) {
            val hoseon = HoseonModel(hoseons[i])
            listofHoseon.add(hoseon)
        }
        hoseonAdapter = HoseonAdapter(this, R.layout.item_spinner, listofHoseon)
        binding.spinner1.adapter = hoseonAdapter
    }

//    private fun setupSpinnerStation() {
//        val stations = resources.getStringArray(R.array.station)
//        val adapter = HoAdapter(this, R.layout.item_spinner, stations)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinner2.adapter = adapter
//    }
//
//    private fun setupSpinnerTime() {
//        val times = resources.getStringArray(R.array.time)
//        val adapter = ArrayAdapter(this, R.layout.item_spinner, times)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinner3.adapter = adapter
//    }
//
//    private fun setupSpinnerSeat() {
//        val seats = resources.getStringArray(R.array.seat)
//        val adapter = ArrayAdapter(this, R.layout.item_spinner, seats)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinner4.adapter = adapter
//    }
}
