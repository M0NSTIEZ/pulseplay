package com.example.pulseplay.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R
import com.example.pulseplay.adapter.MealAdapter
import com.example.pulseplay.models.Meal

class MealPlanner : AppCompatActivity() {

    private lateinit var mealRecyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private val mealList = mutableListOf<Meal>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        mealRecyclerView = findViewById(R.id.mealRecyclerView)
        mealAdapter = MealAdapter(mealList)

        mealRecyclerView.layoutManager = LinearLayoutManager(this)
        mealRecyclerView.adapter = mealAdapter

        // ✅ Find back button outside of add_meal_button click
        val backButton = findViewById<ImageButton>(R.id.mp_back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // This will navigate back
        }

        findViewById<Button>(R.id.add_meal_button).setOnClickListener {
            showAddMealDialog()
        }
    }

    private fun showAddMealDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_meal, null)
        val mealInput = dialogView.findViewById<EditText>(R.id.mealInput)
        val timeInput = dialogView.findViewById<EditText>(R.id.timeInput)

        AlertDialog.Builder(this)
            .setTitle("Add Meal")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val mealName = mealInput.text.toString()
                val mealTime = timeInput.text.toString()

                if (mealName.isNotEmpty() && mealTime.isNotEmpty()) {
                    mealList.add(Meal(mealName, mealTime))
                    mealAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
