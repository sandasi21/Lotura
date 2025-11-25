package com.healthflow.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.healthflow.app.utils.DataManager

class SplashActivity : AppCompatActivity() {

    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        dataManager = DataManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    //Determine where to navigate based on user state

    private fun navigateToNextScreen() {
//        val intent = when {
//            // First time user - show onboarding
//            !dataManager.hasCompletedOnboarding() -> {
//                Intent(this, OnboardingActivity::class.java)
//            }
//            // User not logged in - show login
//            !dataManager.isLoggedIn() -> {
//                Intent(this, LoginActivity::class.java)
//            }
//            // User logged in - go to main app
//            else -> {
//                Intent(this, MainActivity::class.java)
//            }
//        }
        startActivity(Intent(this, OnboardingActivity::class.java))


        startActivity(intent)
        finish() // Close splash so user can't go back to it
    }
}