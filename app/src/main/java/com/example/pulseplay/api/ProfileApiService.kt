package com.example.pulseplay.api

import com.example.pulseplay.profile.ProfileUpdateData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileApiService {
    @POST("api/user-details")
    fun updateProfile(
        @Header("Content-Type") contentType: String = "application/json",
        @Body profileData: ProfileUpdateRequest
    ): Call<ProfileUpdateResponse>

    companion object {
        private const val BASE_URL = "https://health-api-a1jm.onrender.com/"

        fun create(): ProfileApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ProfileApiService::class.java)
        }
    }
}

// Request data model matching the API requirements
data class ProfileUpdateRequest(
    val username: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val age: Int,
    val has_cvd: Boolean
)

// Response data model
data class ProfileUpdateResponse(
    val _id: String,
    val username: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val age: Int,
    val has_cvd: Boolean,
    val updatedAt: String,
    val __v: Int
)

// Extension function to convert local ProfileUpdateData to API request format
fun ProfileUpdateData.toRequest(): ProfileUpdateRequest {
    return ProfileUpdateRequest(
        username = this.username,
        gender = this.gender,
        height = this.height,
        weight = this.weight,
        age = this.age,
        has_cvd = this.hasCardiovascularDisease
    )
}