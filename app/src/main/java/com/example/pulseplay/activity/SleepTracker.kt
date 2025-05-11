package com.example.pulseplay.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R
import com.example.pulseplay.repository.UserRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class SleepTracker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_tracker)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.st_back)
        backButton?.setOnClickListener {
            finish()
        }

        setupBarChartWithAllData()
    }

    private fun setupBarChartWithAllData() {
        val barChart = findViewById<BarChart>(R.id.bar_chart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val sleepDataList = healthData?.sleepAnalysis

        // Sort data by date (oldest first)
        val sortedData = sleepDataList?.sortedBy { it.date }

        // Prepare entries and labels
        val entries = mutableListOf<BarEntry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault()) // Format for display

        sortedData?.forEachIndexed { index, sleepData ->
            try {
                // Convert minutes to hours
                val sleepHours = sleepData.value / 60.0
                entries.add(BarEntry(index.toFloat(), sleepHours.toFloat()))

                // Format date for display
                val date = dateFormat.parse(sleepData.date)
                dateLabels.add(displayFormat.format(date))
            } catch (e: Exception) {
                // Handle date parsing errors
                e.printStackTrace()
            }
        }

        // If we have more than 14 data points, show every nth label to avoid crowding
        val labelInterval = when {
            sortedData?.size!! > 30 -> 7  // Weekly labels if more than a month
            sortedData?.size!! > 14 -> 3  // Every 3 days if 2-4 weeks
            else -> 1                  // Every day if less than 2 weeks
        }

        val finalLabels = dateLabels.mapIndexed { index, label ->
            if (index % labelInterval == 0 || index == dateLabels.size - 1) label else ""
        }

        val dataSet = BarDataSet(entries, "Sleep (hrs)").apply {
            colors = listOf(Color.GREEN) // Set color to green
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        barChart.apply {
            data = BarData(dataSet).apply {
                barWidth = 0.5f // Adjust bar width based on data density
            }

            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = true
            animateY(1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(true)
                valueFormatter = IndexAxisValueFormatter(finalLabels)
                labelCount = finalLabels.size
                textColor = Color.BLACK
                textSize = 10f // Smaller text to fit more labels

                // Enable rotation if many labels
                if (sortedData.size > 14) {
                    labelRotationAngle = -45f
                }
            }

            axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
                textColor = Color.BLACK
                granularity = 1f // Show labels every hour
            }
            axisRight.isEnabled = false

            // Enable zooming and scrolling for large datasets
            setScaleEnabled(true)
            setPinchZoom(true)
            setVisibleXRangeMaximum(14f) // Show max 14 bars at once (about 2 weeks)

            // Move to the end of the data (most recent dates)
            if (entries.isNotEmpty()) {
                moveViewToX(entries.last().x)
            }

            notifyDataSetChanged()
            invalidate()
        }
    }
}