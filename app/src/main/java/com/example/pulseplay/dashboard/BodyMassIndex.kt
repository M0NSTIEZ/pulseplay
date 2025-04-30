package com.example.pulseplay.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R
import com.example.pulseplay.databinding.ActivityBodyMassIndexBinding
import com.google.android.material.textfield.TextInputEditText

class BodyMassIndex : AppCompatActivity() {

    private lateinit var binding: ActivityBodyMassIndexBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBodyMassIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Set up calculate button
        binding.calculateButton.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        // Get weight and height values
        val weightText = binding.weightEditText.text.toString()
        val heightText = binding.heightEditText.text.toString()

        if (weightText.isEmpty() || heightText.isEmpty()) {
            Toast.makeText(this, "Please enter both weight and height", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val weight = weightText.toFloat()
            val height = heightText.toFloat() / 100 // Convert cm to meters

            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, "Values must be greater than zero", Toast.LENGTH_SHORT).show()
                return
            }

            // Calculate BMI (weight / (height^2))
            val bmi = weight / (height * height)

            // Display results
            showResults(bmi)

            // Show Toast with results
            val category = getBMICategory(bmi)
            Toast.makeText(
                this,
                "BMI: %.1f - %s".format(bmi, category),
                Toast.LENGTH_LONG
            ).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResults(bmi: Float) {
        // Make result card visible
        binding.resultCard.visibility = android.view.View.VISIBLE

        // Set BMI value
        binding.resultText.text = "Your BMI: %.1f".format(bmi)

        // Set category
        val category = getBMICategory(bmi)
        binding.categoryText.text = category
    }

    private fun getBMICategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal weight"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }
}