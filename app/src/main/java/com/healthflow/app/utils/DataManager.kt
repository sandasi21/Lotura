package com.healthflow.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.healthflow.app.models.Habit
import com.healthflow.app.models.MoodEntry

/**
 * DataManager class handles all data persistence using SharedPreferences
 * Stores habits, mood entries, and app settings
 */
class DataManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "HealthFlowPrefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        private const val KEY_HYDRATION_ENABLED = "hydration_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_REMINDER_START_HOUR = "reminder_start_hour"
        private const val KEY_REMINDER_START_MINUTE = "reminder_start_minute"
        private const val KEY_STEP_COUNTER_ENABLED = "step_counter_enabled"
        private const val KEY_STEP_COUNT = "step_count"
        private const val KEY_SHAKE_DETECTION_ENABLED = "shake_detection_enabled"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_AVATAR = "user_avatar"
        private const val KEY_MEMBER_SINCE = "member_since"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOTAL_LIFETIME_STEPS = "total_lifetime_steps"

        //  NEW
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_ACTIVE_USER_EMAIL = "active_user_email"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    /**
     * Save list of habits to SharedPreferences
     */
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPreferences.edit().putString(KEY_HABITS, json).apply()
    }

    /**
     * Retrieve list of habits from SharedPreferences
     */
    fun getHabits(): List<Habit> {
        val json = sharedPreferences.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    /**
     * Save list of mood entries to SharedPreferences
     */
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        sharedPreferences.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }


    fun getMoodEntries(): List<MoodEntry> {
        val json = sharedPreferences.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveLastResetDate(date: String) {
        sharedPreferences.edit().putString(KEY_LAST_RESET_DATE, date).apply()
    }

    fun getLastResetDate(): String {
        return sharedPreferences.getString(KEY_LAST_RESET_DATE, "") ?: ""
    }


    fun setHydrationReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }

    fun isHydrationReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_HYDRATION_ENABLED, false)
    }

    fun setReminderInterval(position: Int) {
        sharedPreferences.edit().putInt(KEY_REMINDER_INTERVAL, position).apply()
    }

    fun getReminderInterval(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_INTERVAL, 1) // Default: 1 hour
    }

    fun setReminderStartTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_REMINDER_START_HOUR, hour)
            .putInt(KEY_REMINDER_START_MINUTE, minute)
            .apply()
    }

    fun getReminderStartHour(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_START_HOUR, 8) // Default: 8 AM
    }

    fun getReminderStartMinute(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_START_MINUTE, 0)
    }


    fun setStepCounterEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_STEP_COUNTER_ENABLED, enabled).apply()
    }

    fun isStepCounterEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_STEP_COUNTER_ENABLED, false)
    }

    fun saveStepCount(count: Int) {
        sharedPreferences.edit().putInt(KEY_STEP_COUNT, count).apply()
    }

    fun getStepCount(): Int {
        return sharedPreferences.getInt(KEY_STEP_COUNT, 0)
    }

    fun setShakeDetectionEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SHAKE_DETECTION_ENABLED, enabled).apply()
    }

    fun isShakeDetectionEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SHAKE_DETECTION_ENABLED, false)
    }

    // ========== Profile Management ==========


    fun saveUserName(name: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply()
    }


    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "Health Enthusiast") ?: "Health Enthusiast"
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "user@healthflow.com") ?: "user@healthflow.com"
    }

    fun saveUserAvatar(emoji: String) {
        sharedPreferences.edit().putString(KEY_USER_AVATAR, emoji).apply()
    }

    fun getUserAvatar(): String {
        return sharedPreferences.getString(KEY_USER_AVATAR, "😊") ?: "😊"
    }

    fun getMemberSinceDate(): String {
        val savedDate = sharedPreferences.getString(KEY_MEMBER_SINCE, null)
        if (savedDate == null) {
            // First time - save current date
            val sdf = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
            val currentDate = sdf.format(java.util.Date())
            sharedPreferences.edit().putString(KEY_MEMBER_SINCE, currentDate).apply()
            return currentDate
        }
        return savedDate
    }


    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, true) // Default true
    }

    fun getTotalLifetimeSteps(): Int {
        return sharedPreferences.getInt(KEY_TOTAL_LIFETIME_STEPS, 0)
    }

    fun addToLifetimeSteps(steps: Int) {
        val current = getTotalLifetimeSteps()
        sharedPreferences.edit().putInt(KEY_TOTAL_LIFETIME_STEPS, current + steps).apply()
    }

    // ========== Authentication Management ==========


    fun saveUserPassword(password: String) {
        sharedPreferences.edit().putString(KEY_USER_PASSWORD, password).apply()
    }

    fun getUserPassword(): String {
        return sharedPreferences.getString(KEY_USER_PASSWORD, "") ?: ""
    }


    fun setActiveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_ACTIVE_USER_EMAIL, email).apply()
    }


    fun getActiveUserEmail(): String {
        return sharedPreferences.getString(KEY_ACTIVE_USER_EMAIL, "") ?: ""
    }


    fun setRememberMe(remember: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
    }


    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }


    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    /**
     * Check if onboarding has been completed
     */
    fun hasCompletedOnboarding(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Check if this is first app launch
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        if (isFirst) {
            sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        }
        return isFirst
    }

    /**
     * Clear all user data (for logout)
     */
    fun clearUserData() {
        setLoggedIn(false)
        setActiveUserEmail("")
        setRememberMe(false)
    }
}