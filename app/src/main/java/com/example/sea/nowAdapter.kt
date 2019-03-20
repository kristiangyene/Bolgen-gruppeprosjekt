package com.example.sea


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.ArrayList





class NowAdapter(private val elementList: ArrayList<Widget>) : RecyclerView.Adapter<NowAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val iconString = elementList[position].image
        holder.desc.text = elementList[position].text
        when (iconString) {
            "Synlighet" -> holder.icon.setImageResource(R.drawable.visibility)
            "Vind" -> holder.icon.setImageResource(R.drawable.wind)
            "Bølgehøyde" -> holder.icon.setImageResource(R.drawable.waves)
            "Tidevann" -> holder.icon.setImageResource(R.drawable.tide)
            "Grader" -> holder.icon.setImageResource(R.drawable.thermometer)
            "Nedbør" -> holder.icon.setImageResource(R.drawable.drop)
            "Tåke" -> holder.icon.setImageResource(R.drawable.fog)
            "Fuktighet" -> holder.icon.setImageResource(R.drawable.humidity)
            "Skytetthet" -> holder.icon.setImageResource(R.drawable.cloud)
        }


    }

    override fun getItemCount(): Int {
        return elementList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var icon: ImageView = itemView.findViewById<View>(R.id.icon_widget) as ImageView
        internal var desc: TextView = itemView.findViewById<View>(R.id.value_widget) as TextView

    }
}