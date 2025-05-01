package com.example.pulseplay.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pulseplay.R
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

        setupNotificationBadge()
        setupNotificationClickListener()
    }

    private fun setupNotificationBadge() {
        // Example: Show 3 notifications (replace with your actual count)
        updateNotificationBadge(3)
    }

    private fun setupNotificationClickListener() {
        binding.notificationIcon.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
            // Clear badge when notifications are viewed
            updateNotificationBadge(0)
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