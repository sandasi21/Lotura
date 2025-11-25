package com.healthflow.app

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.healthflow.app.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * MoodChartActivity - Displays mood trends over the past week
 * Uses MPAndroidChart library to visualize mood patterns
 */
class MoodChartActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var dataManager: DataManager

    // Mood to numeric value mapping for chart
    private val moodValueMap = mapOf(
        "Happy" to 5f,
        "Loved" to 5f,
        "Grateful" to 4f,
        "Confident" to 4f,
        "Calm" to 3f,
        "Tired" to 2f,
        "Anxious" to 2f,
        "Sad" to 1f,
        "Angry" to 1f,
        "Depressed" to 1f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_chart)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mood Trends"

        dataManager = DataManager(this)
        lineChart = findViewById(R.id.mood_line_chart)

        setupChart()
        loadMoodData()
    }


    private fun setupChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            animateX(1000)

            // Configure X axis (dates)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = ContextCompat.getColor(this@MoodChartActivity, R.color.text_primary)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, -(6 - value.toInt()))
                        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                        return sdf.format(calendar.time)
                    }
                }
            }

            // Configure Y axis (mood values)
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(this@MoodChartActivity, R.color.accent_lavender)
                textColor = ContextCompat.getColor(this@MoodChartActivity, R.color.text_primary)
                axisMinimum = 0f
                axisMaximum = 6f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            5 -> "😊"
                            4 -> "🙂"
                            3 -> "😐"
                            2 -> "😟"
                            1 -> "😢"
                            else -> ""
                        }
                    }
                }
            }

            axisRight.isEnabled = false

            // Legend configuration
            legend.apply {
                isEnabled = true
                textColor = ContextCompat.getColor(this@MoodChartActivity, R.color.text_primary)
                textSize = 12f
            }
        }
    }

    /**
     * Load mood data for the past 7 days and populate chart
     */
    private fun loadMoodData() {
        val moodEntries = dataManager.getMoodEntries()
        val calendar = Calendar.getInstance()
        val entries = mutableListOf<Entry>()

        // Get data for last 7 days
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)

            val dayStart = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            val dayEnd = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            // Filter moods for this day
            val dayMoods = moodEntries.filter {
                it.timestamp in dayStart..dayEnd
            }

            // Calculate average mood value for the day
            val avgMoodValue = if (dayMoods.isNotEmpty()) {
                val sum = dayMoods.sumOf {
                    (moodValueMap[it.moodName] ?: 3f).toDouble()
                }
                (sum / dayMoods.size).toFloat()
            } else {
                0f // No mood logged for this day
            }

            entries.add(Entry((6 - i).toFloat(), avgMoodValue))
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "Mood Level").apply {
            color = ContextCompat.getColor(this@MoodChartActivity, R.color.primary_violet)
            setCircleColor(ContextCompat.getColor(this@MoodChartActivity, R.color.primary_violet))
            lineWidth = 3f
            circleRadius = 6f
            setDrawCircleHole(true)
            circleHoleColor = Color.WHITE
            valueTextSize = 0f // Hide values on points
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(this@MoodChartActivity, R.color.accent_lavender)
            fillAlpha = 100
            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line
        }

        // Set data to chart
        lineChart.data = LineData(dataSet)
        lineChart.invalidate() // Refresh chart
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