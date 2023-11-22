package ir.pr4e.black_apple.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.pr4e.black_apple.databinding.ActivityLnaguageBinding

class LanguageActivity : AppCompatActivity() {
    lateinit var binding: ActivityLnaguageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLnaguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}