package com.example.sea

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class WeeklyAdapter(private val list: ArrayList<WeeklyElement>) : RecyclerView.Adapter<WeeklyAdapter.MyViewHolder>() {
    private var selectedItem = UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weekly_listview, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    override fun getItemCount() = list.size


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

    companion object {
        private val UNSELECTED = -1
    }
}