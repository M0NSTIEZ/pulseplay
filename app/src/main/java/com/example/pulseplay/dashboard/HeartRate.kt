package com.example.pulseplay.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R
import com.example.pulseplay.repository.UserRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class HeartRate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupHeartRateChartWithData()
    }

    private fun setupHeartRateChartWithData() {
        val heartRateChart = findViewById<LineChart>(R.id.heartRateChart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val heartRateDataList = healthData?.heartRate

        // Sort data by date (oldest first)
        val sortedData = heartRateDataList?.sortedBy { it.date }

        // Prepare entries and labels
        val entries = mutableListOf<Entry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault()) // Format for display

        sortedData?.forEachIndexed { index, hrData ->
            try {
                entries.add(Entry(index.toFloat(), hrData.value.toFloat()))

                // Format date for display
                val date = dateFormat.parse(hrData.date)
                dateLabels.add(displayFormat.format(date))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // If we have more than 14 data points, show every nth label to avoid crowding
        val labelInterval = when {
            sortedData?.size!! > 30 -> 7  // Weekly labels if more than a month
            sortedData?.size!! > 14 -> 3  // Every 3 days if 2-4 weeks
            else -> 1                      // Every day if less than 2 weeks
        }

        val finalLabels = dateLabels.mapIndexed { index, label ->
            if (index % labelInterval == 0 || index == dateLabels.size - 1) label else ""
        }

        val dataSet = LineDataSet(entries, "Heart Rate (BPM)").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(Color.RED)
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false) // Hide values on points to reduce clutter
            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line
            fillAlpha = 50
            setDrawFilled(true)
            fillColor = Color.RED
        }

        heartRateChart.apply {
            data = LineData(dataSet)
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = true
            animateXY(1000, 1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(finalLabels)
                labelCount = finalLabels.size
                textColor = Color.BLACK
                textSize = 10f

                // Rotate labels if many data points
                if (sortedData.size > 14) {
                    labelRotationAngle = -45f
                }
            }

            axisLeft.apply {
                axisMinimum = 40f // Minimum reasonable heart rate
                axisMaximum = 160f // Maximum reasonable heart rate
                granularity = 20f
                textColor = Color.BLACK
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false

            // Enable interactivity
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            setDragEnabled(true)
            setVisibleXRangeMaximum(14f) // Show about 2 weeks of data at once

            // Move to the end of the data (most recent readings)
            if (entries.isNotEmpty()) {
                moveViewToX(entries.last().x)
            }

            notifyDataSetChanged()
            invalidate()
        }
    }
}