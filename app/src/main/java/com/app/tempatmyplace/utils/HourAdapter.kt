package com.app.tempatmyplace.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.tempatmyplace.R
import com.app.tempatmyplace.model.CityWeather
import kotlin.math.roundToInt

class HourAdapter(private val context: Context, private val weather: CityWeather) :
    RecyclerView.Adapter<HourAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val weImage = itemView.findViewById<ImageView>(R.id.imgHourWeather)
        private val txtTemp = itemView.findViewById<TextView>(R.id.txtTempByHour)
        private val txtHour = itemView.findViewById<TextView>(R.id.txtHour)
//        private val txtPrecipitation = itemView.findViewById<TextView>(R.id.txtPrecipitation)
//        private val imgPrp = itemView.findViewById<ImageView>(R.id.imgPrp)
        fun bind(position: Int) {
            txtHour.text = dateFormatHour(weather.hourly[position + 1].dt)
            txtTemp.text = weather.hourly[position + 1].temp.roundToInt().toString() + "°"
            setImage(weImage, weather.hourly[position + 1].weather[0].icon)
//            val precip = weather.hourly[position].pop.roundToInt() * 10
//            txtPrecipitation.text = precip.toString() + "%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.hourly_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = 24
}