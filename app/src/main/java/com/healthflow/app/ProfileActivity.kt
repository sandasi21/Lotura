package com.healthflow.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.healthflow.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var dataManager: DataManager
    private lateinit var txtUserName: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtMemberSince: TextView
    private lateinit var txtTotalHabits: TextView
    private lateinit var txtTotalMoods: TextView
    private lateinit var txtTotalSteps: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var imgProfileAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        dataManager = DataManager(this)

        initializeViews()

        loadProfileData()

        setupClickListeners()
    }


    private fun initializeViews() {
        txtUserName = findViewById(R.id.txt_profile_name)
        txtUserEmail = findViewById(R.id.txt_profile_email)
        txtMemberSince = findViewById(R.id.txt_member_since)
        txtTotalHabits = findViewById(R.id.txt_total_habits)
        txtTotalMoods = findViewById(R.id.txt_total_moods)
        txtTotalSteps = findViewById(R.id.txt_total_steps)
        btnEditProfile = findViewById(R.id.btn_edit_profile)
        btnLogout = findViewById(R.id.btn_logout)
        imgProfileAvatar = findViewById(R.id.img_profile_avatar)
    }

    private fun loadProfileData() {

        val userName = dataManager.getUserName()
        val userEmail = dataManager.getUserEmail()
        val memberSince = dataManager.getMemberSinceDate()

        txtUserName.text = userName
        txtUserEmail.text = userEmail
        txtMemberSince.text = "Member since $memberSince"

        val habits = dataManager.getHabits()
        val moodEntries = dataManager.getMoodEntries()
        val totalSteps = dataManager.getTotalLifetimeSteps()

        txtTotalHabits.text = "${habits.size}\n✔️Habits Created"
        txtTotalMoods.text = "${moodEntries.size}\n😊Mood Entries"
        txtTotalSteps.text = "$totalSteps\n👟Total Steps"

        setProfileAvatarColor(userName)
    }


    private fun setProfileAvatarColor(name: String) {
        val colors = listOf(
            R.color.primary_violet,
            R.color.primary_violet_light,
            R.color.accent_purple,
            R.color.primary_violet_dark
        )

        val colorIndex = if (name.isNotEmpty()) {
            name.first().code % colors.size
        } else {
            0
        }

        imgProfileAvatar.setBackgroundColor(getColor(colors[colorIndex]))
    }


    private fun setupClickListeners() {

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        imgProfileAvatar.setOnClickListener {
            showAvatarOptions()
        }
    }

    // edit profile

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val editName = dialogView.findViewById<EditText>(R.id.edit_profile_name)
        val editEmail = dialogView.findViewById<EditText>(R.id.edit_profile_email)

        // Pre fill
        editName.setText(dataManager.getUserName())
        editEmail.setText(dataManager.getUserEmail())

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = editName.text.toString().trim()
                val newEmail = editEmail.text.toString().trim()

                if (newName.isNotEmpty()) {
                    dataManager.saveUserName(newName)
                    dataManager.saveUserEmail(newEmail)
                    loadProfileData()
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showAvatarOptions() {
        val options = arrayOf(
            "😊 Happy", "🌟 Star", "🎯 Target",
            "💜 Heart", "🚀 Rocket", "🌈 Rainbow"
        )

        AlertDialog.Builder(this)
            .setTitle("Choose Avatar")
            .setItems(options) { _, which ->
                val emoji = options[which].split(" ")[0]
                dataManager.saveUserAvatar(emoji)
                findViewById<TextView>(R.id.txt_avatar_emoji).text = emoji
                Toast.makeText(this, "Avatar updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

//log out confirmation
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout? Your data will be preserved.")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .setIcon(R.drawable.ic_logout)
            .show()
    }


    /**
     * Perform logout action
     */
    private fun performLogout() {
        // Clear user session but keep user data
        dataManager.setLoggedIn(false)
        dataManager.setActiveUserEmail("")

        // Show toast message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to Login Activity and clear back stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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