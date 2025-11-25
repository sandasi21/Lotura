package com.healthflow.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.healthflow.app.fragments.HabitsFragment
import com.healthflow.app.fragments.HomeFragment
import com.healthflow.app.fragments.MoodJournalFragment
import com.healthflow.app.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        // default fragment on first launch - HOME
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_habits -> {
                    loadFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    loadFragment(MoodJournalFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                // Open Notifications Activity
                val intent = Intent(this, NotificationsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_profile -> {
                // Open Profile Activity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Load a fragment into the container
     * @param fragment - The fragment to be loaded
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Public method to navigate to specific tab
     * Called from HomeFragment when cards are clicked
     * @param position Position in bottom navigation (0=Home, 1=Habits, 2=Mood, 3=Settings)
     */
    fun navigateToTab(position: Int) {
        when (position) {
            0 -> bottomNav.selectedItemId = R.id.nav_home
            1 -> bottomNav.selectedItemId = R.id.nav_habits
            2 -> bottomNav.selectedItemId = R.id.nav_mood
            3 -> bottomNav.selectedItemId = R.id.nav_settings
        }
    }
}