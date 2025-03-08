package com.example.pulseplay.api

import com.example.pulseplay.models.HealthData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HealthApiService {
    @GET("getHealthData/{userId}")
    fun getHealthData(@Path("userId") userId: String): Call<HealthData>
}
