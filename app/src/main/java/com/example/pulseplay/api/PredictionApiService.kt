package com.example.pulseplay.api

import com.example.pulseplay.models.PredictionRequest
import com.example.pulseplay.models.PredictionResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface PredictionApiService {
    @POST("predict")
    suspend fun predictCondition(
        @Body request: PredictionRequest
    ): Response<PredictionResponse>

    companion object {
        private const val BASE_URL = "https://detect-api.onrender.com/"

        fun create(): PredictionApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(PredictionApiService::class.java)
        }
    }
}