package com.healthflow.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.healthflow.app.R
import com.healthflow.app.models.MoodEntry

/**
 * RecyclerView Adapter for displaying mood journal entries
 * Shows emoji, mood name, date, time, and notes
 */
class MoodAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onDeleteClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    /**
     * ViewHolder for mood entry items
     */
    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtEmoji: TextView = itemView.findViewById(R.id.txt_mood_emoji)
        val txtMoodName: TextView = itemView.findViewById(R.id.txt_mood_name)
        val txtDate: TextView = itemView.findViewById(R.id.txt_mood_date)
        val txtTime: TextView = itemView.findViewById(R.id.txt_mood_time)
        val txtNote: TextView = itemView.findViewById(R.id.txt_mood_note)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_mood)

        fun bind(entry: MoodEntry) {
            txtEmoji.text = entry.emoji
            txtMoodName.text = entry.moodName
            txtDate.text = entry.date
            txtTime.text = entry.time

            // Show note if available
            if (entry.note.isNotEmpty()) {
                txtNote.visibility = View.VISIBLE
                txtNote.text = entry.note
            } else {
                txtNote.visibility = View.GONE
            }

            // Handle delete button click
            btnDelete.setOnClickListener {
                onDeleteClick(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount(): Int = moodEntries.size
}