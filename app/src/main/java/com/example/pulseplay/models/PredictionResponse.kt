package com.example.pulseplay.models

data class PredictionResponse(
    val condition: String,
    val prediction: String,
    val probability: Double,
    val status: String,
    val threshold: Double
)