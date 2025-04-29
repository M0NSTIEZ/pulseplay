package com.example.pulseplay.repository

import android.content.Intent
import android.util.Log
import com.example.pulseplay.MainActivity
import com.example.pulseplay.PulsePlayApplication
import com.example.pulseplay.api.PredictionApiService
import com.example.pulseplay.api.UserApiService
import com.example.pulseplay.auth.TokenManager
import com.example.pulseplay.models.HealthData
import com.example.pulseplay.models.HealthDataEntry
import com.example.pulseplay.models.PredictionRequest
import com.example.pulseplay.models.User
import com.example.pulseplay.models.UserDetails
import java.util.Calendar

object UserRepository {
    private var user: User? = null
    private var userDetails: UserDetails? = null
    private var healthData: HealthData? = null

    fun getUser(): User? = user
    fun getUserDetails(): UserDetails? = userDetails
    fun getHealthData(): HealthData? = healthData

    fun setUser(newUser: User) {
        user = newUser
    }

    fun setUserDetails(newDetails: UserDetails) {
        userDetails = newDetails
    }

    fun setHealthData(newHealthData: HealthData) {
        healthData = newHealthData
    }

    suspend fun fetchUserData() {
        try {
            val token = TokenManager.getToken() ?: return
            val apiService = UserApiService.create()

            // First fetch the basic user info
            val userResponse = apiService.getUser("Bearer $token")
            if (userResponse.isSuccessful) {
                userResponse.body()?.let { user ->
                    this.user = user

                    // Then fetch user details using the username
                    val detailsResponse = apiService.getUserDetails(user.username, "Bearer $token")
                    if (detailsResponse.isSuccessful) {
                        detailsResponse.body()?.let { details ->
                            this.userDetails = details

                            // Finally fetch health data
                            val healthResponse = apiService.getHealthData(user.username)
                            if (healthResponse.isSuccessful) {
                                healthResponse.body()?.let { health ->
                                    this.healthData = health
                                }
                            }
                        }
                    } else {
                        // If getting user details fails with 401, token is expired
                        if (detailsResponse.code() == 401) {
                            handleTokenExpiration()
                            return
                        } else {
                            Log.e("UserRepository", "Error fetching user details: ${detailsResponse.code()}")
                        }
                    }
                }
            } else {
                // If getting user fails with 401, token is expired
                if (userResponse.code() == 401) {
                    handleTokenExpiration()
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user data", e)
        }
    }

    private fun handleTokenExpiration() {
        // Clear the expired token
        TokenManager.clearToken()

        // Redirect to login activity
        val context = PulsePlayApplication.appContext
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun clearUserData() {
        user = null
        userDetails = null
        healthData = null
    }


    private var dehydrationPrediction: Double? = null
    private var heatStrokePrediction: Double? = null
    private var stressPrediction: Double? = null

    fun getDehydrationPrediction(): Double? = dehydrationPrediction
    fun getHeatStrokePrediction(): Double? = heatStrokePrediction
    fun getStressPrediction(): Double? = stressPrediction

    suspend fun fetchPredictions() {
        try {
            val apiService = PredictionApiService.create()

            // 1. Dehydration prediction
            val dehydrationRequest = createDehydrationRequest()
            println("Dehydration Request: $dehydrationRequest") // Log the request for debugging

            val dehydrationResponse = apiService.predictCondition(dehydrationRequest)
            dehydrationPrediction = dehydrationResponse.body()?.probability

            // 2. Heat stroke prediction
            val heatStrokeRequest = createHeatStrokeRequest()
            println("Heat Stroke Request: $heatStrokeRequest") // Log the request for debugging

            val heatStrokeResponse = apiService.predictCondition(heatStrokeRequest)
            heatStrokePrediction = heatStrokeResponse.body()?.probability

            // 3. Stress prediction
            val stressRequest = createStressRequest()
            println("Stress Request: $stressRequest") // Log the request for debugging

            val stressResponse = apiService.predictCondition(stressRequest)
            stressPrediction = stressResponse.body()?.probability
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching predictions", e)
        }
    }

    private fun createDehydrationRequest(): PredictionRequest {
        val healthData = healthData ?: return PredictionRequest("dehydration", emptyMap())
        val userDetails = userDetails ?: return PredictionRequest("dehydration", emptyMap())

        // Helper function to get latest value with proper type casting
        fun getLatestValue(entries: List<HealthDataEntry>): Double = entries.lastOrNull()?.value?.toDouble() ?: 0.0

        return PredictionRequest(
            condition = "dehydration",
            health_data = mapOf<String, Any>(
                "age" to (userDetails.age ?: 0).toInt(),
                "gender" to (userDetails.gender?.lowercase() ?: "male"),
                "weight" to getLatestValue(healthData.bodyMass),
                "height" to getLatestValue(healthData.height),
                "resting_bpm" to getLatestValue(healthData.restingHeartRate).toInt(), // wala kuy nakuha kay walay data sa latest
                "session_duration" to (getLatestValue(healthData.workoutDuration) / 60.0), // convert seconds to minutes
                "calories_burned" to getLatestValue(healthData.activeEnergyBurned).toInt(),
                "fat_percentage" to (getLatestValue(healthData.bodyFatPercentage) * 100).toInt(), // need e convert to percentage
                "water_intake" to getLatestValue(healthData.dietaryWater),
                "bmi" to getLatestValue(healthData.bodyMassIndex)
            )
        )
    }

    private fun createHeatStrokeRequest(): PredictionRequest {
        val healthData = healthData ?: return PredictionRequest("heat_stroke", emptyMap())
        val userDetails = userDetails ?: return PredictionRequest("heat_stroke", emptyMap())

        fun getLatestValue(entries: List<HealthDataEntry>): Double = entries.lastOrNull()?.value?.toDouble() ?: 0.0

        // Get current month (1-12) and hour (0-23) using Calendar (works on all API levels)
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return PredictionRequest(
            condition = "heat_stroke",
            health_data = mapOf<String, Any>(
                "water_intake" to getLatestValue(healthData.dietaryWater),
                "month" to currentMonth,
                "has_cvd" to if (userDetails.has_cvd == true) 0 else 1,
                "is_dehydrated" to 1, // Default value
                "heat_index" to 100.4, // Placeholder Farenheight
                "diastolic_bp" to getLatestValue(healthData.bloodPressureDiastolic).toInt(),
                "env_temp" to 30.0, // Placeholder IN CELCIUS
                "systolic_bp" to getLatestValue(healthData.bloodPressureSystolic).toInt(),
                "weight" to getLatestValue(healthData.bodyMass),
                "body_temp" to getLatestValue(healthData.bodyTemperature),
                "humidity" to 0.20, // Placeholder percentage in decimal form
                "sun_exposure" to 1, // Default value
                "bmi" to getLatestValue(healthData.bodyMassIndex),
                "heart_rate" to getLatestValue(healthData.heartRate).toInt(),
                "age" to (userDetails.age ?: 0).toInt(),
                "is_sweating" to 1, // Default value
                "strenuous_exercise" to 1, // Default value
                "sex" to when (userDetails.gender?.lowercase()) {
                    "male" -> 1
                    "female" -> 0
                    else -> 1 // default to male if unknown
                },
                "hour_of_day" to currentHour
            )
        )
    }

    private fun createStressRequest(): PredictionRequest {
        val healthData = healthData ?: return PredictionRequest("stress", emptyMap())

        fun getLatestValue(entries: List<HealthDataEntry>): Double = entries.lastOrNull()?.value?.toDouble() ?: 0.0

        return PredictionRequest(
            condition = "stress",
            health_data = mapOf<String, Any>(
                "humidity" to 20.0, // Placeholder HUMIDITY IN PERCENTAGE
                "temperature" to celsiusToFahrenheit(getLatestValue(healthData.bodyTemperature)), // THIS SHOULD BE BODY TEMPERATURE IN FARENHEIGHT
                "step_count" to getLatestValue(healthData.stepCount).toInt()
            )
        )
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }
}