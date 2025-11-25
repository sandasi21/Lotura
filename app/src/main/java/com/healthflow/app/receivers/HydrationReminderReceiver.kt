package com.healthflow.app.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.healthflow.app.MainActivity
import com.healthflow.app.R
import com.healthflow.app.utils.HydrationReminderScheduler

/**
 * BroadcastReceiver for handling hydration reminder alarms
 * Displays notification when alarm triggers
 */
class HydrationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            showHydrationNotification(it)
        }
    }

    /**
     * Display hydration reminder notification
     */
    private fun showHydrationNotification(context: Context) {
        // Create intent to open app when notification is tapped
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, HydrationReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Don't forget to drink water and stay healthy!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(HydrationReminderScheduler.NOTIFICATION_ID, notification)
    }
}