package com.example.pulseplay

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Button

class WelcomeScreen2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen2)

        val buttonNext = findViewById<Button>(R.id.button_next)
        buttonNext.setOnClickListener {
            val intent = Intent(this, WelcomeScreen3::class.java)
            startActivity(intent)
            finish() // Finish the current screen to prevent going back
        }
    }
}

