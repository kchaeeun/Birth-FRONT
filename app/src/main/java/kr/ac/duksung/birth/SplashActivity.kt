package kr.ac.duksung.birth

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kr.ac.duksung.birth.DotProgressBar.DotProgressBar
import kr.ac.duksung.birth.databinding.ActivityMainBinding
import kr.ac.duksung.birth.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var dotProgressBar: DotProgressBar
    private val delayMills: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        animateDotsColorChange()

       Handler().postDelayed({
           startActivity(Intent(this, MainActivity::class.java))
           finish()
       }, delayMills)
    }

    private fun animateDotsColorChange() {
        dotProgressBar.setBackgroundResource(R.drawable.anim_color_change)
        (dotProgressBar.background as AnimationDrawable).start()
    }
}