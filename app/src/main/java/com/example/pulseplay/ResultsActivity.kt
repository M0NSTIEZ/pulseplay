package com.example.pulseplay

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import org.json.JSONObject

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Get the JSON response from intent
        val jsonResponse = intent.getStringExtra("jsonResponse") ?: return
        val jsonObject = JSONObject(jsonResponse)

        // Get condition type
        val condition = jsonObject.getString("condition")

        // Set condition title
        val conditionTitle = findViewById<TextView>(R.id.conditionTitle)
        conditionTitle.text = when (condition) {
            "heat_stroke" -> "Heat Stroke Analysis"
            "stress" -> "Stress Analysis"
            "dehydration" -> "Hydration Analysis"
            else -> "Health Analysis"
        }

        // Show appropriate layout based on condition
        when (condition) {
            "heat_stroke" -> displayHeatStrokeResults(jsonObject)
            "stress" -> displayStressResults(jsonObject)
            "dehydration" -> displayDehydrationResults(jsonObject)
        }

        // Add back button functionality
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun displayHeatStrokeResults(jsonObject: JSONObject) {
        findViewById<LinearLayout>(R.id.heatStrokeLayout).visibility = View.VISIBLE

        val prediction = jsonObject.getString("prediction")
        val probability = jsonObject.getDouble("probability")
        val threshold = jsonObject.getDouble("threshold")

        val predictionText = findViewById<TextView>(R.id.heatStrokePrediction)
        val progressBar = findViewById<ProgressBar>(R.id.heatStrokeProgress)
        val probabilityText = findViewById<TextView>(R.id.heatStrokeProbability)

        // Set prediction text with color
        if (prediction == "high_risk") {
            predictionText.text = "High Risk of Heat Stroke"
            predictionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            predictionText.text = "Low Risk of Heat Stroke"
            predictionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }

        // Set progress bar
        progressBar.max = 100
        progressBar.progress = (probability * 100).toInt()
        progressBar.progressDrawable.setColorFilter(
            if (probability > threshold) Color.RED else Color.GREEN,
            android.graphics.PorterDuff.Mode.SRC_IN
        )

        // Set probability text
        probabilityText.text = "Probability: ${(probability * 100).toInt()}% (Threshold: ${threshold * 100}%)"

        // Set recommendations
        setRecommendations("heat_stroke", prediction)
    }

    private fun displayStressResults(jsonObject: JSONObject) {
        findViewById<LinearLayout>(R.id.stressLayout).visibility = View.VISIBLE

        val prediction = jsonObject.getString("prediction")
        val probabilities = jsonObject.getJSONObject("probabilities")

        val highProb = probabilities.getDouble("high_stress")
        val mediumProb = probabilities.getDouble("medium_stress")
        val lowProb = probabilities.getDouble("low_stress")

        val predictionText = findViewById<TextView>(R.id.stressPrediction)
        val highProbText = findViewById<TextView>(R.id.highStressProbability)
        val mediumProbText = findViewById<TextView>(R.id.mediumStressProbability)
        val lowProbText = findViewById<TextView>(R.id.lowStressProbability)

        // Set prediction text
        predictionText.text = "Prediction: ${prediction.replace("_", " ").capitalize()}"
        predictionText.setTextColor(when (prediction) {
            "high_stress" -> ContextCompat.getColor(this, android.R.color.holo_red_dark)
            "medium_stress" -> ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            else -> ContextCompat.getColor(this, android.R.color.holo_green_dark)
        })

        // Set probability texts
        highProbText.text = "High Stress: ${(highProb * 100).toInt()}%"
        mediumProbText.text = "Medium Stress: ${(mediumProb * 100).toInt()}%"
        lowProbText.text = "Low Stress: ${(lowProb * 100).toInt()}%"

        // Set recommendations
        setRecommendations("stress", prediction)
    }

    private fun displayDehydrationResults(jsonObject: JSONObject) {
        findViewById<LinearLayout>(R.id.dehydrationLayout).visibility = View.VISIBLE

        val prediction = jsonObject.getString("prediction")

        val predictionText = findViewById<TextView>(R.id.dehydrationPrediction)

        // Set prediction text with color
        if (prediction == "dehydrated") {
            predictionText.text = "Dehydrated"
            predictionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            predictionText.text = "Hydrated"
            predictionText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }

        // Set recommendations
        setRecommendations("dehydration", prediction)
    }

    private fun setRecommendations(condition: String, prediction: String) {
        val recommendationsText = findViewById<TextView>(R.id.recommendationsText)

        recommendationsText.text = when (condition) {
            "heat_stroke" -> {
                if (prediction == "high_risk") {
                    "• Move to a cooler place immediately\n" +
                            "• Drink cool (not ice-cold) water\n" +
                            "• Loosen clothing and apply cool wet cloths\n" +
                            "• Seek medical attention if symptoms are severe"
                } else {
                    "• Continue to stay hydrated\n" +
                            "• Avoid prolonged sun exposure\n" +
                            "• Wear light, loose-fitting clothing\n" +
                            "• Take breaks in shaded or cool areas"
                }
            }
            "stress" -> {
                when (prediction) {
                    "high_stress" -> {
                        "• Practice deep breathing exercises\n" +
                                "• Take short breaks throughout the day\n" +
                                "• Engage in physical activity\n" +
                                "• Consider talking to a professional"
                    }
                    "medium_stress" -> {
                        "• Practice mindfulness techniques\n" +
                                "• Maintain a regular sleep schedule\n" +
                                "• Limit caffeine intake\n" +
                                "• Connect with friends or family"
                    }
                    else -> {
                        "• Continue healthy stress management habits\n" +
                                "• Maintain work-life balance\n" +
                                "• Engage in enjoyable activities\n" +
                                "• Monitor stress levels regularly"
                    }
                }
            }
            "dehydration" -> {
                if (prediction == "dehydrated") {
                    "• Drink water immediately\n" +
                            "• Continue drinking small amounts frequently\n" +
                            "• Avoid caffeine and alcohol\n" +
                            "• Eat water-rich fruits and vegetables"
                } else {
                    "• Continue drinking water regularly\n" +
                            "• Monitor urine color (aim for pale yellow)\n" +
                            "• Increase water intake during exercise\n" +
                            "• Be mindful of hydration in hot weather"
                }
            }
            else -> ""
        }
    }
}