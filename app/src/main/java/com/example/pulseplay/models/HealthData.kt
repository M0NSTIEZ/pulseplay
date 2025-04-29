package com.example.pulseplay.models

// models/HealthData.kt
data class HealthData(
    val _id: String,
    val username: String,
    val stepCount: List<HealthDataEntry>,
    val distanceWalkingRunning: List<HealthDataEntry>,
    val flightsClimbed: List<HealthDataEntry>,
    val activeEnergyBurned: List<HealthDataEntry>,
    val basalEnergyBurned: List<HealthDataEntry>,
    val appleExerciseTime: List<HealthDataEntry>,
    val heartRate: List<HealthDataEntry>,
    val restingHeartRate: List<HealthDataEntry>,
    val walkingHeartRateAverage: List<HealthDataEntry>,
    val heartRateVariabilitySDNN: List<HealthDataEntry>,
    val bloodPressureSystolic: List<HealthDataEntry>,
    val bloodPressureDiastolic: List<HealthDataEntry>,
    val bodyTemperature: List<HealthDataEntry>,
    val respiratoryRate: List<HealthDataEntry>,
    val oxygenSaturation: List<HealthDataEntry>,
    val height: List<HealthDataEntry>,
    val bodyMass: List<HealthDataEntry>,
    val bodyMassIndex: List<HealthDataEntry>,
    val bodyFatPercentage: List<HealthDataEntry>,
    val leanBodyMass: List<HealthDataEntry>,
    val dietaryEnergyConsumed: List<HealthDataEntry>,
    val dietaryCarbohydrates: List<HealthDataEntry>,
    val dietaryProtein: List<HealthDataEntry>,
    val dietaryFatTotal: List<HealthDataEntry>,
    val dietaryWater: List<HealthDataEntry>,
    val sleepAnalysis: List<HealthDataEntry>,
    val menstrualFlow: List<HealthDataEntry>,
    val electrocardiogram: List<HealthDataEntry>,
    val workoutDuration: List<HealthDataEntry>,
    val workoutCalories: List<HealthDataEntry>,
    val workoutDistance: List<HealthDataEntry>,
    val workoutDetailedData: List<WorkoutDetailedData>,
    val ecgDetailedData: List<EcgDetailedData>,
    val createdAt: String,
    val __v: Int
)

data class HealthDataEntry(
    val date: String,
    val value: Double,
    val unit: String
)

data class WorkoutDetailedData(
    val type: String,
    val startTime: String,
    val endTime: String,
    val duration: Double,
    val totalActiveEnergy: Double,
    val totalRestingEnergy: Double,
    val totalDistance: Double,
    val averageHeartRate: Double,
    val effortScore: Double
)

data class EcgDetailedData(
    val startDate: String,
    val endDate: String,
    val classification: String,
    val averageHeartRate: Double,
    val symptomsStatus: String,
    val duration: Double,
    val samplingFrequency: Double,
    val device: String,
    val metadata: String
)