package com.healthflow.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.healthflow.app.adapters.OnboardingAdapter
import com.healthflow.app.models.OnboardingItem
import com.healthflow.app.utils.DataManager

/**
 * OnboardingActivity - Shows 3 onboarding screens
 * Appears only on first launch
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var dataManager: DataManager

    private val onboardingItems = listOf(
        OnboardingItem(
            title = "Track Your Habits",
            description = "Build healthy routines and track your daily progress with ease",
            image = R.drawable.onboarding_habits
        ),
        OnboardingItem(
            title = "Journal Your Mood",
            description = "Monitor your emotional wellness with our intuitive mood tracker",
            image = R.drawable.onboarding_mood
        ),
        OnboardingItem(
            title = "Stay Healthy",
            description = "Get Hydration reminders, count steps, and visualize your wellness journey",
            image = R.drawable.violethealth
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        supportActionBar?.hide()

        dataManager = DataManager(this)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btn_next)
        btnSkip = findViewById(R.id.btn_skip)
        indicatorLayout = findViewById(R.id.indicator_layout)

        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        setupIndicators()
        setCurrentIndicator(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

                if (position == onboardingItems.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

//dots
    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingItems.size)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageResource(R.drawable.indicator_inactive)
            indicators[i]?.layoutParams = layoutParams
            indicatorLayout.addView(indicators[i])
        }
    }


    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageResource(R.drawable.indicator_active)
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive)
            }
        }
    }

    private fun finishOnboarding() {
        dataManager.setOnboardingCompleted(true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}