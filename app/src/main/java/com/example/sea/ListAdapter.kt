package com.example.sea

import android.icu.util.ValueIterator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ListAdapter (val elementList: ArrayList<HourlyElement>) : RecyclerView.Adapter<ListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hourlyelement, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        holder.bindItems(elementList[position])

    }

    override fun getItemCount(): Int {

        return elementList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(e : HourlyElement){
            val textTitle = itemView.findViewById(R.id.title) as TextView
            textTitle.text = e.title
        }
    }
}