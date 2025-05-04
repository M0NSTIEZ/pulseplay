package com.example.pulseplay.dashboard

import android.graphics.Color
import android.os.Bundle
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

class TotalSteps : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_steps)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBarChartWithStepData()
    }

    private fun setupBarChartWithStepData() {
        val barChart = findViewById<BarChart>(R.id.steps_bar_chart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val stepDataList = healthData?.stepCount

        // Sort data by date (oldest first)
        val sortedData = stepDataList?.sortedBy { it.date }

        // Prepare entries and labels
        val entries = mutableListOf<BarEntry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault()) // Format for display

        sortedData?.forEachIndexed { index, stepData ->
            try {
                entries.add(BarEntry(index.toFloat(), stepData.value.toFloat()))

                // Format date for display
                val date = dateFormat.parse(stepData.date)
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

        val dataSet = BarDataSet(entries, "Steps").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawValues(false) // Hide values on bars to reduce clutter
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
                setDrawGridLines(true)
                axisMinimum = 0f
                textColor = Color.BLACK
                granularity = 1000f // Show labels every 1000 steps
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

            // Configure the appearance
            setDrawGridBackground(false)
            setDrawBorders(false)
            setDrawMarkers(true)

            notifyDataSetChanged()
            invalidate()
        }
    }
}