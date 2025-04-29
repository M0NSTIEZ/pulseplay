package com.example.pulseplay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.pulseplay.api.UserApiService
import com.example.pulseplay.auth.TokenManager
import com.example.pulseplay.models.LoginRequest
import com.example.pulseplay.repository.UserRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        username = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerText = findViewById<TextView>(R.id.tv_register)
        val forgotPassword = findViewById<TextView>(R.id.tv_forgot_password)

        // Set click listeners
        registerText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        loginButton.setOnClickListener {
            val username = username.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                loginUser(username, password)
            }
        }

        forgotPassword.setOnClickListener {
            // Implement your password reset API call if needed
            Toast.makeText(this, "Password reset functionality to be implemented", Toast.LENGTH_SHORT).show()
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

    private fun validateInput(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                this.username.error = "Username is required"
                false
            }
            password.isEmpty() -> {
                etPassword.error = "Password is required"
                false
            }
            else -> true
        }
    }

    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val apiService = UserApiService.create()
                val response = apiService.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save the token
                        TokenManager.saveToken(loginResponse.token)

                        // Fetch and store user data
                        UserRepository.fetchUserData()

                        // Navigate to home page
                        navigateToHomePage()
                    } ?: run {
                        Toast.makeText(
                            this@MainActivity,
                            "Login failed: Empty response",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid credentials"
                        404 -> "User not found"
                        500 -> "Server error"
                        else -> "Login failed: ${response.message()}"
                    }
                    Toast.makeText(
                        this@MainActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Network error: ${e.message}",
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

    private fun navigateToLoginPage() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is already logged in (has token)
        val token = TokenManager.getToken()
        if (token != null) {
            lifecycleScope.launch {
                try {
                    // Try to fetch user data
                    UserRepository.fetchUserData()

                    // If we have valid user data, go to home
                    if (UserRepository.getUser() != null) {
                        navigateToHomePage()
                    } else {
                        // If no user data despite having token, clear token and stay on login
                        TokenManager.clearToken()
                        // Don't navigate - just stay on login screen
                    }
                } catch (e: Exception) {
                    // If fetch fails, clear token and stay on login
                    TokenManager.clearToken()
                    // Don't navigate - just stay on login screen
                }
            }
        }
        // If no token, just stay on login screen (default behavior)
    }
}