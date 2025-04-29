package com.example.pulseplay.api

import com.example.pulseplay.models.HealthData
import com.example.pulseplay.models.LoginRequest
import com.example.pulseplay.models.LoginResponse
import com.example.pulseplay.models.User
import com.example.pulseplay.models.UserDetails
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/user")
    suspend fun getUser(@Header("Authorization") token: String): Response<User>

    @GET("api/user-details/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Response<UserDetails>

    @GET("api/health-data")
    suspend fun getHealthData(
        @Query("username") username: String
    ): Response<HealthData>

    companion object {
        private const val BASE_URL = "https://health-api-a1jm.onrender.com/"

        fun create(): UserApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UserApiService::class.java)
        }
    }
}