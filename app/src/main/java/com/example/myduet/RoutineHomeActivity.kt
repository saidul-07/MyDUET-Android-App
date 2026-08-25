package com.example.myduet

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myduet.adapters.RoutineAdapter
import com.example.myduet.databinding.ActivityRoutineHomeBinding
import com.example.myduet.databinding.BottomSheetEditProfileBinding
import com.example.myduet.databinding.DialogSetupProfileBinding
import com.example.myduet.models.RoutineClass
import com.example.myduet.repositories.RoutineRepository
import com.example.myduet.storage.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RoutineHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutineHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    private val repository = RoutineRepository()
    private lateinit var adapter: RoutineAdapter

    private val depts = arrayOf("CSE", "EEE", "CE", "ME", "TE", "IPE", "ARCH", "FE")
    private val years = arrayOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    private val sections = arrayOf("Section A", "Section B")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.parseColor("#444A72")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        preferenceManager = PreferenceManager(this)
        
        adapter = RoutineAdapter(ArrayList())
        binding.rvTodayRoutine.layoutManager = LinearLayoutManager(this)
        binding.rvTodayRoutine.adapter = adapter

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupDaySelector()
        checkProfile()

        binding.btnChangeProfile.setOnClickListener { showEditProfileBottomSheet() }
        binding.btnViewWeekly.setOnClickListener {
            startActivity(Intent(this, WeeklyRoutineActivity::class.java))
        }
    }

    private fun setupDaySelector() {
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
    }

    private fun checkProfile() {
        lifecycleScope.launch {
            val dept = preferenceManager.department.first()
            if (dept == null) {
                showSetupProfileDialog()
            } else {
                val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
                val today = sdfDay.format(Date())
                
                // Select today in chip group if it's a weekday, else select Sunday
                val chipId = when (today) {
                    "Sunday" -> R.id.chipSunday
                    "Monday" -> R.id.chipMonday
                    "Tuesday" -> R.id.chipTuesday
                    "Wednesday" -> R.id.chipWednesday
                    "Thursday" -> R.id.chipThursday
                    else -> R.id.chipSunday
                }
                binding.chipGroupDays.check(chipId)
                loadRoutineForDay(if (today in arrayOf("Friday", "Saturday")) "Sunday" else today)
            }
        }
    }

    private fun showSetupProfileDialog() {
        val dialogBinding = DialogSetupProfileBinding.inflate(layoutInflater)
        dialogBinding.spinnerDept.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, depts)
        dialogBinding.spinnerYear.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
        dialogBinding.spinnerSection.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sections)

        AlertDialog.Builder(this)
            .setTitle("Academic Profile")
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                val selectedDept = depts[dialogBinding.spinnerDept.selectedItemPosition]
                val selectedYear = years[dialogBinding.spinnerYear.selectedItemPosition]
                val selectedSection = sections[dialogBinding.spinnerSection.selectedItemPosition]
                lifecycleScope.launch {
                    preferenceManager.saveAcademicProfile(selectedDept, selectedYear, selectedSection)
                    checkProfile()
                }
            }.show()
    }

    private fun showEditProfileBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_edit_profile, null)
        dialog.setContentView(sheetView)
        val editBinding = BottomSheetEditProfileBinding.bind(sheetView)

        editBinding.autoDept.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, depts))
        editBinding.autoYear.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, years))
        editBinding.autoSection.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, sections))

        lifecycleScope.launch {
            editBinding.autoDept.setText(preferenceManager.department.first(), false)
            editBinding.autoYear.setText(preferenceManager.year.first(), false)
            editBinding.autoSection.setText(preferenceManager.section.first(), false)
        }

        editBinding.btnSaveProfile.setOnClickListener {
            val d = editBinding.autoDept.text.toString()
            val y = editBinding.autoYear.text.toString()
            val s = editBinding.autoSection.text.toString()
            if (d.isNotEmpty() && y.isNotEmpty() && s.isNotEmpty()) {
                lifecycleScope.launch {
                    preferenceManager.saveAcademicProfile(d, y, s)
                    loadRoutineForDay(getCurrentSelectedDay())
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun getCurrentSelectedDay(): String {
        return when (binding.chipGroupDays.checkedChipId) {
            R.id.chipSunday -> "Sunday"
            R.id.chipMonday -> "Monday"
            R.id.chipTuesday -> "Tuesday"
            R.id.chipWednesday -> "Wednesday"
            R.id.chipThursday -> "Thursday"
            else -> "Sunday"
        }
    }

    private fun loadRoutineForDay(day: String) {
        lifecycleScope.launch {
            val dept = preferenceManager.department.first() ?: "CSE"
            val year = preferenceManager.year.first() ?: "3rd Year"
            val section = preferenceManager.section.first() ?: "Section A"

            // Header Update
            binding.tvHeaderDept.text = dept
            binding.tvHeaderYearSection.text = getString(R.string.routine_header_info, year, section)
            binding.tvCurrentDay.text = day
            
            val calendar = Calendar.getInstance()
            val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
            
            // Adjust calendar to show the date of the selected day
            val todayName = sdfDay.format(Date())
            val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val targetDayIndex = daysOfWeek.indexOf(day)
            val todayIndex = daysOfWeek.indexOf(todayName)
            
            if (targetDayIndex != -1 && todayIndex != -1) {
                calendar.add(Calendar.DAY_OF_YEAR, targetDayIndex - todayIndex)
            }
            
            val sdfDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvCurrentDate.text = sdfDate.format(calendar.time)

            val routine = repository.getRoutine(this@RoutineHomeActivity, dept, year, section, day)
            
            updateUI(routine, day)
        }
    }

    private fun updateUI(routine: List<RoutineClass>, selectedDay: String) {
        val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
        val isToday = selectedDay.equals(sdfDay.format(Date()), ignoreCase = true)
        
        TransitionManager.beginDelayedTransition(binding.root as ViewGroup, Fade())

        binding.tvTodayHeader.text = if (isToday) {
            getString(R.string.routine_title_today)
        } else {
            getString(R.string.routine_title_day, selectedDay)
        }
        
        if (routine.isEmpty()) {
            binding.rvTodayRoutine.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.tvClassCount.text = getString(R.string.routine_no_classes)
        } else {
            binding.rvTodayRoutine.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            binding.tvClassCount.text = getString(R.string.routine_class_count, routine.size)
            
            adapter.updateData(routine)
            binding.rvTodayRoutine.scheduleLayoutAnimation()
        }
    }
}