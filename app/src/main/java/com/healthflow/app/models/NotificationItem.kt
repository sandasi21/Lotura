package com.healthflow.app.models

data class NotificationItem(
    val id: String,
    val icon: String,
    val title: String,
    val message: String,
    val time: String,
    val timestamp: Long,
    var isRead: Boolean = false
)