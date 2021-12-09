package com.example.timetracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackerDao {
    @Query("SELECT * from tracker")
    fun getAll(): LiveData<List<Tracker>>

    @Query("SELECT * from tracker where trackingDate = :trackingDate")
    fun getDetailsForDate(trackingDate: String): List<Tracker>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivity(tracker: Tracker)

    @Insert
    fun insertActivities(trackers: List<Tracker>)

    @Query("DELETE from tracker")
    fun deleteAll()

    @Delete
    fun deleteActivity(tracker: Tracker)

    @Query("SELECT * from tracker where trackingId=:id")
    fun getActivityById(id: Int): Tracker
}