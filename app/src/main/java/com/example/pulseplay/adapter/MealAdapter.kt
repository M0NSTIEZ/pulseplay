package com.example.pulseplay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R
import com.example.pulseplay.models.Meal

class MealAdapter(private var mealList: MutableList<Meal>) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.mealName)
        val mealTime: TextView = view.findViewById(R.id.mealTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]
        holder.mealName.text = meal.name
        holder.mealTime.text = meal.time
    }

    override fun getItemCount(): Int = mealList.size

    // Function to update the list and refresh RecyclerView
    fun updateMeals(newMeals: List<Meal>) {
        mealList.clear()
        mealList.addAll(newMeals)
        notifyDataSetChanged()
    }
}
