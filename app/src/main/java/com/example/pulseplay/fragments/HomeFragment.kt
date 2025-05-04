package com.example.pulseplay.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pulseplay.R
import com.example.pulseplay.Register
import com.example.pulseplay.dashboard.AvgBloodPressure
import com.example.pulseplay.dashboard.AvgBodyTemperature
import com.example.pulseplay.dashboard.AvgCaloriesConsumed
import com.example.pulseplay.dashboard.AvgFatPercentage
import com.example.pulseplay.dashboard.AvgOxygenSat
import com.example.pulseplay.dashboard.AvgRespiratoryRate
import com.example.pulseplay.dashboard.AvgWalkingHR
import com.example.pulseplay.dashboard.BodyMassIndex
import com.example.pulseplay.dashboard.HeartRate
import com.example.pulseplay.dashboard.TotalSteps
import com.example.pulseplay.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.pulseplay.databinding.FragmentHomeBinding
import com.example.pulseplay.notification.NotificationActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var badge: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
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
        val walkingheartrate = view.findViewById<LinearLayout>(R.id.avg_walking_hr)
        walkingheartrate.setOnClickListener {
            val intent = Intent(requireContext(), AvgWalkingHR::class.java)
            startActivity(intent)
        }
        val avgbloodpressure = view.findViewById<LinearLayout>(R.id.avg_blood_pressure)
        avgbloodpressure.setOnClickListener {
            val intent = Intent(requireContext(), AvgBloodPressure::class.java)
            startActivity(intent)
        }
        val avgbodytemp = view.findViewById<LinearLayout>(R.id.avg_body_temp)
        avgbodytemp.setOnClickListener {
            val intent = Intent(requireContext(), AvgBodyTemperature::class.java)
            startActivity(intent)
        }
        val avgresrate = view.findViewById<LinearLayout>(R.id.avg_res_rate)
        avgresrate.setOnClickListener {
            val intent = Intent(requireContext(), AvgRespiratoryRate::class.java)
            startActivity(intent)
        }
        val avgoxygensat = view.findViewById<LinearLayout>(R.id.avg_oxygen_sat)
        avgoxygensat.setOnClickListener {
            val intent = Intent(requireContext(), AvgOxygenSat::class.java)
            startActivity(intent)
        }
        val avgfatperc = view.findViewById<LinearLayout>(R.id.avg_fat_per)
        avgfatperc.setOnClickListener {
            val intent = Intent(requireContext(), AvgFatPercentage::class.java)
            startActivity(intent)
        }
        val avgcalcon = view.findViewById<LinearLayout>(R.id.avg_cal_consumed)
        avgcalcon.setOnClickListener {
            val intent = Intent(requireContext(), AvgCaloriesConsumed::class.java)
            startActivity(intent)
        }
        setupNotificationBadge()
        setupNotificationClickListener()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment comes back into view
        refreshData()
    }

    private fun setupNotificationBadge() {
        // Example: Show 3 notifications (replace with your actual count)
        updateNotificationBadge(3)
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                // Show loading state if needed
                UserRepository.fetchUserData()
                UserRepository.fetchPredictions()

                // Check predictions and create notifications if needed
                UserRepository.getUser()?.username?.let { username ->
                    UserRepository.checkAndCreateNotifications(username)
                }

                updateUI()
            } catch (e: Exception) {
                // Handle error (show toast or error message)
                Toast.makeText(context, "Failed to refresh data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNotificationClickListener() {
        binding.notificationIcon.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
            // Clear badge when notifications are viewed
            updateNotificationBadge(0)
        }
    }

    private fun updateUI() {
        val user = UserRepository.getUser()
        val userDetails = UserRepository.getUserDetails()

        println("User: $user")
        println("User Details: $userDetails")

        binding.userName.text = "${user?.fullName ?: "User"}"

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val healthData = UserRepository.getHealthData()

        healthData?.let { data ->
            // Find latest entries for current date
            val latestSteps = data.stepCount.lastOrNull { it.date == currentDate }
            val latestHeartRate = data.heartRate.lastOrNull { it.date == currentDate }
            val latestWalkingHeartRate = data.walkingHeartRateAverage.lastOrNull { it.date == currentDate }
            val latestBloodPressureSystolic = data.bloodPressureSystolic.lastOrNull { it.date == currentDate }
            val latestBloodPressureDiastolic = data.bloodPressureDiastolic.lastOrNull { it.date == currentDate }
            val latestBodyTemp = data.bodyTemperature.lastOrNull { it.date == currentDate }
            val latestRespiratoryRate = data.respiratoryRate.lastOrNull { it.date == currentDate }
            val latestOxygenSaturation = data.oxygenSaturation.lastOrNull { it.date == currentDate }
            val latestFatPercentage = data.bodyFatPercentage.lastOrNull { it.date == currentDate }
            val latestDietaryEnergy = data.dietaryEnergyConsumed.lastOrNull { it.date == currentDate }

            // Update UI with latest values
            binding.totalStepsValue.text = latestSteps?.value?.toInt().toString() + " /steps"
            binding.heartRateValue.text = latestHeartRate?.value?.toInt().toString() + " bpm"
            binding.walkingHeartRateValue.text = latestWalkingHeartRate?.value?.toInt().toString() + " bpm"

            // Combine systolic and diastolic blood pressure if both exist
            val bloodPressureText = if (latestBloodPressureSystolic != null && latestBloodPressureDiastolic != null) {
                "${latestBloodPressureSystolic.value.toInt()}/${latestBloodPressureDiastolic.value.toInt()}"
            } else if (latestBloodPressureSystolic != null) {
                "${latestBloodPressureSystolic.value.toInt()}/--"
            } else if (latestBloodPressureDiastolic != null) {
                "--/${latestBloodPressureDiastolic.value.toInt()}"
            } else {
                "--/--"
            }
            binding.bloodPressureValue.text = "$bloodPressureText mmHg"

            binding.bodyTempValue.text = latestBodyTemp?.value?.toString()?.take(4) + " °C" // Limit to 1 decimal place
            binding.respiratoryRateValue.text = latestRespiratoryRate?.value?.toInt().toString() + " bpm"
            binding.oxygenSaturationValue.text = (latestOxygenSaturation?.value?.times(100))?.toInt().toString() + " %"
            binding.fatPercentageValue.text = latestFatPercentage?.value?.toInt().toString() + " %"
            binding.dietartyEnergyValue.text = latestDietaryEnergy?.value?.toInt().toString() + " kcal"

            println("Latest Steps: $latestSteps")
            println("Latest Heart Rate: $latestHeartRate")
            println("Latest Walking Heart Rate: $latestWalkingHeartRate")
            println("Latest Blood Pressure: $latestBloodPressureSystolic/$latestBloodPressureDiastolic")
            println("Latest Body Temp: $latestBodyTemp")
            println("Latest Respiratory Rate: $latestRespiratoryRate")
            println("Latest Oxygen Saturation: $latestOxygenSaturation")
            println("Latest Fat Percentage: $latestFatPercentage")
            println("Latest Dietary Energy: $latestDietaryEnergy")
        }

        // Update prediction circles
        val dehydrationRisk = UserRepository.getDehydrationPrediction()
        val heatStrokeRisk = UserRepository.getHeatStrokePrediction()
        val stressRisk = UserRepository.getStressPrediction()

        println("Dehydration Risk: $dehydrationRisk")
        println("Heat Stroke Risk: $heatStrokeRisk")
        println("Stress Risk: $stressRisk")

        binding.dehydrationValue.text = "${(dehydrationRisk?.times(100))?.toInt() ?: 0}%"
        binding.heatStrokeValue.text = "${(heatStrokeRisk?.times(100))?.toInt() ?: 0}%"
        binding.fatigueValue.text = "${(stressRisk?.times(100))?.toInt() ?: 0}%"

        // Update circle colors based on risk level
        updateRiskCircle(binding.dehydrationCircleBg, dehydrationRisk)
        updateRiskCircle(binding.heatStrokeCircleBg, heatStrokeRisk)
        updateRiskCircle(binding.fatigueCircleBg, stressRisk)
    }

    private fun updateRiskCircle(circleView: ImageView, risk: Double?) {
        risk?.let {
            val color = when {
                it > 0.7 -> R.color.high_risk_red
                it > 0.4 -> R.color.medium_risk_orange
                else -> R.color.low_risk_green
            }
            circleView.setColorFilter(
                ContextCompat.getColor(requireContext(), color)
            )
        }
    }

    private fun updateNotificationBadge(count: Int) {
        // Remove existing badge if any
        badge?.let {
            (binding.notificationIcon.parent as? ViewGroup)?.removeView(it)
        }

        if (count > 0) {
            badge = TextView(requireContext()).apply {
                id = R.id.notificationBadge
                text = if (count > 9) "9+" else count.toString()
                setTextColor(Color.WHITE)
                textSize = 12f
                setBackgroundResource(R.drawable.badge_background)
                // Add padding for better appearance
                setPadding(8.dpToPx(), 4.dpToPx(), 8.dpToPx(), 4.dpToPx())
                // Ensure text is centered
                gravity = Gravity.CENTER
            }

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Position badge on top-right of icon
                gravity = Gravity.END or Gravity.TOP
                // Adjust these values to fine-tune position:
                topMargin = (-5).dpToPx()  // Negative to overlap icon
                marginEnd = (-5).dpToPx()  // Negative to overlap icon
            }

            // Add badge to the notification icon's parent
            (binding.notificationIcon.parent as? ViewGroup)?.addView(badge, params)
        }
    }

    // Extension to convert dp to pixels
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}