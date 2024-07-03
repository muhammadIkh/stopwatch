package com.stopwatch.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stopwatch.myapplication.databinding.CounterBinding

class CounterActivity : AppCompatActivity() {

    private lateinit var binding: CounterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views or start countdown logic if needed
    }
}
