package com.healthflow.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.healthflow.app.adapters.NotificationAdapter
import com.healthflow.app.models.NotificationItem
import com.healthflow.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*


// * Display app notifications and reminders

class NotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var dataManager: DataManager
    private lateinit var emptyStateLayout: LinearLayout  // ← CHANGED FROM TextView
    private val notifications = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notifications"

        dataManager = DataManager(this)

        recyclerView = findViewById(R.id.notifications_recycler_view)
        emptyStateLayout = findViewById(R.id.txt_no_notifications)  // ← FIXED!

        setupRecyclerView()

        loadNotifications()
    }


    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = notificationAdapter
    }


    private fun loadNotifications() {
        notifications.clear()

        generateNotifications()

        if (notifications.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            notificationAdapter.notifyDataSetChanged()
        }
    }

//     * Generate notifications based on user activity
    private fun generateNotifications() {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Check if hydration reminder is enabled
        if (dataManager.isHydrationReminderEnabled()) {
            notifications.add(
                NotificationItem(
                    id = "1",
                    icon = "💧",
                    title = "Hydration Reminder",
                    message = "Time to drink water! Stay healthy and hydrated.",
                    time = sdf.format(Date()),
                    timestamp = currentTime,
                    isRead = false
                )
            )
        }

        // Check incomplete habits
        val habits = dataManager.getHabits()
        val incompleteCount = habits.count { !it.completed }
        if (incompleteCount > 0) {
            notifications.add(
                NotificationItem(
                    id = "2",
                    icon = "✅",
                    title = "Complete Your Habits",
                    message = "You have $incompleteCount habits left to complete today!",
                    time = sdf.format(Date(currentTime - 3600000)), // 1 hour ago
                    timestamp = currentTime - 3600000,
                    isRead = false
                )
            )
        }

        // Mood reminder
        val moodEntries = dataManager.getMoodEntries()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val todayMoods = moodEntries.filter { it.timestamp >= todayStart }
        if (todayMoods.isEmpty()) {
            notifications.add(
                NotificationItem(
                    id = "3",
                    icon = "😊",
                    title = "Log Your Mood",
                    message = "How are you feeling today? Take a moment to reflect.",
                    time = sdf.format(Date(currentTime - 7200000)), // 2 hours ago
                    timestamp = currentTime - 7200000,
                    isRead = false
                )
            )
        }

        // Motivational notification
        notifications.add(
            NotificationItem(
                id = "4",
                icon = "🌟",
                title = "You're Doing Great!",
                message = "Keep up the amazing work on your wellness journey!",
                time = "Yesterday",
                timestamp = currentTime - 86400000,
                isRead = true
            )
        )

        // Step goal notification
        val steps = dataManager.getStepCount()
        if (steps > 5000 && steps < 8000) {
            notifications.add(
                NotificationItem(
                    id = "5",
                    icon = "👟",
                    title = "Almost There!",
                    message = "You're ${8000 - steps} steps away from your daily goal!",
                    time = sdf.format(Date(currentTime - 1800000)), // 30 min ago
                    timestamp = currentTime - 1800000,
                    isRead = false
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}