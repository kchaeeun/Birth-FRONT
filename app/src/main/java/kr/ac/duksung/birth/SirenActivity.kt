package kr.ac.duksung.birth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.DotProgressBar.DotProgressBar
import kr.ac.duksung.birth.databinding.ActivitySirenBinding
class SirenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySirenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySirenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.constraintLayout10.visibility = View.GONE

        binding.include.constraint11.setOnClickListener {
            finish()
            Log.d("버튼 눌림 확인", "눌림")

        }
    }
//    private fun onRadioButtonClicked(View view) {
//        Boolean isSelected = binding.
//    }
}