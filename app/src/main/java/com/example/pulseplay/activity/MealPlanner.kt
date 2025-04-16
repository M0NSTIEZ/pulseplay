package com.example.pulseplay.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MealPlanner : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        val backButton = findViewById<ImageButton>(R.id.mp_back)
        backButton?.setOnClickListener {
            finish()
        }

        setupBarChart()
    }

    private fun setupBarChart() {
        val barChart = findViewById<BarChart>(R.id.bar_chart)

        val entries = listOf(
            BarEntry(0f, 320f),
            BarEntry(1f, 450f),
            BarEntry(2f, 300f),
            BarEntry(3f, 600f),
            BarEntry(4f, 500f),
            BarEntry(5f, 700f),
            BarEntry(6f, 280f)
        )

        val dataSet = BarDataSet(entries, "Calories Burned (kcal)").apply {
            colors = listOf(
                Color.parseColor("#FF6B6B"), // Red
                Color.parseColor("#FF8C42"), // Orange
                Color.parseColor("#FFA94D"),
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#FF8C42"),
                Color.parseColor("#FFA94D"),
                Color.parseColor("#FF6B6B")
            )
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        val data = BarData(dataSet)
        barChart.data = data

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
