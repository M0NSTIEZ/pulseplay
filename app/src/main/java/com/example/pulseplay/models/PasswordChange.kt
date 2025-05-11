// models/PasswordChange.kt
package com.example.pulseplay.models

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String
)

data class PasswordChangeResponse(
    val success: Boolean,
    val message: String
)