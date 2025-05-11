package com.example.pulseplay.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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

class AvgWalkingHR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avg_walking_hr)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupWalkingHRChart()
    }

    private fun setupWalkingHRChart() {
        val walkingHRChart = findViewById<LineChart>(R.id.whr_bar_chart)

        // Get all health data from repository
        val healthData = UserRepository.getHealthData()
        val walkingHRDataList = healthData?.walkingHeartRateAverage

        // Sort data by date (oldest first)
        val sortedData = walkingHRDataList?.sortedBy { it.date }

        // Prepare entries and labels
        val entries = mutableListOf<Entry>()
        val dateLabels = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        sortedData?.forEachIndexed { index, hrData ->
            try {
                entries.add(Entry(index.toFloat(), hrData.value.toFloat()))
                val date = dateFormat.parse(hrData.date)
                dateLabels.add(displayFormat.format(date))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Adjust label density based on data size
        val labelInterval = when {
            sortedData?.size!! > 30 -> 7  // Weekly labels
            sortedData.size > 14 -> 3      // Every 3 days
            else -> 1                      // Daily
        }

        val finalLabels = dateLabels.mapIndexed { index, label ->
            if (index % labelInterval == 0 || index == dateLabels.size - 1) label else ""
        }

        val dataSet = LineDataSet(entries, "Avg Walking HR (BPM)").apply {
            color = Color.parseColor("#4CAF50") // Green color for walking HR
            valueTextColor = Color.BLACK
            lineWidth = 2f
            setCircleColor(Color.parseColor("#2E7D32")) // Darker green
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha = 50
            setDrawFilled(true)
            fillColor = Color.parseColor("#81C784")
        }

        walkingHRChart.apply {
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
                if (sortedData.size > 14) {
                    labelRotationAngle = -45f
                }
            }

            axisLeft.apply {
                axisMinimum = 20f // Reasonable min for walking HR
                axisMaximum = 200f // Reasonable max for walking HR
                granularity = 10f
                textColor = Color.BLACK
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false

            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            setDragEnabled(true)
            setVisibleXRangeMaximum(14f)

            if (entries.isNotEmpty()) {
                moveViewToX(entries.last().x)
            }

            notifyDataSetChanged()
            invalidate()
        }
    }
}