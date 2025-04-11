package com.example.pulseplay

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity<FirebaseAuthInvalidUserException> : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize views
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerText = findViewById<TextView>(R.id.tv_register)
        val forgotPassword = findViewById<TextView>(R.id.tv_forgot_password)

        // Set click listeners
        registerText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        loginButton.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        forgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter valid email to reset password"
                return@setOnClickListener
            }

            sendPasswordResetEmail(email)
        }

        // Edge-to-edge handling
        findViewById<View>(R.id.main)?.let { rootView ->
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                etEmail.error = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Valid email is required"
                false
            }
            password.isEmpty() -> {
                etPassword.error = "Password is required"
                false
            }
            else -> true
        }
    }



    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToHomePage()
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthException -> {
                            when (exception.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Invalid email format"
                                "ERROR_USER_NOT_FOUND" -> "Account not found"
                                "ERROR_WRONG_PASSWORD" -> "Invalid password"
                                "ERROR_USER_DISABLED" -> "Account disabled"
                                "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts - try again later"
                                "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password login not enabled"
                                else -> "Authentication failed: ${exception.message}"
                            }
                        }
                        else -> "Login failed: ${task.exception?.message ?: "Unknown error"}"
                    }
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Password reset email sent to $email",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, HomePage::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToHomePage()
        }
    }
}