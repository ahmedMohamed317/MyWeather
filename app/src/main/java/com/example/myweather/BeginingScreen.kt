package com.example.myweather

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView

class BeginingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val DELAY_TIME_MS: Long = 3500
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begining_screen)
        val animationView = findViewById<LottieAnimationView>(R.id.splash_lotti)
        animationView.setAnimation(R.raw.different_weather)
        animationView.playAnimation()

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity to prevent going back to it
        }, DELAY_TIME_MS)
    }
}