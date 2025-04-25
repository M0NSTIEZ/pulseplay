package com.example.pulseplay.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pulseplay.R
import com.example.pulseplay.dashboard.BodyMassIndex
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set user name
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: "User"
        view.findViewById<TextView>(R.id.user_name).text = "Hi, $name"

        // BMI Section Click Listener
        view.findViewById<View>(R.id.btn_view_more).setOnClickListener {
            startActivity(Intent(requireContext(), BodyMassIndex::class.java))
        }
    }
}