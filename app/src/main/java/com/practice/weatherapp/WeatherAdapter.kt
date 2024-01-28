package com.practice.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter(private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<WeatherAdapter.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvTime: TextView = itemView.findViewById(R.id.tvHourlyWeatherTime)
        val rvTemp: TextView = itemView.findViewById(R.id.tvHourlyWeatherTemp)
        val ivImage: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_list, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvTime.text = currentItem.dataTime
        holder.rvTemp.text = currentItem.dataTemp

        val imageResource = getImageResourceForTime(currentItem.dataTime)
        holder.ivImage.setImageResource(imageResource)

    }

    private fun getImageResourceForTime(dataTime: String): Int {
        val hour = dataTime.split(":")[0].toInt()
        return when (hour) {
            in 6..11 -> R.drawable.morning
            in 12..17 -> R.drawable.afternoon
            in 18..23 -> R.drawable.evening
            else -> R.drawable.night
        }
    }

}