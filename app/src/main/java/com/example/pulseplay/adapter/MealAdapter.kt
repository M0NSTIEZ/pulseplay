package com.example.pulseplay.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R
import com.example.pulseplay.models.Meal

class MealAdapter(private val mealList: MutableList<Meal>) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    // ViewHolder class holds item views
    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealTime: TextView = itemView.findViewById(R.id.mealTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meal_item, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]
        Log.d("MealAdapter", "Binding meal: ${meal.name} at position $position")

        holder.mealName.text = meal.name
        holder.mealTime.text = meal.time
    }

    override fun getItemCount(): Int {
        Log.d("MealAdapter", "List size: ${mealList.size}")
        return mealList.size
    }

    // Add a new meal to the list and notify the adapter
    fun addMeal(meal: Meal) {
        mealList.add(meal)
        notifyItemInserted(mealList.size - 1)  // Notify that an item was inserted at the end
    }

    // Update the entire list and notify the adapter
    fun updateMeals(newMeals: List<Meal>) {
        Log.d("MealAdapter", "Updating meals: ${newMeals.size} items")
        mealList.clear()
        mealList.addAll(newMeals)
        notifyDataSetChanged()
    }
}
