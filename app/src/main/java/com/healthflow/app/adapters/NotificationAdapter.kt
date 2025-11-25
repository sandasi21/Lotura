package com.healthflow.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.healthflow.app.R
import com.healthflow.app.models.NotificationItem

/**
 * RecyclerView Adapter for displaying notifications
 */
class NotificationAdapter(
    private val notifications: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    /**
     * ViewHolder for notification items
     */
    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.notification_card)
        val txtIcon: TextView = itemView.findViewById(R.id.txt_notification_icon)
        val txtTitle: TextView = itemView.findViewById(R.id.txt_notification_title)
        val txtMessage: TextView = itemView.findViewById(R.id.txt_notification_message)
        val txtTime: TextView = itemView.findViewById(R.id.txt_notification_time)
        val unreadIndicator: View = itemView.findViewById(R.id.unread_indicator)

        fun bind(notification: NotificationItem) {
            txtIcon.text = notification.icon
            txtTitle.text = notification.title
            txtMessage.text = notification.message
            txtTime.text = notification.time

            // Show unread indicator
            unreadIndicator.visibility = if (notification.isRead) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Different background for unread
            if (!notification.isRead) {
                cardView.setCardBackgroundColor(
                    itemView.context.getColor(R.color.accent_lavender)
                )
            } else {
                cardView.setCardBackgroundColor(
                    itemView.context.getColor(R.color.white)
                )
            }

            itemView.setOnClickListener {
                notification.isRead = true
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}