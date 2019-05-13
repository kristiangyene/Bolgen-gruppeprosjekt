package com.example.sea.ui.now

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sea.R
import java.util.ArrayList

class NowAdapter(private val listOfElements: ArrayList<NowElement>, private var context: Context) : RecyclerView.Adapter<NowAdapter.MyViewHolder>() {

    // Oppretter nye visninger (påkalt av layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.now_listview, parent, false)
        return MyViewHolder(itemView)
    }

    // Retunerer størrelsen på datasettet (påkalt av layout manager)
    override fun getItemCount() = listOfElements.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Henter elementer fra datasettet i denne posisjonen
        // Erstatter innholdet til visningen med dette elementet
        holder.desc.text = listOfElements[position].text
        holder.dir.text = listOfElements[position].dir
        when (listOfElements[position].image) {
            context.getString(R.string.navigation_drawer_visibility) -> holder.icon.setImageResource(R.drawable.visibility_black)
            context.getString(R.string.navigation_drawer_wind) -> holder.icon.setImageResource(R.drawable.wind_black)
            context.getString(R.string.navigation_drawer_wave) -> holder.icon.setImageResource(R.drawable.waves_black)
            context.getString(R.string.navigation_drawer_tide) -> holder.icon.setImageResource(R.drawable.tide_black)
            context.getString(R.string.navigation_drawer_temperature2) -> holder.icon.setImageResource(R.drawable.thermometer_black)
            context.getString(R.string.navigation_drawer_rain) -> holder.icon.setImageResource(R.drawable.rain_black)
            context.getString(R.string.navigation_drawer_fog) -> holder.icon.setImageResource(R.drawable.fog_black)
            context.getString(R.string.navigation_drawer_humidity) -> holder.icon.setImageResource(R.drawable.humidity_black)
            context.getString(R.string.navigation_drawer_cloudiness) -> holder.icon.setImageResource(R.drawable.cloud_black)
            context.getString(R.string.navigation_drawer_pressure2) -> holder.icon.setImageResource(R.drawable.gauge_black)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var icon: ImageView = itemView.findViewById<View>(R.id.icon_widget) as ImageView
        internal var desc: TextView = itemView.findViewById<View>(R.id.value_widget) as TextView
        internal var dir: TextView = itemView.findViewById<View>(R.id.direction) as TextView
    }
}