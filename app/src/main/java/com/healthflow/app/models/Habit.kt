package com.healthflow.app.models

//  daily habit Data class
data class Habit(
    val id: String,
    var name: String,
    var target: Int,
    var unit: String,
    var currentProgress: Int = 0,
    var completed: Boolean = false,
    var lastUpdated: String = ""
)
