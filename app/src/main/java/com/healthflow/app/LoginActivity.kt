package com.healthflow.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.healthflow.app.utils.DataManager

/**
 * LoginActivity - Handles user authentication
 * Supports both login and registration
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editName: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var txtToggleMode: TextView
    private lateinit var checkRememberMe: CheckBox
    private lateinit var dataManager: DataManager

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide action bar
        supportActionBar?.hide()

        dataManager = DataManager(this)

        // Initialize views
        editEmail = findViewById(R.id.edit_email)
        editPassword = findViewById(R.id.edit_password)
        editName = findViewById(R.id.edit_name)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_register)
        txtToggleMode = findViewById(R.id.txt_toggle_mode)
        checkRememberMe = findViewById(R.id.check_remember_me)

        // Set remember me if previously checked
        checkRememberMe.isChecked = dataManager.getRememberMe()

        // Login button click
        btnLogin.setOnClickListener {
            if (isLoginMode) {
                performLogin()
            } else {
                toggleToLoginMode()
            }
        }

        // Register button click
        btnRegister.setOnClickListener {
            if (isLoginMode) {
                toggleToRegisterMode()
            } else {
                performRegister()
            }
        }

        // Toggle text click
        txtToggleMode.setOnClickListener {
            if (isLoginMode) {
                toggleToRegisterMode()
            } else {
                toggleToLoginMode()
            }
        }

        // Set initial mode
        updateUIMode()
    }

    /**
     * Perform login authentication
     */
    private fun performLogin() {
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user exists
        val savedEmail = dataManager.getUserEmail()
        val savedPassword = dataManager.getUserPassword()

        if (email == savedEmail && password == savedPassword) {
            // Login successful
            dataManager.setLoggedIn(true)
            dataManager.setActiveUserEmail(email)
            dataManager.setRememberMe(checkRememberMe.isChecked)

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Navigate to main app
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Perform user registration
     */
    private fun performRegister() {
        val name = editName.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // Save user data
        dataManager.saveUserName(name)
        dataManager.saveUserEmail(email)
        dataManager.saveUserPassword(password)
        dataManager.setLoggedIn(true)
        dataManager.setActiveUserEmail(email)
        dataManager.setRememberMe(checkRememberMe.isChecked)

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

        // Navigate to main app
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * Switch to login mode
     */
    private fun toggleToLoginMode() {
        isLoginMode = true
        updateUIMode()
    }

    /**
     * Switch to register mode
     */
    private fun toggleToRegisterMode() {
        isLoginMode = false
        updateUIMode()
    }

    /**
     * Update UI based on current mode
     */
    private fun updateUIMode() {
        if (isLoginMode) {
            // Login mode
            editName.visibility = android.view.View.GONE
            btnLogin.text = "Login"
            btnRegister.text = "Register"
            txtToggleMode.text = "Don't have an account? Register"
            checkRememberMe.visibility = android.view.View.VISIBLE
        } else {
            // Register mode
            editName.visibility = android.view.View.VISIBLE
            btnLogin.text = "Back to Login"
            btnRegister.text = "Create Account"
            txtToggleMode.text = "Already have an account? Login"
            checkRememberMe.visibility = android.view.View.GONE
        }
    }

    override fun onBackPressed() {
        // Prevent going back
        finishAffinity()
    }
}