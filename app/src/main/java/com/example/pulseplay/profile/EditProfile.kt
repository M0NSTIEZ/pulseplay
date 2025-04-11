package com.example.pulseplay.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile) // Ensure this file exists

        // Find Views
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        val saveButton = findViewById<Button>(R.id.btn_save)

        // Handle Back Button Click
        backButton?.setOnClickListener {
            finish()
        }

        // Handle Save Button Click (optional)
        saveButton?.setOnClickListener {
            // Add save functionality
            finish()
        }
        val uploadBtn = findViewById<ImageView>(R.id.btn_upload_pic)

        uploadBtn.setOnClickListener {
            // TODO: Launch image picker here
        }


        // Apply Window Insets (Fix Null Reference)
        val rootView = findViewById<View>(R.id.age_profile) // Replace with actual root ID
        rootView?.setOnApplyWindowInsetsListener { v, insets ->
            insets
        }
    }
}
