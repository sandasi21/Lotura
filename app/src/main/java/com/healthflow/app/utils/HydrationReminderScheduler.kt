package com.healthflow.app.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.healthflow.app.R
import com.healthflow.app.receivers.HydrationReminderReceiver
import java.util.*

/**
 * Scheduler for hydration reminders using AlarmManager
 * Sends periodic notifications to remind users to drink water
 */
class HydrationReminderScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "hydration_reminder_channel"
        const val CHANNEL_NAME = "Hydration Reminders"
        const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE = 100
    }

    init {
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Reminders to drink water throughout the day"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

//     * Schedule repeating hydration reminders
//     * @param intervalMinutes Interval between reminders in minutes
    fun scheduleReminder(intervalMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate first trigger time
        val calendar = Calendar.getInstance().apply {
            val dataManager = DataManager(context)
            set(Calendar.HOUR_OF_DAY, dataManager.getReminderStartHour())
            set(Calendar.MINUTE, dataManager.getReminderStartMinute())
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Schedule repeating alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            (intervalMinutes * 60 * 1000).toLong(),
            pendingIntent
        )
    }

    /**
     * Cancel all scheduled hydration reminders
     */
    fun cancelReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}