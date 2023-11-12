package kr.ac.duksung.birth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.ac.duksung.birth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val num = binding.editText.text.toString()  // edittext 값을 가져올 때는 text.toString()을 사용해준다.
            Log.d("num",num)
            val intent = Intent(this, CheckActivity::class.java)
            intent.putExtra("num", num)
            startActivity(intent)
        }
    }
}