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

    // UI Components
    private lateinit var analyzeButton: Button
    private lateinit var loadingProgress: ProgressBar
    private lateinit var conditionRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_form)

        initializeViews()
        setupButtonClickListener()
    }

    private fun initializeViews() {
        analyzeButton = findViewById(R.id.analyzeButton)
        loadingProgress = findViewById(R.id.loadingProgress)
        conditionRadioGroup = findViewById(R.id.conditionRadioGroup)
    }

    private fun setupButtonClickListener() {
        analyzeButton.setOnClickListener {
            if (!isConditionSelected()) {
                showSelectionError()
                return@setOnClickListener
            }

            startSimulatedAnalysis()
        }
    }

    private fun isConditionSelected(): Boolean {
        return conditionRadioGroup.checkedRadioButtonId != -1
    }

    private fun showSelectionError() {
        Toast.makeText(this, "Please select a condition", Toast.LENGTH_SHORT).show()
    }

    private fun startSimulatedAnalysis() {
        showLoadingState(true)

        analyzeButton.postDelayed({
            showLoadingState(false)
            launchResultsActivity()
        }, SIMULATION_DELAY_MS)
    }

    private fun showLoadingState(show: Boolean) {
        loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        analyzeButton.isEnabled = !show
    }

    private fun launchResultsActivity() {
        val selectedCondition = getSelectedCondition()
        val sampleJson = generateSampleResponse(selectedCondition)

        Intent(this, ResultsActivity::class.java).apply {
            putExtra(EXTRA_JSON_RESPONSE, sampleJson)
            startActivity(this)
        }
    }

    private fun getSelectedCondition(): String {
        return when (findViewById<RadioButton>(conditionRadioGroup.checkedRadioButtonId).id) {
            R.id.heatStrokeRadio -> CONDITION_HEAT_STROKE
            R.id.stressRadio -> CONDITION_STRESS
            R.id.dehydrationRadio -> CONDITION_DEHYDRATION
            else -> ""
        }
    }

    private fun generateSampleResponse(condition: String): String {
        return when (condition) {
            CONDITION_HEAT_STROKE -> generateHeatStrokeResponse()
            CONDITION_STRESS -> generateStressResponse()
            CONDITION_DEHYDRATION -> generateDehydrationResponse()
            else -> ""
        }
    }

    private fun generateHeatStrokeResponse(): String {
        val isHighRisk = Random.nextBoolean()
        val probability = if (isHighRisk) {
            Random.nextDouble(MIN_HIGH_RISK_PROBABILITY, MAX_PROBABILITY)
        } else {
            Random.nextDouble(MIN_PROBABILITY, MAX_LOW_RISK_PROBABILITY)
        }

        return """
        {
            "condition": "$CONDITION_HEAT_STROKE",
            "prediction": "${if (isHighRisk) "high_risk" else "low_risk"}",
            "probability": $probability,
            "status": "success",
            "threshold": $HEAT_STROKE_THRESHOLD
        }
        """.trimIndent()
    }

    private fun generateStressResponse(): String {
        val highStress = Random.nextDouble(MIN_HIGH_STRESS_PROBABILITY, MAX_PROBABILITY)
        val remaining = 1.0 - highStress
        val mediumStress = remaining * Random.nextDouble(MIN_MEDIUM_STRESS_FACTOR, MAX_MEDIUM_STRESS_FACTOR)
        val lowStress = remaining - mediumStress

        val prediction = when {
            highStress > HIGH_STRESS_THRESHOLD -> "high_stress"
            mediumStress > MEDIUM_STRESS_THRESHOLD -> "medium_stress"
            else -> "low_stress"
        }

        return """
        {
            "condition": "$CONDITION_STRESS",
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

    private fun generateDehydrationResponse(): String {
        val isDehydrated = Random.nextBoolean()
        return """
        {
            "condition": "$CONDITION_DEHYDRATION",
            "prediction": "${if (isDehydrated) "dehydrated" else "hydrated"}",
            "status": "success"
        }
        """.trimIndent()
    }

    companion object {
        // Constants
        private const val SIMULATION_DELAY_MS = 1000L
        private const val EXTRA_JSON_RESPONSE = "jsonResponse"

        // Condition types
        private const val CONDITION_HEAT_STROKE = "heat_stroke"
        private const val CONDITION_STRESS = "stress"
        private const val CONDITION_DEHYDRATION = "dehydration"

        // Probability thresholds
        private const val HEAT_STROKE_THRESHOLD = 0.5
        private const val HIGH_STRESS_THRESHOLD = 0.7
        private const val MEDIUM_STRESS_THRESHOLD = 0.5

        // Probability ranges
        private const val MIN_PROBABILITY = 0.1
        private const val MAX_PROBABILITY = 0.95
        private const val MIN_HIGH_RISK_PROBABILITY = 0.5
        private const val MAX_LOW_RISK_PROBABILITY = 0.49
        private const val MIN_HIGH_STRESS_PROBABILITY = 0.7
        private const val MIN_MEDIUM_STRESS_FACTOR = 0.3
        private const val MAX_MEDIUM_STRESS_FACTOR = 0.7
    }
}