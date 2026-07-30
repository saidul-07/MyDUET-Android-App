package com.example.myduet

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myduet.adapters.RoutineAdapter
import com.example.myduet.databinding.ActivityWeeklyRoutineBinding
import com.example.myduet.repositories.RoutineRepository
import com.example.myduet.storage.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeeklyRoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyRoutineBinding
    private lateinit var preferenceManager: PreferenceManager
    private val repository = RoutineRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root as View)

        preferenceManager = PreferenceManager(this)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.chipGroupDays.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val day = when (checkedIds[0]) {
                    R.id.chipSunday -> "Sunday"
                    R.id.chipMonday -> "Monday"
                    R.id.chipTuesday -> "Tuesday"
                    R.id.chipWednesday -> "Wednesday"
                    R.id.chipThursday -> "Thursday"
                    else -> "Sunday"
                }
                loadRoutineForDay(day)
            }
        }

        // Default selection to today or Sunday if weekend
        val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
        val today = sdfDay.format(Date())
        
        val initialDay = when (today) {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday" -> today
            else -> "Sunday"
        }

        when (initialDay) {
            "Sunday" -> binding.chipSunday.isChecked = true
            "Monday" -> binding.chipMonday.isChecked = true
            "Tuesday" -> binding.chipTuesday.isChecked = true
            "Wednesday" -> binding.chipWednesday.isChecked = true
            "Thursday" -> binding.chipThursday.isChecked = true
        }
        
        loadRoutineForDay(initialDay)
    }

    private fun loadRoutineForDay(day: String) {
        lifecycleScope.launch {
            val dept = preferenceManager.department.first() ?: "CSE"
            val year = preferenceManager.year.first() ?: "3rd Year"
            val section = preferenceManager.section.first() ?: "Section A"

            val routine = repository.getRoutine(this@WeeklyRoutineActivity, dept, year, section, day)
            binding.rvWeeklyRoutine.layoutManager = LinearLayoutManager(this@WeeklyRoutineActivity)
            binding.rvWeeklyRoutine.adapter = RoutineAdapter(routine)
        }
    }
}