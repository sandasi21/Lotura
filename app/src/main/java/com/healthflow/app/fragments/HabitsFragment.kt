package com.healthflow.app.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.healthflow.app.R
import com.healthflow.app.adapters.HabitAdapter
import com.healthflow.app.models.Habit
import com.healthflow.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

//Fragment managing daily habits add, edit, delete and track habits

class HabitsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var dataManager: DataManager
    private var habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habits, container, false)

        dataManager = DataManager(requireContext())

        recyclerView = view.findViewById(R.id.habits_recycler_view)
        fabAddHabit = view.findViewById(R.id.fab_add_habit)
        progressBar = view.findViewById(R.id.progress_bar)
        progressText = view.findViewById(R.id.progress_text)

        setupRecyclerView()

        loadHabits()

        // Setup FAB click listener
        fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }

        return view
    }

    /**
     * Setup RecyclerView with adapter and layout manager
     */
    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habits,
            onItemClick = { habit -> showEditHabitDialog(habit) },
            onCheckChanged = { habit, isChecked ->
                updateHabitCompletion(habit, isChecked)
            },
            onDeleteClick = { habit -> deleteHabit(habit) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = habitAdapter
    }

    /**
     * Load habits from SharedPreferences
     */
    private fun loadHabits() {
        habits.clear()
        habits.addAll(dataManager.getHabits())
        habitAdapter.notifyDataSetChanged()
        updateProgress()
    }

    /**
     * Show dialog - add a new habit
     */
    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val editHabitName = dialogView.findViewById<EditText>(R.id.edit_habit_name)
        val editHabitTarget = dialogView.findViewById<EditText>(R.id.edit_habit_target)
        val spinnerUnit = dialogView.findViewById<Spinner>(R.id.spinner_unit)

        // Setup spinner
        val units = arrayOf("times", "minutes", "glasses", "steps", "pages")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editHabitName.text.toString()
                val targetStr = editHabitTarget.text.toString()
                val unit = spinnerUnit.selectedItem.toString()

                if (name.isNotEmpty() && targetStr.isNotEmpty()) {
                    val target = targetStr.toIntOrNull() ?: 1
                    val habit = Habit(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        target = target,
                        unit = unit,
                        currentProgress = 0,
                        completed = false,
                        lastUpdated = getCurrentDate()
                    )
                    addHabit(habit)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show dialog - edit an existing habit
     */
    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val editHabitName = dialogView.findViewById<EditText>(R.id.edit_habit_name)
        val editHabitTarget = dialogView.findViewById<EditText>(R.id.edit_habit_target)
        val spinnerUnit = dialogView.findViewById<Spinner>(R.id.spinner_unit)

        // Pre fill existing data
        editHabitName.setText(habit.name)
        editHabitTarget.setText(habit.target.toString())

        // Setup spinner
        val units = arrayOf("times", "minutes", "glasses", "steps", "pages")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = adapter
        spinnerUnit.setSelection(units.indexOf(habit.unit))

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = editHabitName.text.toString()
                val targetStr = editHabitTarget.text.toString()
                val unit = spinnerUnit.selectedItem.toString()

                if (name.isNotEmpty() && targetStr.isNotEmpty()) {
                    val target = targetStr.toIntOrNull() ?: 1
                    habit.name = name
                    habit.target = target
                    habit.unit = unit
                    updateHabit(habit)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Add a new habit
     */
    private fun addHabit(habit: Habit) {
        habits.add(habit)
        dataManager.saveHabits(habits)
        habitAdapter.notifyItemInserted(habits.size - 1)
        updateProgress()
        Toast.makeText(context, "Habit added", Toast.LENGTH_SHORT).show()
    }

    /**
     * Update a habit
     */
    private fun updateHabit(habit: Habit) {
        dataManager.saveHabits(habits)
        habitAdapter.notifyDataSetChanged()
        updateProgress()
        Toast.makeText(context, "Habit updated", Toast.LENGTH_SHORT).show()
    }

    /**
     * Delete a habit
     */
    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete '${habit.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                val position = habits.indexOf(habit)
                habits.remove(habit)
                dataManager.saveHabits(habits)
                habitAdapter.notifyItemRemoved(position)
                updateProgress()
                Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Update habit completion status
     */
    private fun updateHabitCompletion(habit: Habit, isChecked: Boolean) {
        habit.completed = isChecked
        habit.currentProgress = if (isChecked) habit.target else 0
        habit.lastUpdated = getCurrentDate()
        dataManager.saveHabits(habits)
        updateProgress()
    }

    /**
     * Calculate and update overall progress
     */
    private fun updateProgress() {
        if (habits.isEmpty()) {
            progressBar.progress = 0
            progressText.text = "0% Complete"
            return
        }

        val completedCount = habits.count { it.completed }
        val percentage = (completedCount * 100) / habits.size
        progressBar.progress = percentage
        progressText.text = "$percentage% Complete"
    }

    /**
     * Get current date as string
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onResume() {
        super.onResume()
        // Reset habits if it's a new day
        checkAndResetDailyHabits()
    }

    /**
     * Check if it's a new day and reset habit completion
     */
    private fun checkAndResetDailyHabits() {
        val currentDate = getCurrentDate()
        val lastResetDate = dataManager.getLastResetDate()

        if (currentDate != lastResetDate) {
            // Reset all habits for the new day
            habits.forEach { habit ->
                habit.completed = false
                habit.currentProgress = 0
            }
            dataManager.saveHabits(habits)
            dataManager.saveLastResetDate(currentDate)
            habitAdapter.notifyDataSetChanged()
            updateProgress()
        }
    }
}