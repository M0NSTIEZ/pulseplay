package com.example.pulseplay

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class InputFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_form)

        val analyzeButton = findViewById<Button>(R.id.analyzeButton)
        val loadingProgress = findViewById<ProgressBar>(R.id.loadingProgress)
        val conditionRadioGroup = findViewById<RadioGroup>(R.id.conditionRadioGroup)

        analyzeButton.setOnClickListener {
            val selectedId = conditionRadioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a condition", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            loadingProgress.visibility = View.VISIBLE
            analyzeButton.isEnabled = false

            // Simulate analysis (no API call)
            analyzeButton.postDelayed({
                loadingProgress.visibility = View.GONE
                analyzeButton.isEnabled = true

                // Get the selected condition
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val condition = when (selectedRadioButton.id) {
                    R.id.heatStrokeRadio -> "heat_stroke"
                    R.id.stressRadio -> "stress"
                    R.id.dehydrationRadio -> "dehydration"
                    else -> ""
                }

                // Generate appropriate sample response
                val sampleJson = generateSampleResponse(condition)

                // Start ResultsActivity with sample data
                val intent = Intent(this, ResultsActivity::class.java)
                intent.putExtra("jsonResponse", sampleJson)
                startActivity(intent)
            }, 1000) // 1 second delay to simulate processing
        }
    }

    private fun generateSampleResponse(condition: String): String {
        return when (condition) {
            "heat_stroke" -> {
                // Randomly generate high or low risk
                val isHighRisk = Random.nextBoolean()
                val probability = if (isHighRisk) Random.nextDouble(0.5, 0.95) else Random.nextDouble(0.1, 0.49)

                """
                {
                    "condition": "heat_stroke",
                    "prediction": "${if (isHighRisk) "high_risk" else "low_risk"}",
                    "probability": $probability,
                    "status": "success",
                    "threshold": 0.5
                }
                """.trimIndent()
            }

            "stress" -> {
                // Generate random stress probabilities (summing to 1.0)
                val highStress = Random.nextDouble(0.7, 0.98)
                val remaining = 1.0 - highStress
                val mediumStress = remaining * Random.nextDouble(0.3, 0.7)
                val lowStress = remaining - mediumStress

                // Determine prediction based on highest probability
                val prediction = when {
                    highStress > 0.7 -> "high_stress"
                    mediumStress > 0.5 -> "medium_stress"
                    else -> "low_stress"
                }

                """
                {
                    "condition": "stress",
                    "confidence": $highStress,
                    "prediction": "$prediction",
                    "probabilities": {
                        "high_stress": $highStress,
                        "medium_stress": $mediumStress,
                        "low_stress": $lowStress
                    },
                    "status": "success"
                }
                """.trimIndent()
            }

            "dehydration" -> {
                // Randomly choose between hydrated and dehydrated
                val isDehydrated = Random.nextBoolean()

                """
                {
                    "condition": "dehydration",
                    "prediction": "${if (isDehydrated) "dehydrated" else "hydrated"}",
                    "status": "success"
                }
                """.trimIndent()
            }

            else -> ""
        }
    }
}