package com.example.pulseplay.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView // Import CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R

class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_us)

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get references to the CardViews and set click listeners
        findViewById<CardView>(R.id.hazelcard).setOnClickListener {
            // Find the email TextView within this specific CardView
            val emailTextView = it.findViewById<TextView>(R.id.tv_hazel_email)
            val emailAddress = emailTextView.text.toString()
            sendEmail(emailAddress)
        }

        findViewById<CardView>(R.id.jeffcard).setOnClickListener {
            val emailTextView = it.findViewById<TextView>(R.id.tv_jefferson_email)
            val emailAddress = emailTextView.text.toString()
            sendEmail(emailAddress)
        }

        findViewById<CardView>(R.id.butchcard).setOnClickListener {
            val emailTextView = it.findViewById<TextView>(R.id.tv_butch_email)
            val emailAddress = emailTextView.text.toString()
            sendEmail(emailAddress)
        }
    }

    // Helper function to create and launch an email intent
    private fun sendEmail(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No email application found.", Toast.LENGTH_SHORT).show()
        }
    }
}