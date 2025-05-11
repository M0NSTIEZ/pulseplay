package com.example.pulseplay.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
        }

        findViewById<CardView>(R.id.hazelcard).setOnClickListener {
            openEmail("hclimaco98@gmail.com")
        }

        findViewById<CardView>(R.id.jeffcard).setOnClickListener {
            openEmail("jpaulcv1@gmail.com")
        }

        findViewById<CardView>(R.id.butchcard).setOnClickListener {
            openEmail("butchbutas1112@gmail.com")
        }
    }

    private fun openEmail(emailAddress: String) {
        // Try with Gmail first
        try {
            val gmailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress")).apply {
                setPackage("com.google.android.gm")  // Gmail package name
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(gmailIntent)
            return
        } catch (e: Exception) {
            // Gmail failed, try with any email client
            try {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress"))
                startActivity(emailIntent)
                return
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Couldn't open email app. Please check if Gmail is installed.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}