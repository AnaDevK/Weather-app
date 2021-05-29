package com.app.weather.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.weather.R
import com.app.weather.model.Daily
import kotlin.math.roundToInt

class DayAdapter(private val context: Context, private val weather: List<Daily>): RecyclerView.Adapter<DayAdapter.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.daily_weather, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imgDay = itemView.findViewById<ImageView>(R.id.imgDay)
        private val txtDay = itemView.findViewById<TextView>(R.id.txtDay)
        private val txtDescription = itemView.findViewById<TextView>(R.id.txtDescription)
        private val txtTempDay = itemView.findViewById<TextView>(R.id.txtTempDay)

             fun bind(position: Int) {
                 txtDay.text = dateFormatDay(weather[position + 1].dt)
                 txtTempDay.text = weather[position + 1].temp.day.roundToInt().toString()+ "°"
                 txtDescription.text = weather[position + 1].weather[0].description
                 setImage(imgDay, weather[position + 1].weather[0].icon)
            }
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = 7
    
}