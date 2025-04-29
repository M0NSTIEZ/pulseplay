package com.example.pulseplay.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.pulseplay.api.ProfileApiService
import com.example.pulseplay.api.ProfileUpdateResponse
import com.example.pulseplay.api.toRequest
import com.example.pulseplay.models.UserDetails
import com.example.pulseplay.repository.UserRepository

class EditProfile : AppCompatActivity() {
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile) // Ensure this file exists

        // Initialize views first
        val genderSpinner: Spinner = findViewById(R.id.spinner_gender)
        val cardiovascularSpinner: Spinner = findViewById(R.id.spinner_cardiovascular)
        val usernameEditText = findViewById<EditText>(R.id.et_username)
        val heightEditText = findViewById<EditText>(R.id.height_profile1)
        val weightEditText = findViewById<EditText>(R.id.weight_profile1)
        val ageEditText = findViewById<EditText>(R.id.age_profile1)

        // For Gender Spinner
        //val genderSpinner: Spinner = findViewById(R.id.spinner_gender)
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            mutableListOf("Select Gender").apply { addAll(genderOptions.toList()) })
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter
        genderSpinner.setSelection(0, false) // Show hint as initial selection

        // For Cardiovascular Spinner
        //val cardiovascularSpinner: Spinner = findViewById(R.id.spinner_cardiovascular)
        val cardiovascularOptions = resources.getStringArray(R.array.cardiovascular_options)
        val cardiovascularAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            mutableListOf("Cardiovascular Disease?").apply { addAll(cardiovascularOptions.toList()) })
        cardiovascularAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardiovascularSpinner.adapter = cardiovascularAdapter
        cardiovascularSpinner.setSelection(0, false) // Show hint as initial selection

        // Populate fields with existing user data
        val userDetails = UserRepository.getUserDetails()
        userDetails?.let { details ->
            // Set username (non-editable)
            usernameEditText.setText(details.username)

            // Set gender
            val genderPosition = genderOptions.indexOfFirst { it.equals(details.gender, ignoreCase = true) }
            if (genderPosition >= 0) {
                genderSpinner.setSelection(genderPosition + 1) // +1 because of the hint
            }

            // Set height, weight, age
            details.height?.let { heightEditText.setText(it.toString()) }
            details.weight?.let { weightEditText.setText(it.toString()) }
            details.age?.let { ageEditText.setText(it.toString()) }

            // Set cardiovascular disease status
            val cardioOptions = resources.getStringArray(R.array.cardiovascular_options)
            val cardioPosition = if (details.has_cvd == true) {
                cardioOptions.indexOf("Yes")
            } else {
                cardioOptions.indexOf("No")
            }
            if (cardioPosition >= 0) {
                cardiovascularSpinner.setSelection(cardioPosition + 1) // +1 because of the hint
            }
        }



        // Find Views
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        val saveButton = findViewById<Button>(R.id.btn_save)

        // Handle Back Button Click
        backButton?.setOnClickListener {
            finish()
        }

        // Handle Save Button Click (optional)
        saveButton?.setOnClickListener {
            // Collect all the data from the form
            val username = findViewById<EditText>(R.id.et_username).text.toString()

            // Get gender selection (skip the first item which is the hint)
            val gender = if (genderSpinner.selectedItemPosition > 0) {
                genderSpinner.selectedItem.toString()
            } else {
                // Show error if no gender selected
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get height (validate it's a valid float)
            val heightStr = findViewById<EditText>(R.id.height_profile1).text.toString()
            val height = try {
                heightStr.toFloat()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get weight (validate it's a valid float)
            val weightStr = findViewById<EditText>(R.id.weight_profile1).text.toString()
            val weight = try {
                weightStr.toFloat()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get age (validate it's not empty)
            val ageStr = findViewById<EditText>(R.id.age_profile1).text.toString()
            if (ageStr.isEmpty()) {
                Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val age = ageStr.toInt()

            // Get cardiovascular disease selection (skip the first item which is the hint)
            val hasCardiovascularDisease = if (cardiovascularSpinner.selectedItemPosition > 0) {
                cardiovascularSpinner.selectedItem.toString() == "Yes"
            } else {
                // Show error if no selection
                Toast.makeText(this, "Please select cardiovascular disease status", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a data class/object with all the collected information
            val profileData = ProfileUpdateData(
                username = username,
                gender = gender,
                height = height,
                weight = weight,
                age = age,
                hasCardiovascularDisease = hasCardiovascularDisease
            )

            // Make the API call
            val apiService = ProfileApiService.create()
            apiService.updateProfile(profileData = profileData.toRequest()).enqueue(
                object : Callback<ProfileUpdateResponse> {
                    override fun onResponse(
                        call: Call<ProfileUpdateResponse>,
                        response: Response<ProfileUpdateResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { updatedProfile ->
                                // Convert the response to UserDetails and update repository
                                val userDetails = UserDetails(
                                    _id = updatedProfile._id,
                                    username = updatedProfile.username,
                                    gender = updatedProfile.gender,
                                    height = updatedProfile.height,
                                    weight = updatedProfile.weight,
                                    age = updatedProfile.age,
                                    has_cvd = updatedProfile.has_cvd,
                                    updatedAt = updatedProfile.updatedAt,
                                    __v = updatedProfile.__v
                                )
                                UserRepository.setUserDetails(userDetails)

                                Toast.makeText(
                                    this@EditProfile,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } ?: run {
                                Toast.makeText(
                                    this@EditProfile,
                                    "Failed to update profile: Empty response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@EditProfile,
                                "Failed to update profile: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                        Toast.makeText(
                            this@EditProfile,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            // For now, just show the collected data and close
            Toast.makeText(this, "Profile data ready for API call", Toast.LENGTH_SHORT).show()
            finish()
        }


        val uploadBtn = findViewById<ImageView>(R.id.btn_upload_pic)

        uploadBtn.setOnClickListener {
            // TODO: Launch image picker here
        }


        // Apply Window Insets (Fix Null Reference)
        val rootView = findViewById<View>(R.id.age_profile) // Replace with actual root ID
        rootView?.setOnApplyWindowInsetsListener { _, insets ->
            insets
        }
    }
}

// Data class to hold all profile information
data class ProfileUpdateData(
    val username: String,
    val gender: String,
    val height: Float,
    val weight: Float,
    val age: Int,
    val hasCardiovascularDisease: Boolean
)
