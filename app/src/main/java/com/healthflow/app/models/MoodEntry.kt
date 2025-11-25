package com.healthflow.app.models

data class MoodEntry(
    val id: String,
    val emoji: String,
    val moodName: String,
    val note: String,
    val timestamp: Long,
    val date: String,
    val time: String
)