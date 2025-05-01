package com.example.pulseplay.notification

data class Notification(
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)