package com.example.pulseplay.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.example.pulseplay.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get Firebase user and display name
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: "User"

        // Set display name to the TextView
        val userNameTextView = view.findViewById<TextView>(R.id.user_name)
        userNameTextView.text = "Welcome, $name!"

        // Chart code
        val barChart = view.findViewById<BarChart>(R.id.bar_chart)
        val entries = listOf(
            BarEntry(0f, 4f),
            BarEntry(1f, 8f),
            BarEntry(2f, 6f),
            BarEntry(3f, 2f),
            BarEntry(4f, 7f)
        )

        val dataSet = BarDataSet(entries, "Weekly Water Intake").apply {
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
            }

            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false

            notifyDataSetChanged()
            invalidate()
        }
    }
}
