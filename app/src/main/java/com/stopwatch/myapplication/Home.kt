package com.stopwatch.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stopwatch.myapplication.databinding.HomeBinding

class Home : AppCompatActivity(), ScheduleAdapter.OnItemClickListener {

    private lateinit var binding: HomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<Schedule>()
    private val REQUEST_CODE_TIME_SETUP = 1
    private var editingPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("TimerPrefs", MODE_PRIVATE)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleAdapter = ScheduleAdapter(scheduleList, this)
        binding.recyclerView.adapter = scheduleAdapter

        // Set up Add FAB
        val fabAdd: FloatingActionButton = findViewById(R.id.fab)
        fabAdd.setOnClickListener {
            val intent = Intent(this, TimeSetup::class.java)
            startActivityForResult(intent, REQUEST_CODE_TIME_SETUP)
        }

        // Set up Start FAB
        val startButton: FloatingActionButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this, CounterActivity::class.java)
            startActivity(intent)
        }

        // Display set time in RecyclerView
        displaySetTime()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TIME_SETUP && resultCode == RESULT_OK) {
            data?.let {
                val newTitle = it.getStringExtra("newTitle") ?: ""
                val newDescription = it.getStringExtra("newDescription") ?: ""
                val hours = it.getIntExtra("hours", 0)
                val minutes = it.getIntExtra("minutes", 0)
                val seconds = it.getIntExtra("seconds", 0)

                val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                val schedule = Schedule(newTitle, newDescription, time)

                editingPosition?.let { position ->
                    scheduleList[position] = schedule
                    editingPosition = null
                } ?: run {
                    scheduleList.add(schedule)
                }

                scheduleAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClick(position: Int) {
        editingPosition = position
        val schedule = scheduleList[position]
        val intent = Intent(this, TimeSetup::class.java).apply {
            putExtra("title", schedule.title)
            putExtra("description", schedule.description)
            putExtra("hours", sharedPreferences.getInt("hours", 0))
            putExtra("minutes", sharedPreferences.getInt("minutes", 0))
            putExtra("seconds", sharedPreferences.getInt("seconds", 0))
        }
        startActivityForResult(intent, REQUEST_CODE_TIME_SETUP)
    }

    private fun displaySetTime() {
        // Initial display logic if needed
    }
}
