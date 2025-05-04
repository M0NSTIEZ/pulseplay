package com.example.pulseplay.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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

class AvgBodyTemperature : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avg_body_temperature)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBodyTemperatureChart()
    }

    private fun setupBodyTemperatureChart() {
        val tempChart = findViewById<BarChart>(R.id.body_temp_bar_chart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val tempDataList = healthData?.bodyTemperature

        // Sort data by date (oldest first)
        val sortedData = tempDataList?.sortedBy { it.date }

        // Prepare entries and labels
        val entries = mutableListOf<BarEntry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        sortedData?.forEachIndexed { index, tempData ->
            try {
                entries.add(BarEntry(index.toFloat(), tempData.value.toFloat()))

                // Format date for display
                val date = dateFormat.parse(tempData.date)
                dateLabels.add(displayFormat.format(date))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Adjust label density based on data size
        val labelInterval = when {
            sortedData?.size!! > 30 -> 7  // Weekly labels if more than a month
            sortedData.size > 14 -> 3     // Every 3 days if 2-4 weeks
            else -> 1                     // Every day if less than 2 weeks
        }

        val finalLabels = dateLabels.mapIndexed { index, label ->
            if (index % labelInterval == 0 || index == dateLabels.size - 1) label else ""
        }

        val dataSet = BarDataSet(entries, "Body Temperature (°C)").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawValues(true) // Show values on bars
        }

        tempChart.apply {
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
                textSize = 10f

                // Enable rotation if many labels
                if (sortedData.size > 14) {
                    labelRotationAngle = -45f
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 35f // Minimum reasonable body temp
                axisMaximum = 40f // Maximum reasonable body temp
                granularity = 0.5f // Show labels every 0.5°C
                textColor = Color.BLACK
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

            // Additional styling
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setFitBars(true)

            notifyDataSetChanged()
            invalidate()
        }
    }
}