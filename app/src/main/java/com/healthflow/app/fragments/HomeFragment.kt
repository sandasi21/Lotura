package com.healthflow.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.healthflow.app.MainActivity
import com.healthflow.app.MoodChartActivity
import com.healthflow.app.NotificationsActivity
import com.healthflow.app.ProfileActivity
import com.healthflow.app.R
import com.healthflow.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*


 // HomeFragment - First screen

class HomeFragment : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var txtWelcome: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtQuote: TextView
    private lateinit var txtHabitsSummary: TextView
    private lateinit var txtMoodSummary: TextView
    private lateinit var txtStepsSummary: TextView
    private lateinit var cardHabits: CardView
    private lateinit var cardMood: CardView
    private lateinit var cardSteps: CardView
    private lateinit var quickActionsLayout: LinearLayout

    // Motivational quotes
    private val quotes = listOf(
        "Every day is a fresh start! 🌅",
        "Small steps lead to big changes! 💪",
        "You're doing amazing! ✨",
        "Progress over perfection! 🎯",
        "Believe in yourself! 🌟",
        "Keep going, you've got this! 🚀",
        "Today is full of possibilities! 🌈",
        "Your wellness journey matters! 💜"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        dataManager = DataManager(requireContext())

        initializeViews(view)

        updateWelcomeMessage()
        updateDailySummary()
        displayRandomQuote()

        setupClickListeners()

        animateCards()

        return view
    }

    private fun initializeViews(view: View) {
        txtWelcome = view.findViewById(R.id.txt_welcome)
        txtDate = view.findViewById(R.id.txt_date)
        txtQuote = view.findViewById(R.id.txt_quote)
        txtHabitsSummary = view.findViewById(R.id.txt_habits_summary)
        txtMoodSummary = view.findViewById(R.id.txt_mood_summary)
        txtStepsSummary = view.findViewById(R.id.txt_steps_summary)
        cardHabits = view.findViewById(R.id.card_habits_summary)
        cardMood = view.findViewById(R.id.card_mood_summary)
        cardSteps = view.findViewById(R.id.card_steps_summary)
        quickActionsLayout = view.findViewById(R.id.quick_actions_layout)
//        btnNotifications = view.findViewById(R.id.btn_notifications)
//        btnProfile = view.findViewById(R.id.btn_profile)
    }

    private fun updateWelcomeMessage() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..11 -> "Good Morning! ☀️"
            in 12..16 -> "Good Afternoon! 🌤️"
            in 17..20 -> "Good Evening! 🌆"
            else -> "Good Night! 🌙"
        }

        txtWelcome.text = greeting

        // Display current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        txtDate.text = dateFormat.format(Date())
    }

    private fun displayRandomQuote() {
        val randomQuote = quotes.random()
        txtQuote.text = randomQuote
    }


    private fun updateDailySummary() {
        // Habits Summary
        val habits = dataManager.getHabits()
        val completedHabits = habits.count { it.completed }
        val totalHabits = habits.size
        val habitPercentage = if (totalHabits > 0) {
            (completedHabits * 100) / totalHabits
        } else {
            0
        }

        txtHabitsSummary.text = buildString {
            appendLine("✅ $completedHabits of $totalHabits completed")
            append("$habitPercentage% progress today")
        }

        // Mood Summary
        val moodEntries = dataManager.getMoodEntries()
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        val todayMoods = moodEntries.filter { it.timestamp >= todayStart }

        txtMoodSummary.text = if (todayMoods.isNotEmpty()) {
            val latestMood = todayMoods.first()
            buildString {
                appendLine("${latestMood.emoji} Feeling ${latestMood.moodName}")
                append("${todayMoods.size} entries today")
            }
        } else {
            "📝 No mood logged yet\nTap to add your first entry!"
        }

        // Steps Summary
        val steps = dataManager.getStepCount()
        val stepGoal = 8000 // Default goal
        val stepPercentage = ((steps.toFloat() / stepGoal) * 100).toInt().coerceAtMost(100)

        txtStepsSummary.text = buildString {
            appendLine("👟 $steps steps")
            append("$stepPercentage% of daily goal")
        }
    }

    private fun setupClickListeners() {
        // navigate to habits tab
        cardHabits.setOnClickListener {
            navigateToTab(1)
        }

        // navigate to mood journal tab
        cardMood.setOnClickListener {
            navigateToTab(2)
        }

        // navigate to settings tab
        cardSteps.setOnClickListener {
            navigateToTab(3)
        }

        // Quick action buttons
        val btnAddHabit = view?.findViewById<CardView>(R.id.btn_quick_add_habit)
        btnAddHabit?.setOnClickListener {
            navigateToTab(1) // Go to habits
        }

        val btnAddMood = view?.findViewById<CardView>(R.id.btn_quick_add_mood)
        btnAddMood?.setOnClickListener {
            navigateToTab(2) // Go to mood journal
        }

        val btnViewChart = view?.findViewById<CardView>(R.id.btn_quick_view_chart)
        btnViewChart?.setOnClickListener {
            // Open mood chart
            val intent = Intent(requireContext(), MoodChartActivity::class.java)
            startActivity(intent)
        }

    }


    private fun navigateToTab(position: Int) {
        val mainActivity = activity as? MainActivity
        mainActivity?.navigateToTab(position)
    }


    private fun animateCards() {
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)

        cardHabits.startAnimation(slideUp)

        cardMood.postDelayed({
            cardMood.startAnimation(slideUp)
        }, 100)

        cardSteps.postDelayed({
            cardSteps.startAnimation(slideUp)
        }, 200)

        quickActionsLayout.postDelayed({
            quickActionsLayout.startAnimation(slideUp)
        }, 300)
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to home
        updateDailySummary()
        displayRandomQuote()
    }
}