package com.example.pulseplay.models

data class UserDetails(
    val _id: String,
    val username: String,
    val gender: String?,
    val height: Float?,
    val weight: Float?,
    val age: Int?,
    val has_cvd: Boolean?,
    val updatedAt: String,
    val __v: Int
)