package com.example.timetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.data.Tracker
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainRecyclerAdapter(
    var trackerList: List<Tracker>,
    private val itemListener: EditItemListener
) :
    RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val trackerDetail: TextView = itemView.findViewById(R.id.timeDetails)
        val trackingTime: TextView = itemView.findViewById(R.id.trackingTime)
    }

    interface EditItemListener {
        fun onFabClickListener(tracker: Tracker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.tracker_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracker = trackerList[position]
        with(holder) {
            trackerDetail.text = tracker.activityDetails
            trackingTime.text = tracker.trackingTime
            itemView.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
                itemListener.onFabClickListener(tracker)
            }
        }
    }

    override fun getItemCount() = trackerList.size
}