package com.example.pulseplay.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pulseplay.MainActivity  // Only this import is needed
import com.example.pulseplay.R
import com.example.pulseplay.profile.EditProfile
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Profile More button
        val profileMore = view.findViewById<ImageView>(R.id.profilemore1)
        profileMore.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfile::class.java))
        }

        // Logout button
        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .create()
            .show()
    }

    private fun performLogout() {
        // Example using SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

// If using Firebase Auth
        FirebaseAuth.getInstance().signOut()
        // Navigate back to MainActivity (which is your login screen)
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}