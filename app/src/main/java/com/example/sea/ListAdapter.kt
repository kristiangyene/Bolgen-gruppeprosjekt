package com.example.sea

import android.icu.util.ValueIterator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ListAdapter(private val list: List<HourlyElement>, val clickListener: (HourlyElement) -> Unit)
    : RecyclerView.Adapter<HourlyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HourlyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val time: HourlyElement = list[position]
        holder.bind(time, clickListener)
    }

    override fun getItemCount(): Int = list.size

}