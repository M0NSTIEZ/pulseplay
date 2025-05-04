package com.example.pulseplay.notification

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R
import com.example.pulseplay.models.Notification
import com.example.pulseplay.repository.UserRepository
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var noNotificationsText: TextView
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupToolbar()
        setupRecyclerView()
        loadNotifications()
    }

    private fun setupViews() {
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        noNotificationsText = findViewById(R.id.noNotificationsText)
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(emptyList()) { notification ->
            lifecycleScope.launch {
                UserRepository.markNotificationAsRead(notification.id ?: return@launch)
                // Update the notification in the adapter
                val index = adapter.notifications.indexOfFirst { it.id == notification.id }
                if (index != -1) {
                    adapter.updateNotification(index, notification.copy(isRead = true))
                }
            }
        }

        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationRecyclerView.adapter = adapter
    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            val username = UserRepository.getUser()?.username ?: return@launch
            try {
                val notifications = UserRepository.getNotificationsForUser(username)
                updateUI(notifications)
            } catch (e: Exception) {
                updateUI(emptyList())
            }
        }
    }

    private fun updateUI(notifications: List<Notification>) {
        runOnUiThread {
            if (notifications.isEmpty()) {
                noNotificationsText.visibility = View.VISIBLE
                notificationRecyclerView.visibility = View.GONE
            } else {
                noNotificationsText.visibility = View.GONE
                notificationRecyclerView.visibility = View.VISIBLE
                adapter.updateNotifications(notifications)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }
    }
}