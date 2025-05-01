package com.example.pulseplay.notification

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val notificationRecyclerView = findViewById<RecyclerView>(R.id.notificationRecyclerView)
        val noNotificationsText = findViewById<TextView>(R.id.noNotificationsText)

        // Sample notifications - replace with your actual data
        val notifications = listOf(
            Notification(
                "Health Alert",
                "Your heart rate is elevated. Consider taking a break.",
                "10 minutes ago",
                isRead = false
            ),
            Notification(
                "Activity Completed",
                "You've reached your daily step goal! Great job!",
                "2 hours ago",
                isRead = true
            ),
            Notification(
                "New Feature",
                "Check out our new health tracking features!",
                "1 day ago",
                isRead = true
            )
        )

        if (notifications.isEmpty()) {
            noNotificationsText.visibility = View.VISIBLE
            notificationRecyclerView.visibility = View.GONE
        } else {
            noNotificationsText.visibility = View.GONE
            notificationRecyclerView.visibility = View.VISIBLE

            notificationRecyclerView.layoutManager = LinearLayoutManager(this)
            notificationRecyclerView.adapter = NotificationAdapter(notifications)
        }
    }
}