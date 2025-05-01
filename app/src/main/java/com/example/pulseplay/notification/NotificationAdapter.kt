package com.example.pulseplay.notification

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseplay.R

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notificationTitle)
        val message: TextView = itemView.findViewById(R.id.notificationMessage)
        val time: TextView = itemView.findViewById(R.id.notificationTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.title.text = notification.title
        holder.message.text = notification.message
        holder.time.text = notification.time

        // Style unread notifications differently
        if (!notification.isRead) {
            holder.title.setTypeface(null, Typeface.BOLD)
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.unread_notification_bg))
        } else {
            holder.title.setTypeface(null, Typeface.NORMAL)
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
        }
    }

    override fun getItemCount(): Int = notifications.size
}