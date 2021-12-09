package com.example.timetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracker")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    val trackingId: Int,
    var trackingDate: String,
    var trackingTime: String,
    var activityDetails: String
)