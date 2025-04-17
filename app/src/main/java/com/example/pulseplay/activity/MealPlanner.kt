package com.example.pulseplay.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pulseplay.R
import com.example.pulseplay.adapter.MealAdapter
import com.example.pulseplay.databinding.ActivityMealPlannerBinding
import com.example.pulseplay.models.Meal
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MealPlanner : AppCompatActivity() {

    private lateinit var binding: ActivityMealPlannerBinding
    private val mealList = mutableListOf<Meal>()
    private lateinit var mealAdapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1️⃣ RecyclerView setup
        mealAdapter = MealAdapter(mealList)
        binding.mealRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MealPlanner)
            adapter = mealAdapter
        }

        // 2️⃣ UI callbacks
        binding.mpBack.setOnClickListener { finish() }
        binding.checkButton.setOnClickListener {
            Toast.makeText(this, "Check Button Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.addMealButton.setOnClickListener { showAddMealDialog() }

        // 3️⃣ Chart initialization
        setupBarChart(binding.barChart)
    }

    private fun setupBarChart(barChart: BarChart) {
        val entries = listOf(
            BarEntry(0f, 320f), BarEntry(1f, 450f), BarEntry(2f, 300f),
            BarEntry(3f, 600f), BarEntry(4f, 500f), BarEntry(5f, 700f),
            BarEntry(6f, 280f)
        )
        val dataSet = BarDataSet(entries, "Calories Burned (kcal)").apply {
            colors = listOf(
                Color.parseColor("#FF6B6B"), Color.parseColor("#FF8C42"),
                Color.parseColor("#FFA94D"), Color.parseColor("#FF6B6B"),
                Color.parseColor("#FF8C42"), Color.parseColor("#FFA94D"),
                Color.parseColor("#FF6B6B")
            )
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }
        barChart.data = BarData(dataSet)
        val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }
        barChart.apply {
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
            animateY(1000)
            invalidate()
        }
    }

    private fun showAddMealDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_meal, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.mealInput)
        val timeInput = dialogView.findViewById<EditText>(R.id.timeInput)

        AlertDialog.Builder(this)
            .setTitle("Add Meal")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val time = timeInput.text.toString().trim()
                if (name.isNotEmpty() && time.isNotEmpty()) {
                    // add to list, update adapter...
                    mealList.add(Meal(name, time))
                    mealAdapter.updateMeals(mealList)
                    // ...and scroll to bottom so you see it:
                    binding.mealRecyclerView.visibility = View.VISIBLE
                    binding.mealRecyclerView.scrollToPosition(mealList.size - 1)
                } else {
                    Toast.makeText(this,
                        "Please enter both meal name and time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
