package com.example.pulseplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.os.Looper

class WelcomeScreen1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen1)

        // Delay for 5 seconds (5000ms) then navigate to WelcomeScreen2
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeScreen2::class.java)
            startActivity(intent)
            finish() // Close this activity
        }, 5000) // 5000 milliseconds = 5 seconds
    }
}
