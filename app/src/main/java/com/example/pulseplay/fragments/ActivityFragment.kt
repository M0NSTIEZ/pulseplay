package com.example.pulseplay.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.pulseplay.R
import com.example.pulseplay.activity.Workout

class ActivityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Finding Views inside Fragment Layout
        val work1 = view.findViewById<ImageView>(R.id.work1)
        val work2 = view.findViewById<ImageView>(R.id.work2)
        val work3 = view.findViewById<ImageView>(R.id.work3)
        val work4 = view.findViewById<ImageView>(R.id.work4)

        // Setting Click Listeners to Navigate to Workout Activity
        val intent = Intent(requireContext(), Workout::class.java)

        work1.setOnClickListener { startActivity(intent) }
        work2.setOnClickListener { startActivity(intent) }
        work3.setOnClickListener { startActivity(intent) }
        work4.setOnClickListener { startActivity(intent) }
    }
}
