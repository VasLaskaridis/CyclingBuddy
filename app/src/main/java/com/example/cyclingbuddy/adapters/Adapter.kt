package com.example.cyclingbuddy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.util.SortingHeader
import com.example.cyclingbuddy.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var rideClickListener: onRideClickListener? = null
    private var deleteClickListener:onDeleteClickListener? = null
    var rideList: List<RideData>?=null

    fun submitList(list: List<RideData>) {
        rideList = list
        notifyDataSetChanged()
    }

    fun sortRideList(sortType: SortingHeader){
        when (sortType) {
            SortingHeader.DATE -> {  submitList(rideList!!.sortedByDescending { it.runStartTimestamp }) }
            SortingHeader.RUNNING_TIME -> { submitList(rideList!!.sortedByDescending { it.runTime }) }
            SortingHeader.AVERAGE_SPEED -> {  submitList(rideList!!.sortedByDescending { it.averageSpeed })}
            SortingHeader.DISTANCE -> { submitList(rideList!!.sortedByDescending { it.distance }) }
            SortingHeader.CALORIES_BURNED -> {  submitList(rideList!!.sortedByDescending { it.caloriesBurned })}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val itemView = inflater.inflate(R.layout.ride_item, parent, false)
        val viewHolder = RideViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val rideHolder: RideViewHolder = holder as RideViewHolder
        val currentRide: RideData = rideList!!.get(position)

        rideHolder.itemView.apply {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = currentRide.runStartTimestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            rideHolder.tvDate.text = "Date: "+ dateFormat.format(calendar.time)
            rideHolder.tvTime.text = "Duration: "+TrackingUtility.getFormattedStopWatchTime(currentRide.runTime)
        }
    }

    override fun getItemCount(): Int {
        if(rideList!=null){
            return rideList!!.size
        }else{
            return 0
        }
    }

    interface onRideClickListener {
        fun onRideClick(ride: RideData, position: Int)
    }

    fun setRideClickListener(listener: onRideClickListener) {
        this.rideClickListener = listener
    }

    interface onDeleteClickListener {
        fun onDeleteClick(ride: RideData, position: Int)
    }

    fun setDeleteClickListener(listener: onDeleteClickListener) {
        this.deleteClickListener = listener
    }

    inner class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvDate: TextView
        var tvTime: TextView
        var delete_iv: ImageView
        init{

            tvDate = itemView.findViewById(R.id.date_tv)
            tvTime= itemView.findViewById(R.id.time_tv)
            delete_iv= itemView.findViewById(R.id.delete_iv)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (rideClickListener != null && position != RecyclerView.NO_POSITION) {
                    rideClickListener!!.onRideClick(rideList!!.get(position), position)
                }
            }
            delete_iv.setOnClickListener {
                val position = adapterPosition
                if (deleteClickListener != null && position != RecyclerView.NO_POSITION) {
                    deleteClickListener!!.onDeleteClick(rideList!!.get(position), position)
                }
            }
        }
    }
}