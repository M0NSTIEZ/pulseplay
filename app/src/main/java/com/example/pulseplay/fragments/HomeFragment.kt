package com.example.pulseplay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pulseplay.R

class HomeFragment : Fragment() {

    private var steps: Int = 0
    private var calories: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            steps = it.getInt("STEPS", 0)
            calories = it.getInt("CALORIES", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val stepsTextView = view.findViewById<TextView>(R.id.tv_steps)
        val caloriesTextView = view.findViewById<TextView>(R.id.tv_calories)

        stepsTextView.text = "Steps: $steps"
        caloriesTextView.text = "Calories: $calories"

        return view
    }

    companion object {
        fun newInstance(steps: Int, calories: Int) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt("STEPS", steps)
                    putInt("CALORIES", calories)
                }
            }
    }
}
