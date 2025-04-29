package com.example.pulseplay.models

data class PredictionRequest(
    val condition: String,
    val health_data: Map<String, Any>
)