package ir.pr4e.black_apple.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ir.pr4e.black_apple.databinding.ActivitySplashMainBinding


class SplashMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashMainBinding

    private val shPrCodeName = "isFirstRunning"
    private val shPr: SharedPreferences by lazy { getSharedPreferences("First_Start", MODE_PRIVATE) }
    private val shPrEdit: SharedPreferences.Editor by lazy { shPr.edit() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val version = "ver ${packageManager.getPackageInfo(packageName, 0).versionName}"
        binding.lblVersionSplash.text = version


        startActivity(Intent(this, MainActivity::class.java))
        finish()

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (shPr.getBoolean(shPrCodeName, false)) {
//                shPrEdit.putBoolean(shPrCodeName, true)
//                shPrEdit.commit()
//                startActivity(Intent(this, LanguageActivity::class.java))
//            } else
//                startActivity(Intent(this, MainActivity::class.java))
//        finish()
//        }, 3000)
    }
}