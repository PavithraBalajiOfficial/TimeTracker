package com.example.timetracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timetracker.databinding.ActivityEditorBinding
import com.example.timetracker.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*


class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding
    private lateinit var viewModel: SharedViewModel
    private var newNote: Boolean = false
    private var editing: Boolean = false
    private lateinit var activityDetailText: TextView
    private lateinit var trackingDateText: TextView
    private lateinit var trackingTimeText: TextView

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_check)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        trackingDateText = findViewById<TextView>(R.id.trackingDate)
        trackingTimeText = findViewById<TextView>(R.id.trackingTime)
        activityDetailText = findViewById<TextView>(R.id.timeDetails)

        trackingDateOnClick()
        trackingTimeOnClick()

        /* if (savedInstanceState != null) {
             editing = savedInstanceState.getBoolean(EDITIMG_KEY)
         }*/
        initViewModel()
    }

    private fun trackingTimeOnClick() {
        val timeSetLstener =
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "hh:mm a"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                trackingTimeText.text = sdf.format(cal.time)
            }
        trackingTimeText.setOnClickListener {
            TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, // Theme
                timeSetLstener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun trackingDateOnClick() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM/dd/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                trackingDateText.text = sdf.format(cal.time)

            }

        trackingDateText.setOnClickListener {
            DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, // Theme
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.selectedTrackerData.observe(this, {
            if (it.activityDetails != null) {
                activityDetailText.text = it.activityDetails
                trackingDateText.text = it.trackingDate
                trackingTimeText.text = it.trackingTime
            }
        })
        val extras = intent.extras
        if (extras == null) {
            title = getString(R.string.NEW_ACTIVITY)
            newNote = true
        } else {
            title = getString(R.string.EDIT_ACTIVITY)
            val noteId = extras.getInt(TRACKER_ID_KEY)
            viewModel.loadActivity(noteId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!newNote) {
            menuInflater.inflate(R.menu.menu_editor, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            saveAndReturn()
            return true
        } else if (item.getItemId() == R.id.action_delete) {
            viewModel.deleteActivity()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveAndReturn() {
        val trackerDate = findViewById<TextView>(R.id.trackingDate).text.trim().toString()
        val timeDetails = findViewById<TextView>(R.id.timeDetails).text.trim().toString()
        val trackingTime = findViewById<TextView>(R.id.trackingTime).text.trim().toString()
        if (timeDetails.isNotBlank() and trackerDate.isNotBlank()) {
            viewModel.addDetailsToTracker(trackerDate, trackingTime, timeDetails)
        }
        finish()
    }
}