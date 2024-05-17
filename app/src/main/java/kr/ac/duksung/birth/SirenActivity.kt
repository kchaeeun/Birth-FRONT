package kr.ac.duksung.birth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.duksung.birth.DotProgressBar.DotProgressBar
import kr.ac.duksung.birth.databinding.ActivitySirenBinding
import kr.ac.duksung.birth.databinding.ActivitySplashBinding

class SirenActivity: BaseActivity() {

    private lateinit var binding: ActivitySirenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySirenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.imageButton2.visibility = View.GONE

        binding.include.imageButton.setOnClickListener {
            finish()
            Log.d("버튼 눌림 확인", "눌림")

        }
    }
}