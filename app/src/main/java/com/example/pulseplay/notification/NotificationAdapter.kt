package com.example.pulseplay.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R
import com.example.pulseplay.models.Notification

class NotificationAdapter(
    notifications: List<Notification>,
    private val onItemClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // Use mutable list internally
    private val _notifications = notifications.toMutableList()

    // Expose as read-only list
    val notifications: List<Notification> get() = _notifications

    fun updateNotifications(newNotifications: List<Notification>) {
        _notifications.clear()
        _notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = _notifications[position]
        holder.bind(notification)
        holder.itemView.setOnClickListener { onItemClick(notification) }
    }

    override fun getItemCount() = _notifications.size

    fun updateNotification(index: Int, notification: Notification) {
        _notifications[index] = notification
        notifyItemChanged(index)
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notification: Notification) {
            itemView.findViewById<TextView>(R.id.notificationTitle).text = notification.title
            itemView.findViewById<TextView>(R.id.notificationMessage).text = notification.message
            itemView.findViewById<TextView>(R.id.notificationTime).text =
                notification.createdAt?.take(10) ?: "" // Simple date formatting

            // Visual indicator for unread notifications
            itemView.setBackgroundColor(
                if (!notification.isRead) {
                    ContextCompat.getColor(itemView.context, R.color.unread_notification_bg)
                } else {
                    Color.TRANSPARENT
                }
            )
        }
    }
}