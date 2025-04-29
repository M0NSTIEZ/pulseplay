package com.example.pulseplay.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pulseplay.api.UserApiService
import com.example.pulseplay.auth.TokenManager
import com.example.pulseplay.models.LoginRequest

class LoginViewModel : ViewModel() {
    private val userApiService = UserApiService.create()

    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = userApiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    TokenManager.saveToken(loginResponse.token)
                    Result.success(Unit)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}