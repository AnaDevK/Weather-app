package com.app.weather.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.weather.R
import com.app.weather.model.CityWeather
import kotlin.math.roundToInt

class HourAdapter(private val context: Context, private val weather: CityWeather): RecyclerView.Adapter<HourAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val weImage = itemView.findViewById<ImageView>(R.id.imgHourWeather)
        private val txtTemp = itemView.findViewById<TextView>(R.id.txtTempByHour)
        private val txtHour = itemView.findViewById<TextView>(R.id.txtHour)
        fun bind(position: Int) {
            txtHour.text = dateFormatHour(weather.hourly[position].dt)
            txtTemp.text = weather.hourly[position].temp.roundToInt().toString()+ "°"
            setImage(weImage, weather.hourly[position].weather[0].icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hourly_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = 48
    }