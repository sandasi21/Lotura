package com.healthflow.app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.healthflow.app.MoodChartActivity
import com.healthflow.app.R
import com.healthflow.app.adapters.MoodAdapter
import com.healthflow.app.models.MoodEntry
import com.healthflow.app.utils.DataManager
import com.healthflow.app.utils.ShakeDetector
import java.text.SimpleDateFormat
import java.util.*


class MoodJournalFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var fabAddMood: FloatingActionButton
    private lateinit var btnShare: ImageButton
    private lateinit var switchShake: SwitchCompat
    private lateinit var dataManager: DataManager
    private var moodEntries = mutableListOf<MoodEntry>()

    private var shakeDetector: ShakeDetector? = null

    private val moodEmojis = listOf(
        "😊" to "Happy",
        "😢" to "Sad",
        "😡" to "Angry",
        "😰" to "Anxious",
        "😌" to "Calm",
        "😴" to "Tired",
        "🤗" to "Grateful",
        "😎" to "Confident",
        "😔" to "Depressed",
        "🥰" to "Loved"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mood_journal, container, false)

        // Initialize DataManager
        dataManager = DataManager(requireContext())

        // Initialize views
        recyclerView = view.findViewById(R.id.mood_recycler_view)
        fabAddMood = view.findViewById(R.id.fab_add_mood)
        btnShare = view.findViewById(R.id.btn_share_mood)
        switchShake = view.findViewById(R.id.switch_shake_detection)

        // Initialize shake detector
        shakeDetector = ShakeDetector(requireContext()) {
            showQuickMoodDialog()
        }

        setupRecyclerView()

        loadMoodEntries()

        loadShakeSettings()

        fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }

        btnShare.setOnClickListener {
            shareMoodSummary()
        }

        val btnViewChart = view.findViewById<ImageButton>(R.id.btn_view_chart)
        btnViewChart.setOnClickListener {
            val intent = Intent(requireContext(), MoodChartActivity::class.java)
            startActivity(intent)
        }


        switchShake.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setShakeDetectionEnabled(isChecked)
            if (isChecked) {
                shakeDetector?.start()
                Toast.makeText(context, "Shake to add quick mood!", Toast.LENGTH_SHORT).show()
            } else {
                shakeDetector?.stop()
            }
        }

        return view
    }

//      Setup RecyclerView with adapter and layout manager

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            moodEntries,
            onDeleteClick = { entry -> deleteMoodEntry(entry) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = moodAdapter
    }

//     Load mood entries from SharedPreferences
    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(dataManager.getMoodEntries())
        // Sort by date descending (newest first)
        moodEntries.sortByDescending { it.timestamp }
        moodAdapter.notifyDataSetChanged()
    }

