package com.example.sea


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList




class NowAdapter(private val listOfElements: ArrayList<Widget>) : RecyclerView.Adapter<NowAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.desc.text = listOfElements[position].text
        holder.dir.text = listOfElements[position].dir
        when (listOfElements[position].image) {
            "Synlighet" -> holder.icon.setImageResource(R.drawable.visibility)
            "Vind" -> holder.icon.setImageResource(R.drawable.wind)
            "Bølgehøyde" -> holder.icon.setImageResource(R.drawable.waves)
            "Tidevann" -> holder.icon.setImageResource(R.drawable.tide)
            "Grader" -> holder.icon.setImageResource(R.drawable.thermometer)
            "Nedbør" -> holder.icon.setImageResource(R.drawable.drop)
            "Tåke" -> holder.icon.setImageResource(R.drawable.fog)
            "Fuktighet" -> holder.icon.setImageResource(R.drawable.humidity)
            "Skytetthet" -> holder.icon.setImageResource(R.drawable.cloud)
            "Trykk" -> holder.icon.setImageResource(R.drawable.gauge)

        }


    }

    override fun getItemCount(): Int {
        return listOfElements.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var icon: ImageView = itemView.findViewById<View>(R.id.icon_widget) as ImageView
        internal var desc: TextView = itemView.findViewById<View>(R.id.value_widget) as TextView
        internal var dir: TextView = itemView.findViewById<View>(R.id.direction) as TextView

    }
}