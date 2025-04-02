package com.example.pulseplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pulseplay.fragments.ActivityFragment
import com.example.pulseplay.fragments.CameraFragment
import com.example.pulseplay.fragments.HomeFragment
import com.example.pulseplay.fragments.ProfileFragment
import com.example.pulseplay.databinding.ActivityHomePageBinding
//dashboard
class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding for cleaner code
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the default fragment (Home)
        loadFragment(HomeFragment())

        // Set click listeners for navigation bar buttons
        binding.navHome.setOnClickListener {
            loadFragment(HomeFragment())
        }
        binding.navActivity.setOnClickListener {
            loadFragment(ActivityFragment())
        }
        binding.navCamera.setOnClickListener {
            loadFragment(CameraFragment())
        }
        binding.navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
        }
    }

    // Method to load fragments dynamically
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
