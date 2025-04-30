package com.example.pulseplay.dashboard

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pulseplay.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class HeartRate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        setupHeartRateChart()
    }

    private fun setupHeartRateChart() {
        val heartRateChart = findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.heartRateChart)

        // Sample data (replace with real-time data)
        val entries = listOf(
            Entry(0f, 72f),  // Time (e.g., 10:00 AM)
            Entry(1f, 85f),   // Time (e.g., 11:00 AM)
            Entry(2f, 78f),
            Entry(3f, 90f),
            Entry(4f, 88f)
        )

        // Configure dataset
        val dataSet = LineDataSet(entries, "Heart Rate (BPM)")
        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.WHITE
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.RED)
        dataSet.circleRadius = 4f

        // Configure X-axis (time labels)
        val xAxis = heartRateChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> "10:00"
                    1 -> "11:00"
                    2 -> "12:00"
                    3 -> "13:00"
                    4 -> "14:00"
                    else -> ""
                }
            }
        }

        // Configure Y-axis
        val leftAxis = heartRateChart.axisLeft
        leftAxis.axisMinimum = 60f
        leftAxis.axisMaximum = 120f
        leftAxis.granularity = 10f
        leftAxis.textColor = Color.WHITE

        // Disable right Y-axis
        heartRateChart.axisRight.isEnabled = false

        // Chart styling
        heartRateChart.description.isEnabled = false
        heartRateChart.legend.isEnabled = false
        heartRateChart.setTouchEnabled(true)
        heartRateChart.setPinchZoom(true)

        // Set data and refresh
        heartRateChart.data = LineData(dataSet)
        heartRateChart.invalidate()
    }
}