package com.example.sea.ui.weekly

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sea.R
import kotlinx.android.synthetic.main.weekly_listview.view.*

class WeeklyAdapter(private val list: ArrayList<WeeklyElement>) : RecyclerView.Adapter<WeeklyAdapter.MyViewHolder>() {
    // Oppretter nye visninger (påkalt av layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Lager en ny visning
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weekly_listview, parent, false)
        return MyViewHolder(itemView)
    }

    // Retunerer størrelsen på datasettet (påkalt av layout manager)
    override fun getItemCount() = list.size

    // Erstatter innholdet i en visning (påkalt av layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Henter elementer fra datasettet i denne posisjonen
        // Erstatter innholdet til visningen med dette elementet

        holder.title.text = list[position].title
        holder.wind.text = list[position].windspeed
        holder.wave.text = list[position].waves
    }

    // Gi en referanse til visningene for hvert element.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.titleWeekly
        val wind: TextView = itemView.weekly_wind
        val wave: TextView = itemView.weekly_wave
    }
}