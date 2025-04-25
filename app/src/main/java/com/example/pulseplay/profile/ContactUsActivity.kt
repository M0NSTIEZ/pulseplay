package com.example.pulseplay.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R

class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_us)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

            // You can add click listeners to make emails clickable
            // Example for Hazel Climaco's email:
            // hclimaco_email.setOnClickListener {
            //     val intent = Intent(Intent.ACTION_SENDTO).apply {
            //         data = Uri.parse("mailto:hclimaco98@gmail.com")
            //     }
            //     startActivity(intent)
            // }
            // You would need to add IDs to your email TextViews in the XML

        }
    }
}