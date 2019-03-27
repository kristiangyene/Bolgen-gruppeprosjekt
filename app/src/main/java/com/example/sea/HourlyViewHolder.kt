package com.example.sea

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class HourlyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_hourly2, parent, false)) {
    private var mTitleView: TextView? = null
    private var mExpandLayer: CardView? = null



    init {
        mTitleView = itemView.findViewById(R.id.KL)
        mExpandLayer = itemView.findViewById(R.id.expand_button2)
    }

    fun bind(time: HourlyElement, clickListener: (HourlyElement) -> Unit) {
        mTitleView?.text = time.title
        itemView?.setOnClickListener{ clickListener (time)}
    }

}