package com.example.timetracker.data

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TrackerRepository(val app: Application) {

    private val trackerDao = TrackerDatabase.getDatabase(app).trackerDao()

    val trackerListData = MutableLiveData<List<Tracker>>().apply {
        CoroutineScope(Dispatchers.IO).launch {
            loadActivityForDate(
                SimpleDateFormat(
                    "MM/dd/yyyy",
                    Locale.US
                ).format(
                    Calendar.getInstance().time
                )
            )
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            loadActivityForToday()
        }
    }

    @WorkerThread
    fun insertActivity(tracker: Tracker) {
        trackerDao.insertActivity(tracker)
    }

    @WorkerThread
    fun deleteActivity(tracker: Tracker) {
        CoroutineScope(Dispatchers.IO).launch {
            trackerDao.deleteActivity(tracker)
        }
    }

    @WorkerThread
    suspend fun getActivityById(id: Int): Tracker {
        return withContext(Dispatchers.IO) {
            trackerDao.getActivityById(id)
        }
    }

    @WorkerThread
    suspend fun deleteAll() {
        return withContext(Dispatchers.IO) {
            trackerDao.deleteAll()
        }
    }

    @WorkerThread
    fun loadActivityForDate(activityDate: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = trackerDao.getDetailsForDate(activityDate.trim())
            trackerListData?.postValue(data)
        }
    }

    @WorkerThread
    fun loadActivityForToday() {
        loadActivityForDate(
            SimpleDateFormat(
                "MM/dd/yyyy",
                Locale.US
            ).format(Calendar.getInstance().time)
        )
    }
}