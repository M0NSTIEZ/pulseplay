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
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class AvgBloodPressure : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avg_blood_pressure)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBloodPressureChart()
    }

    private fun setupBloodPressureChart() {
        val bpChart = findViewById<BarChart>(R.id.bp_bar_chart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val systolicData = healthData?.bloodPressureSystolic
        val diastolicData = healthData?.bloodPressureDiastolic

        // Create a map of dates to systolic values for easier lookup
        val systolicMap = systolicData?.associateBy { it.date }
        val diastolicMap = diastolicData?.associateBy { it.date }

        // Get all unique dates from both datasets
        val allDates = (systolicData?.map { it.date } ?: emptyList()) +
                (diastolicData?.map { it.date } ?: emptyList())
        val uniqueDates = allDates.distinct().sorted()

        // Prepare entries and labels
        val systolicEntries = mutableListOf<BarEntry>()
        val diastolicEntries = mutableListOf<BarEntry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        uniqueDates.forEachIndexed { index, date ->
            try {
                val systolicValue = systolicMap?.get(date)?.value?.toFloat() ?: 0f
                val diastolicValue = diastolicMap?.get(date)?.value?.toFloat() ?: 0f

                // Only add entries if we have both values
                if (systolicValue > 0 && diastolicValue > 0) {
                    // Create stacked bar entries (systolic on top of diastolic)
                    systolicEntries.add(BarEntry(index.toFloat(), floatArrayOf(diastolicValue, systolicValue)))
                    diastolicEntries.add(BarEntry(index.toFloat(), floatArrayOf(diastolicValue)))

                    // Format date for display
                    val parsedDate = dateFormat.parse(date)
                    dateLabels.add(displayFormat.format(parsedDate))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Adjust label density based on data size
        val labelInterval = when {
            dateLabels.size > 30 -> 7  // Weekly labels
            dateLabels.size > 14 -> 3   // Every 3 days
            else -> 1                   // Daily
        }

        val finalLabels = dateLabels.mapIndexed { index, label ->
            if (index % labelInterval == 0 || index == dateLabels.size - 1) label else ""
        }

        // Create datasets
        val systolicDataSet = BarDataSet(systolicEntries, "Systolic").apply {
            colors = listOf(Color.TRANSPARENT, Color.RED) // Top part (systolic)
            setDrawValues(true)
            valueTextColor = Color.BLACK
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    // Display systolic value (second value in the stack)
                    return if (barEntry?.yVals?.size ?: 0 > 1) {
                        "${barEntry?.yVals?.get(1)?.toInt()}"
                    } else {
                        ""
                    }
                }
            }
        }

        val diastolicDataSet = BarDataSet(diastolicEntries, "Diastolic").apply {
            color = Color.BLUE // Bottom part (diastolic)
            setDrawValues(true)
            valueTextColor = Color.BLACK
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    // Display diastolic value
                    return "${barEntry?.y?.toInt()}"
                }
            }
        }

        bpChart.apply {
            data = BarData(systolicDataSet, diastolicDataSet).apply {
                barWidth = 0.4f
                setValueTextColor(Color.BLACK)
            }

            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = true
            animateY(1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(finalLabels)
                labelCount = finalLabels.size
                textColor = Color.BLACK
                textSize = 10f
                if (dateLabels.size > 14) {
                    labelRotationAngle = -45f
                }
            }

            axisLeft.apply {
                axisMinimum = 60f // Reasonable min for BP
                axisMaximum = 180f // Reasonable max for BP
                granularity = 20f
                textColor = Color.BLACK
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false

            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            setDragEnabled(true)
            setVisibleXRangeMaximum(14f)

            if (systolicEntries.isNotEmpty()) {
                moveViewToX(systolicEntries.last().x)
            }

            // Add space between bars
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            notifyDataSetChanged()
            invalidate()
        }
    }
}