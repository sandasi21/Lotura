package com.healthflow.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.healthflow.app.R
import com.healthflow.app.models.Habit

// Handles habit item display and user interactions
class HabitAdapter(
    private val habits: List<Habit>,
    private val onItemClick: (Habit) -> Unit,
    private val onCheckChanged: (Habit, Boolean) -> Unit,
    private val onDeleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {


//   ViewHolder for habit items

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHabitName: TextView = itemView.findViewById(R.id.txt_habit_name)
        val txtHabitTarget: TextView = itemView.findViewById(R.id.txt_habit_target)
        val checkboxCompleted: CheckBox = itemView.findViewById(R.id.checkbox_completed)
        val progressBar: ProgressBar = itemView.findViewById(R.id.habit_progress)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_habit)

        fun bind(habit: Habit) {
            txtHabitName.text = habit.name
            txtHabitTarget.text = "${habit.currentProgress} / ${habit.target} ${habit.unit}"
            checkboxCompleted.isChecked = habit.completed

            // Calculate progress percentage
            val progress = if (habit.target > 0) {
                (habit.currentProgress * 100) / habit.target
            } else {
                0
            }
            progressBar.progress = progress

            checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                onCheckChanged(habit, isChecked)
            }

            itemView.setOnClickListener {
                onItemClick(habit)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(habit)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size
}