package com.example.pulseplay.fragments

import android.annotation.SuppressLint
import  android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.pulseplay.MainActivity
import com.example.pulseplay.R
import com.example.pulseplay.auth.TokenManager
import com.example.pulseplay.profile.Achievement
import com.example.pulseplay.profile.ContactUsActivity
import com.example.pulseplay.profile.EditProfile
import com.example.pulseplay.profile.PrivacyPolicyActivity
import com.example.pulseplay.repository.UserRepository
import com.example.pulseplay.settings.ChangePasswordActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // 🔓 Get the logged-in user and display name
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: "User"

        // 📝 Set name to the profile_name TextView
        val nameTextView = view.findViewById<TextView>(R.id.profile_name)
        nameTextView.text = name

        // Profile Edit (More) button
        val editProfile = view.findViewById<ImageView>(R.id.profilemore1)
        editProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfile::class.java))
        }
        val achievement = view.findViewById<ImageView>(R.id.profilemore2)
        achievement.setOnClickListener {
            startActivity(Intent(requireActivity(), Achievement::class.java))
        }
        val contactus = view.findViewById<ImageView>(R.id.contact_us)
        contactus.setOnClickListener {
            startActivity(Intent(requireActivity(),ContactUsActivity::class.java))
        }
        val privacy = view.findViewById<ImageView>(R.id.more_privacy)
       privacy.setOnClickListener {
            startActivity(Intent(requireActivity(),PrivacyPolicyActivity::class.java))
        }
        val settings = view.findViewById<ImageView>(R.id.more_settings)
        settings.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
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
        // Clear local session (if any)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Firebase logout
        FirebaseAuth.getInstance().signOut()

        // Clear the API token
        TokenManager.clearToken()

        // Clear any local user data
        UserRepository.clearUserData()

        // Redirect to login screen
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
