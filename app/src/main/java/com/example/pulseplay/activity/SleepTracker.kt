package com.example.pulseplay.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

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

        setupBarChart()
    }

    private fun setupBarChart() {
        val barChart = findViewById<BarChart>(R.id.bar_chart)

        // Sample sleep hours (Monday to Sunday)
        val entries = listOf(
            BarEntry(0f, 6.5f),
            BarEntry(1f, 7f),
            BarEntry(2f, 5.5f),
            BarEntry(3f, 8f),
            BarEntry(4f, 7.2f),
            BarEntry(5f, 6f),
            BarEntry(6f, 7.5f)
        )

        val dataSet = BarDataSet(entries, "Sleep (hrs)").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        barChart.apply {
            data = BarData(dataSet)
            setFitBars(true)
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            legend.isEnabled = true
            animateY(1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(true)
                valueFormatter = IndexAxisValueFormatter(
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                )
                labelCount = 7
                textColor = Color.BLACK
                textSize = 12f
            }

            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false

            notifyDataSetChanged()
            invalidate()
        }
    }
}
