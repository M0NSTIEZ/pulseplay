package com.example.pulseplay.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class TotalSteps : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_steps)

        setupBarChart()
    }

    private fun setupBarChart() {
        val barChart = findViewById<com.github.mikephil.charting.charts.BarChart>(R.id.steps_bar_chart)

        // Sample data (replace with your actual step data)
        val entries = listOf(
            BarEntry(0f, 8500f),  // Monday
            BarEntry(1f, 12000f), // Tuesday
            BarEntry(2f, 9800f),  // Wednesday
            BarEntry(3f, 15000f), // Thursday
            BarEntry(4f, 7500f),  // Friday
            BarEntry(5f, 11000f), // Saturday
            BarEntry(6f, 6000f)   // Sunday
        )

        // Create dataset
        val dataSet = BarDataSet(entries, "Daily Steps")
        dataSet.color = Color.parseColor("#00FFAA") // Bar color
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Show integer values
            }
        }

        // Configure chart appearance
        val barData = BarData(dataSet)
        barChart.data = barData

        // X-axis configuration
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value) {
                    0f -> "Mon"
                    1f -> "Tue"
                    2f -> "Wed"
                    3f -> "Thu"
                    4f -> "Fri"
                    5f -> "Sat"
                    6f -> "Sun"
                    else -> ""
                }
            }
        }

        // Left Y-axis configuration
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = 1000f
        leftAxis.textColor = Color.WHITE

        // Disable right Y-axis
        barChart.axisRight.isEnabled = false

        // Other chart settings
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateY(1000)

        // Refresh chart
        barChart.invalidate()
    }
}