//     * Load shake detection settings
    private fun loadShakeSettings() {
        val shakeEnabled = dataManager.isShakeDetectionEnabled()
        switchShake.isChecked = shakeEnabled
        if (shakeEnabled) {
            shakeDetector?.start()
        }
    }

    /**
     * Show quick mood dialog (triggered by shake)
     * Only shows emoji selector without notes field
     */
    private fun showQuickMoodDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quick_mood, null)
        val emojiGrid = dialogView.findViewById<GridLayout>(R.id.emoji_grid)

        var selectedEmoji = ""
        var selectedMoodName = ""

        // Create emoji buttons
        moodEmojis.forEach { (emoji, name) ->
            val button = Button(requireContext()).apply {
                text = emoji
                textSize = 32f
                setBackgroundResource(R.drawable.emoji_selector)
                setPadding(16, 16, 16, 16)

                setOnClickListener {
                    // Reset all buttons
                    for (i in 0 until emojiGrid.childCount) {
                        emojiGrid.getChildAt(i).isSelected = false
                    }
                    // Select this button
                    isSelected = true
                    selectedEmoji = emoji
                    selectedMoodName = name
                }
            }
            emojiGrid.addView(button)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Quick Mood - How are you feeling?")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                if (selectedEmoji.isNotEmpty()) {
                    val entry = MoodEntry(
                        id = UUID.randomUUID().toString(),
                        emoji = selectedEmoji,
                        moodName = selectedMoodName,
                        note = "", // No note in quick mode
                        timestamp = System.currentTimeMillis(),
                        date = getCurrentDate(),
                        time = getCurrentTime()
                    )
                    addMoodEntry(entry)
                    Toast.makeText(context, "Quick mood logged!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

//  Show dialog to add a new mood entry with notes

    private fun showAddMoodDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_mood, null)
        val emojiGrid = dialogView.findViewById<GridLayout>(R.id.emoji_grid)
        val editNote = dialogView.findViewById<EditText>(R.id.edit_mood_note)

        var selectedEmoji = ""
        var selectedMoodName = ""

        // Create emoji buttons
        moodEmojis.forEach { (emoji, name) ->
            val button = Button(requireContext()).apply {
                text = emoji
                textSize = 32f
                setBackgroundResource(R.drawable.emoji_selector)
                setPadding(16, 16, 16, 16)

                setOnClickListener {
                    // Reset all buttons
                    for (i in 0 until emojiGrid.childCount) {
                        emojiGrid.getChildAt(i).isSelected = false
                    }
                    // Select this button
                    isSelected = true
                    selectedEmoji = emoji
                    selectedMoodName = name
                }
            }
            emojiGrid.addView(button)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                if (selectedEmoji.isNotEmpty()) {
                    val note = editNote.text.toString()
                    val entry = MoodEntry(
                        id = UUID.randomUUID().toString(),
                        emoji = selectedEmoji,
                        moodName = selectedMoodName,
                        note = note,
                        timestamp = System.currentTimeMillis(),
                        date = getCurrentDate(),
                        time = getCurrentTime()
                    )
                    addMoodEntry(entry)
                } else {
                    Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Add a new mood entry
     */
    private fun addMoodEntry(entry: MoodEntry) {
        moodEntries.add(0, entry) // Add at beginning
        dataManager.saveMoodEntries(moodEntries)
        moodAdapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        Toast.makeText(context, "Mood logged", Toast.LENGTH_SHORT).show()
    }

    /**
     * Delete a mood entry
     */
    private fun deleteMoodEntry(entry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                val position = moodEntries.indexOf(entry)
                moodEntries.remove(entry)
                dataManager.saveMoodEntries(moodEntries)
                moodAdapter.notifyItemRemoved(position)
                Toast.makeText(context, "Mood entry deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Share mood summary using implicit intent
     */
    private fun shareMoodSummary() {
        if (moodEntries.isEmpty()) {
            Toast.makeText(context, "No mood entries to share", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate mood statistics
        val moodCounts = moodEntries.groupingBy { it.moodName }.eachCount()
        val mostFrequent = moodCounts.maxByOrNull { it.value }?.key ?: "N/A"
        val totalEntries = moodEntries.size

        // Get last 7 days entries
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val recentEntries = moodEntries.filter { it.timestamp >= sevenDaysAgo }

        val summary = buildString {
            appendLine("🌟 My Mood Summary - HealthFlow")
            appendLine()
            appendLine("Total Entries: $totalEntries")
            appendLine("Most Frequent Mood: $mostFrequent")
            appendLine()
            appendLine("Last 7 Days:")
            recentEntries.take(5).forEach { entry ->
                appendLine("${entry.emoji} ${entry.moodName} - ${entry.date} ${entry.time}")
            }
            appendLine()
            appendLine("Track your wellness with HealthFlow!")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, summary)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
    }

     // Get current date as string
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }


//   Get current time as string

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onResume() {
        super.onResume()
        if (dataManager.isShakeDetectionEnabled()) {
            shakeDetector?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shakeDetector?.stop()
    }
}