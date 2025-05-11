// settings/ChangePasswordActivity.kt
package com.example.pulseplay.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.api.UserApiService
import com.example.pulseplay.auth.TokenManager
import com.example.pulseplay.databinding.ActivityChangePasswordBinding
import com.example.pulseplay.models.PasswordChangeRequest
import com.example.pulseplay.models.PasswordChangeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var apiService: UserApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = UserApiService.create()
        setupViews()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener { finish() }

        binding.updateButton.setOnClickListener {
            val currentPass = binding.currentPassword.text.toString()
            val newPass = binding.newPassword.text.toString()
            val confirmPass = binding.confirmPassword.text.toString()

            if (validateInputs(currentPass, newPass, confirmPass)) {
                changePassword(currentPass, newPass)
            }
        }
    }

    private fun validateInputs(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ): Boolean {
        binding.apply {
            currentPasswordLayout.error = null
            newPasswordLayout.error = null
            confirmPasswordLayout.error = null

            if (currentPass.isEmpty()) {
                currentPasswordLayout.error = "Current password required"
                return false
            }

            if (newPass.isEmpty()) {
                newPasswordLayout.error = "New password required"
                return false
            }

            if (newPass.length < 6) {
                newPasswordLayout.error = "Minimum 6 characters required"
                return false
            }

            if (confirmPass != newPass) {
                confirmPasswordLayout.error = "Passwords don't match"
                return false
            }

            return true
        }
    }

    private fun changePassword(currentPass: String, newPass: String) {
        val token = TokenManager.getToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showLoading(true)

        val request = PasswordChangeRequest(currentPass, newPass)

        apiService.changePassword("Bearer $token", request).enqueue(object : Callback<PasswordChangeResponse> {
            override fun onResponse(
                call: Call<PasswordChangeResponse>,
                response: Response<PasswordChangeResponse>
            ) {
                showLoading(false)
                handleResponse(response)
            }

            override fun onFailure(call: Call<PasswordChangeResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleResponse(response: Response<PasswordChangeResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.success) {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            when (response.code()) {
                401 -> Toast.makeText(this, "Invalid current password", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.updateButton.isEnabled = !show
    }
}