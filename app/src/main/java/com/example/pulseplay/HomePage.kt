package com.example.pulseplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pulseplay.databinding.ActivityHomePageBinding
import com.example.pulseplay.fragments.ActivityFragment
import com.example.pulseplay.fragments.CameraFragment
import com.example.pulseplay.fragments.HomeFragment
import com.example.pulseplay.fragments.ProfileFragment

class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val steps = intent.getIntExtra("STEPS", 0)
        val calories = intent.getIntExtra("CALORIES", 0)

        loadFragment(HomeFragment.newInstance(steps, calories))

        binding.navHome.setOnClickListener {
            loadFragment(HomeFragment.newInstance(steps, calories))
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

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
