package com.example.pulseplay.models

import com.google.gson.annotations.SerializedName

// models/Notification.kt
data class Notification(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("username") val username: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("isRead") val isRead: Boolean = false,
    @SerializedName("type") val type: String = "HEALTH_ALERT",
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("__v") val version: Int? = null
)