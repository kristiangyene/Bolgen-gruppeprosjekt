package com.example.sea.ui.weekly

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sea.R


class WeeklyAdapter(private val list: ArrayList<WeeklyElement>) : RecyclerView.Adapter<WeeklyAdapter.MyViewHolder>() {

    // Oppretter nye visninger (påkalt av layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Lager en ny visning
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weekly_listview, parent, false)
        return MyViewHolder(itemView)
    }

    // Erstatter innholdet i en visning (påkalt av layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Henter elementer fra datasettet i denne posisjonen
        // Erstatter innholdet til visningen med dette elementet
        holder.bindItems(list[position])
    }

    // Retunerer størrelsen på datasettet (påkalt av layout manager)
    override fun getItemCount() = list.size


    // Gi en referanse til visningene for hvert element.
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(element : WeeklyElement){
            val textTitle = itemView.findViewById(R.id.titleWeekly) as TextView
            val wind = itemView.findViewById(R.id.weekly_wind) as TextView
            val waves = itemView.findViewById(R.id.weekly_wave) as TextView

            textTitle.text = element.title
            wind.text = element.windspeed
            waves.text = element.waves
        }
    }
}