package com.example.pulseplay.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: TextInputEditText
    private lateinit var newPasswordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var currentPasswordLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var updateButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser ?: run {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        currentPasswordEditText = findViewById(R.id.currentPassword)
        newPasswordEditText = findViewById(R.id.newPassword)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout)
        newPasswordLayout = findViewById(R.id.newPasswordLayout)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)
        updateButton = findViewById(R.id.updateButton)

        // Set up back button
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Reset errors
        currentPasswordLayout.error = null
        newPasswordLayout.error = null
        confirmPasswordLayout.error = null

        // Validate inputs
        if (currentPassword.isEmpty()) {
            currentPasswordLayout.error = "Current password is required"
            return
        }

        if (newPassword.isEmpty()) {
            newPasswordLayout.error = "New password is required"
            return
        }

        if (newPassword.length < 6) {
            newPasswordLayout.error = "Password must be at least 6 characters"
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.error = "Please confirm your new password"
            return
        }

        if (newPassword != confirmPassword) {
            confirmPasswordLayout.error = "Passwords don't match"
            return
        }

        // Show progress
        updateButton.isEnabled = false
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE

        // Re-authenticate user
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    // Update password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            updateButton.isEnabled = true

                            if (updateTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to update password: ${updateTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    updateButton.isEnabled = true
                    currentPasswordLayout.error = "Incorrect current password"
                    Toast.makeText(
                        this,
                        "Authentication failed: ${reAuthTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}