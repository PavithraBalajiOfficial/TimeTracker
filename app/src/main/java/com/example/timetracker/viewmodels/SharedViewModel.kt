package com.example.timetracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.Tracker
import com.example.timetracker.data.TrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    val selectedTrackerData: MutableLiveData<Tracker> by lazy {
        MutableLiveData<Tracker>()
    }
    private val trackerRepository = TrackerRepository(app)
    val trackerListData = trackerRepository.trackerListData

    fun deleteAllActivities() {
        CoroutineScope(Dispatchers.IO).launch {
            trackerRepository.deleteAll()
        }
    }

    fun loadActivity(trackerId: Int) = viewModelScope.launch {
        val data = trackerRepository.getActivityById(trackerId)
        selectedTrackerData.postValue(data)
    }

    fun loadActivityForDate(activityDate: String) = viewModelScope.launch {
        trackerRepository.loadActivityForDate(activityDate)
    }

    fun addDetailsToTracker(
        trackerDate: String,
        trackerTime: String,
        trackerActivityDetail: String
    ) = viewModelScope.launch {
        var liveTracker = selectedTrackerData.value
        if (liveTracker == null) {
            liveTracker = Tracker(0, trackerDate, trackerTime, trackerActivityDetail)
        } else {
            liveTracker.trackingDate = trackerDate
            liveTracker.trackingTime = trackerTime
            liveTracker.activityDetails = trackerActivityDetail
        }
        trackerRepository.insertActivity(liveTracker)
    }

    fun deleteActivity() = viewModelScope.launch {
        trackerRepository.deleteActivity(selectedTrackerData.value!!)
    }

    private fun resetView() {
        loadActivityForDate(
            SimpleDateFormat(
                "MM/dd/yyyy",
                Locale.US
            ).format(Calendar.getInstance().time)
        )
    }
}