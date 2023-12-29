package com.example.cyclingbuddy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.db.Ride.RideData
import com.example.cyclingbuddy.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Adapter : RecyclerView.Adapter<Adapter.RunViewHolder>() {

    private var rideClickListener: onRideClickListener? = null

    val diffCallback = object : DiffUtil.ItemCallback<RideData>() {
        override fun areItemsTheSame(oldItem: RideData, newItem: RideData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RideData, newItem: RideData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<RideData>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ride_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.runStartTimestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            holder.tvDate.text = "Date: "+ dateFormat.format(calendar.time)
            holder.tvTime.text = "Duration: "+TrackingUtility.getFormattedStopWatchTime(run.runTime)
        }
    }

    interface onRideClickListener {
        fun onRideClick(ride: RideData, position: Int)
    }

    fun setRideClickListener(listener: onRideClickListener) {
        this.rideClickListener = listener
    }

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvDate: TextView
        var tvTime: TextView

        init{

            tvDate = itemView.findViewById(R.id.date_tv)
            tvTime= itemView.findViewById(R.id.time_tv)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (rideClickListener != null && position != RecyclerView.NO_POSITION) {
                    rideClickListener!!.onRideClick(differ.currentList.get(position), position)
                }
            }
        }
    }
}