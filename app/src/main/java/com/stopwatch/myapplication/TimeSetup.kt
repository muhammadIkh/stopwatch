package com.stopwatch.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.stopwatch.myapplication.databinding.TimeSetupBinding

class TimeSetup : AppCompatActivity() {

    private lateinit var binding: TimeSetupBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var timerRunning: Boolean = false
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE)

        // Initialize MediaPlayer with the sound resource
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)

        binding.hoursPicker.minValue = 0
        binding.hoursPicker.maxValue = 23

        binding.minutesPicker.minValue = 0
        binding.minutesPicker.maxValue = 59

        binding.secondsPicker.minValue = 0
        binding.secondsPicker.maxValue = 59

        // Get the data for editing
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val hours = intent.getIntExtra("hours", 0)
        val minutes = intent.getIntExtra("minutes", 0)
        val seconds = intent.getIntExtra("seconds", 0)

        binding.editTitle.setText(title)
        binding.editDescription.setText(description)
        binding.hoursPicker.value = hours
        binding.minutesPicker.value = minutes
        binding.secondsPicker.value = seconds

        binding.setTimeButton.setOnClickListener {
            setTime()
        }

        binding.startButton.setOnClickListener {
            if (!timerRunning && timeLeftInMillis > 0) {
                startTimer(timeLeftInMillis)
            }
        }

        binding.stopButton.setOnClickListener {
            stopTimer()
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

        binding.resumeButton.setOnClickListener {
            resumeTimer()
        }
    }

    private fun setTime() {
        val hours = binding.hoursPicker.value
        val minutes = binding.minutesPicker.value
        val seconds = binding.secondsPicker.value

        timeLeftInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
        updateTimer()

        // Save the time to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("hours", hours)
        editor.putInt("minutes", minutes)
        editor.putInt("seconds", seconds)
        editor.apply()

        // Save edited title and description
        val newTitle = binding.editTitle.text.toString()
        val newDescription = binding.editDescription.text.toString()

        // Create an intent to return data to Home activity
        val resultIntent = Intent()
        resultIntent.putExtra("newTitle", newTitle)
        resultIntent.putExtra("newDescription", newDescription)
        resultIntent.putExtra("hours", hours)
        resultIntent.putExtra("minutes", minutes)
        resultIntent.putExtra("seconds", seconds)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                playSound()
                timerRunning = false
            }
        }.start()
        timerRunning = true
    }

    private fun resumeTimer() {
        if (!timerRunning && timeLeftInMillis > 0) {
            startTimer(timeLeftInMillis)
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        timerRunning = false
    }

    private fun resetTimer() {
        stopTimer()
        timeLeftInMillis = 0
        updateTimer()
        binding.hoursPicker.value = 0
        binding.minutesPicker.value = 0
        binding.secondsPicker.value = 0
    }

    private fun updateTimer() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        binding.timerTextView.text = time
    }

    private fun playSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
