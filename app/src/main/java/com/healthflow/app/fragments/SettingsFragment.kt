package com.healthflow.app.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.healthflow.app.R
import com.healthflow.app.utils.DataManager
import com.healthflow.app.utils.HydrationReminderScheduler
import java.util.*
import kotlin.math.sqrt

/**
 * Fragment for app settings
 * Manages hydration reminders, step counter, and other preferences
 */
class SettingsFragment : Fragment(), SensorEventListener {

    private lateinit var switchReminder: SwitchCompat
    private lateinit var spinnerInterval: Spinner
    private lateinit var btnSetTime: Button
    private lateinit var switchStepCounter: SwitchCompat
    private lateinit var txtStepCount: TextView
    private lateinit var btnResetSteps: Button
    private lateinit var dataManager: DataManager
    private lateinit var reminderScheduler: HydrationReminderScheduler

    // Step counter variables
    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var stepCount = 0
    private var lastStepCount = 0

    // Shake detection variables
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val SHAKE_THRESHOLD = 800

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize DataManager and ReminderScheduler
        dataManager = DataManager(requireContext())
        reminderScheduler = HydrationReminderScheduler(requireContext())

        // Initialize views
        switchReminder = view.findViewById(R.id.switch_hydration_reminder)
        spinnerInterval = view.findViewById(R.id.spinner_reminder_interval)
        btnSetTime = view.findViewById(R.id.btn_set_reminder_time)
        switchStepCounter = view.findViewById(R.id.switch_step_counter)
        txtStepCount = view.findViewById(R.id.txt_step_count)
        btnResetSteps = view.findViewById(R.id.btn_reset_steps)

        setupIntervalSpinner()

        loadSettings()

        setupListeners()

        setupStepCounter()

        return view
    }


    private fun setupIntervalSpinner() {
        val intervals = arrayOf("30 minutes", "1 hour", "2 hours", "3 hours", "4 hours")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, intervals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval.adapter = adapter
    }

//     * Load saved settings from SharedPreferences
    private fun loadSettings() {
        // Load hydration reminder settings
        val reminderEnabled = dataManager.isHydrationReminderEnabled()
        val intervalPosition = dataManager.getReminderInterval()

        switchReminder.isChecked = reminderEnabled
        spinnerInterval.setSelection(intervalPosition)
        spinnerInterval.isEnabled = reminderEnabled
        btnSetTime.isEnabled = reminderEnabled

        // Load step counter settings
        val stepCounterEnabled = dataManager.isStepCounterEnabled()
        stepCount = dataManager.getStepCount()

        switchStepCounter.isChecked = stepCounterEnabled
        updateStepCountDisplay()
    }

    //click listeners
    private fun setupListeners() {
        // Hydration reminder toggle
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            spinnerInterval.isEnabled = isChecked
            btnSetTime.isEnabled = isChecked
            dataManager.setHydrationReminderEnabled(isChecked)

            if (isChecked) {
                scheduleReminder()
                Toast.makeText(context, "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
            } else {
                reminderScheduler.cancelReminders()
                Toast.makeText(context, "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Interval selection
        spinnerInterval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dataManager.setReminderInterval(position)
                if (switchReminder.isChecked) {
                    scheduleReminder()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set reminder time
        btnSetTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Step counter toggle
        switchStepCounter.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setStepCounterEnabled(isChecked)

            if (isChecked) {
                startStepCounting()
                Toast.makeText(context, "Step counter enabled", Toast.LENGTH_SHORT).show()
            } else {
                stopStepCounting()
                Toast.makeText(context, "Step counter disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Reset steps button
        btnResetSteps.setOnClickListener {
            stepCount = 0
            lastStepCount = 0
            dataManager.saveStepCount(0)
            updateStepCountDisplay()
            Toast.makeText(context, "Steps reset", Toast.LENGTH_SHORT).show()
        }
    }


//     * Show time picker dialog for reminder start time

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = dataManager.getReminderStartHour()
        val minute = dataManager.getReminderStartMinute()

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            dataManager.setReminderStartTime(selectedHour, selectedMinute)
            scheduleReminder()
            Toast.makeText(
                context,
                "Reminder will start at ${String.format("%02d:%02d", selectedHour, selectedMinute)}",
                Toast.LENGTH_SHORT
            ).show()
        }, hour, minute, false).show()
    }

//     * Schedule hydration reminder based on settings
    private fun scheduleReminder() {
        val intervalPosition = spinnerInterval.selectedItemPosition
        val intervalMinutes = when (intervalPosition) {
            0 -> 30
            1 -> 60
            2 -> 120
            3 -> 180
            4 -> 240
            else -> 60
        }

        reminderScheduler.scheduleReminder(intervalMinutes)
    }

//     * Setup step counter sensor
    private fun setupStepCounter() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // Fallback to accelerometer if step counter not available
            stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            Toast.makeText(context, "Using accelerometer for step detection", Toast.LENGTH_SHORT).show()
        }

        if (dataManager.isStepCounterEnabled()) {
            startStepCounting()
        }
    }

//     * Start listening to step counter sensor
    private fun startStepCounting() {
        stepSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

//     * Stop listening to step counter sensor
    private fun stopStepCounting() {
        sensorManager?.unregisterListener(this)
    }

//     * Handle sensor changes
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    // Step counter sensor (hardware)
                    if (lastStepCount == 0) {
                        lastStepCount = it.values[0].toInt()
                    }
                    stepCount = it.values[0].toInt() - lastStepCount
                    dataManager.saveStepCount(stepCount)
                    updateStepCountDisplay()
                }
                Sensor.TYPE_ACCELEROMETER -> {
                    // Accelerometer-based step detection
                    detectShakeAndSteps(it)
                }
            }
        }
    }


//     * Detect steps using accelerometer (fallback method)

    private fun detectShakeAndSteps(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        if ((currentTime - lastUpdate) > 100) {
            val diffTime = currentTime - lastUpdate
            lastUpdate = currentTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val speed = sqrt(((x - lastX) * (x - lastX) +
                    (y - lastY) * (y - lastY) +
                    (z - lastZ) * (z - lastZ)).toDouble()) / diffTime * 10000

            if (speed > SHAKE_THRESHOLD) {
                // Detected movement - increment step
                stepCount++
                dataManager.saveStepCount(stepCount)
                updateStepCountDisplay()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

//     * Update step count display
    private fun updateStepCountDisplay() {
        txtStepCount.text = "$stepCount steps today"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used but required by interface
    }

    override fun onResume() {
        super.onResume()
        if (dataManager.isStepCounterEnabled()) {
            startStepCounting()
        }
    }

    override fun onPause() {
        super.onPause()
        stopStepCounting()
    }
}