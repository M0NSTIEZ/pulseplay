package com.example.pulseplay.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pulseplay.R
import com.example.pulseplay.Register
import com.example.pulseplay.dashboard.BodyMassIndex
import com.example.pulseplay.dashboard.HeartRate
import com.example.pulseplay.dashboard.TotalSteps
import com.example.pulseplay.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set initial UI with cached data
        updateUI()

        // Fetch fresh data
        refreshData()

        // BMI Section Click Listener
        val bmibutton = view.findViewById<Button>(R.id.btn_view_more)
        bmibutton.setOnClickListener {
            startActivity(Intent(requireContext(), BodyMassIndex::class.java))
        }

        val totalStepsLayout = view.findViewById<LinearLayout>(R.id.total_steps)
        totalStepsLayout.setOnClickListener {
            val intent = Intent(requireContext(), TotalSteps::class.java)
            startActivity(intent)
        }
        val heartrate = view.findViewById<LinearLayout>(R.id.heart_rate)
        heartrate.setOnClickListener {
            val intent = Intent(requireContext(), HeartRate::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment comes back into view
        refreshData()
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                // Show loading state if needed
                UserRepository.fetchUserData()
                UserRepository.fetchPredictions()
                updateUI()
            } catch (e: Exception) {
                // Handle error (show toast or error message)
                Toast.makeText(context, "Failed to refresh data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val user = UserRepository.getUser()
        val userDetails = UserRepository.getUserDetails()

        println("User: $user")
        println("User Details: $userDetails")

        view?.findViewById<TextView>(R.id.user_name)?.text = "${user?.fullName ?: "User"}"

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val healthData = UserRepository.getHealthData()

        healthData?.let { data ->
            // Find latest entries for current date
            val latestSteps = data.stepCount.lastOrNull { it.date == currentDate }
            val latestEnergy = data.activeEnergyBurned.lastOrNull { it.date == currentDate }
            val latestHeartRate = data.heartRate.lastOrNull { it.date == currentDate }

            view?.findViewById<TextView>(R.id.totalStepsValue)?.text = latestSteps?.value.toString() + " /steps"

            println("Latest Steps: $latestSteps")
            println("Latest Energy: $latestEnergy")
            println("Latest Heart Rate: $latestHeartRate")
        }

        // Update prediction circles
        val dehydrationRisk = UserRepository.getDehydrationPrediction()
        val heatStrokeRisk = UserRepository.getHeatStrokePrediction()
        val stressRisk = UserRepository.getStressPrediction()

        println("Dehydration Risk: $dehydrationRisk")
        println("Heat Stroke Risk: $heatStrokeRisk")
        println("Stress Risk: $stressRisk")

        view?.findViewById<TextView>(R.id.dehydrationValue)?.text =
            "${(dehydrationRisk?.times(100))?.toInt() ?: 0}%"
        view?.findViewById<TextView>(R.id.heatStrokeValue)?.text =
            "${(heatStrokeRisk?.times(100))?.toInt() ?: 0}%"
        view?.findViewById<TextView>(R.id.fatigueValue)?.text =
            "${(stressRisk?.times(100))?.toInt() ?: 0}%"

        // Update circle colors based on risk level
        updateRiskCircle(R.id.dehydrationCircleBg, dehydrationRisk)
        updateRiskCircle(R.id.heatStrokeCircleBg, heatStrokeRisk)
        updateRiskCircle(R.id.fatigueCircleBg, stressRisk)
    }

    private fun updateRiskCircle(circleViewId: Int, risk: Double?) {
        risk?.let {
            val color = when {
                it > 0.7 -> R.color.high_risk_red
                it > 0.4 -> R.color.medium_risk_orange
                else -> R.color.low_risk_green
            }
            view?.findViewById<ImageView>(circleViewId)?.setColorFilter(
                ContextCompat.getColor(requireContext(), color)
            )
        }
    }
}