package com.example.pulseplay

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize views
        etFullName = findViewById(R.id.et_full_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        val btnRegister = findViewById<View>(R.id.btn_register)
        val tvLogin = findViewById<View>(R.id.tv_login)

        // Set up password visibility toggle
        setupPasswordToggle()

        // Register button click listener
        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(fullName, email, password)) {
                registerUser(email, password, fullName)
            }
        }

        // Login text click listener
        tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Edge-to-edge handling
        findViewById<View>(R.id.register)?.let { rootView ->
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun setupPasswordToggle() {
        val ivTogglePassword = findViewById<View>(R.id.iv_toggle_password)
        ivTogglePassword.setOnClickListener {
            if (etPassword.transformationMethod == null) {
                etPassword.transformationMethod = PasswordTransformationMethod()
                ivTogglePassword.setBackgroundResource(R.drawable.view)
            } else {
                etPassword.transformationMethod = null
                ivTogglePassword.setBackgroundResource(R.drawable.hide)
            }
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }
    }

    private fun validateInput(fullName: String, email: String, password: String): Boolean {
        return when {
            fullName.isEmpty() -> {
                etFullName.error = "Full name is required"
                false
            }
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
            password.length < 6 -> {
                etPassword.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Update user profile with display name
                    val user = auth.currentUser
                    val profileUpdates = user?.let {
                        FirebaseAuth.getInstance().currentUser?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()
                        )
                    }

                    profileUpdates?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Registration complete
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    // Handle registration errors
                    Toast.makeText(
                        baseContext, "Registration failed: ${
                            task.exception?.message ?: "Unknown error"
                        }", Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}