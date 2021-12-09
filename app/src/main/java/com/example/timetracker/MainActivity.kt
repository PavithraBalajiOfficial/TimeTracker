package com.example.timetracker

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.data.Tracker
import com.example.timetracker.databinding.ActivityMainBinding
import com.example.timetracker.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), MainRecyclerAdapter.EditItemListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        initRecyclerView()
        initViewModel()
        dateChangeButtonActions()
    }

    /*override fun onResume() {
        super.onResume()
        // Hack because MutableLiveData trackerListData does not update with insert/update/delete
        *//*if (viewModel != null && recyclerView != null) {
            var trackingListDate: String
            if (!(viewModel.trackerListData.value.isNullOrEmpty())) {
                trackingListDate = viewModel.trackerListData.value!!.get(0).trackingDate
                findViewById<Button>(R.id.pickDateButton).text = trackingListDate
                viewModel.loadActivityForDate(trackingListDate)
            } else {
                trackingListDate = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Calendar.getInstance().time)
                findViewById<Button>(R.id.pickDateButton).text = trackingListDate
            }
            viewModel.loadActivityForDate(trackingListDate)
        }*//*
    }*/

    private fun dateChangeButtonActions() {
        val pickDateButton = findViewById<Button>(R.id.pickDateButton)
        var cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)

        // Set the date to Today's Date
        var dateOnLoad = sdf.format(cal.time)
        pickDateButton.text = dateOnLoad

        // Upon choosing Date, the activities for the date are loaded
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateChosen = sdf.format(cal.time)
                pickDateButton.text = dateChosen
                viewModel.loadActivityForDate(dateChosen)
            }

        // On Click action shows a Date Picker
        pickDateButton.setOnClickListener {
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

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = layoutManager
        val decoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(decoration)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.trackerListData.observe(this, {
            if (recyclerView.adapter == null) {
                adapter = MainRecyclerAdapter(it, this)
                recyclerView.adapter = adapter
            } else {
                adapter.trackerList = viewModel.trackerListData.value ?: emptyList()
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteAllActivities()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFabClickListener(tracker: Tracker) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(TRACKER_ID_KEY, tracker.trackingId)
        startActivity(intent)
    }

    private fun deleteAllActivities() {
        viewModel.deleteAllActivities()
    }

}