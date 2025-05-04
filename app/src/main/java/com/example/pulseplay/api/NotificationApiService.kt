package com.example.pulseplay.api

import com.example.pulseplay.models.Notification
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// api/NotificationApiService.kt
interface NotificationApiService {
    @POST("notifications")
    suspend fun saveNotification(
        @Body notification: Notification
    ): Response<Notification>

    @GET("notifications/{username}")
    suspend fun getNotificationsByUser(
        @Path("username") username: String
    ): Response<List<Notification>>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: String
    ): Response<Notification>

    companion object {
        private const val BASE_URL = "https://health-api-a1jm.onrender.com/api/"

        fun create(): NotificationApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationApiService::class.java)
        }
    }
}