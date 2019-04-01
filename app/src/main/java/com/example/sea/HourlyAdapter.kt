package com.example.sea

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import net.cachapa.expandablelayout.ExpandableLayout

class HourlyAdapter(private val list: List<HourlyElement>, private val recyclerView: RecyclerView) : RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    companion object {
        private const val UNSELECTED = -1
    }
    private var selectedItem = UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourlylistview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
        holder.bindItems(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
        ExpandableLayout.OnExpansionUpdateListener {
        private val expandableLayout: ExpandableLayout = itemView.findViewById(R.id.expandable_layout_2)
        private val expandButton: CardView

        init {
            expandableLayout.setInterpolator(OvershootInterpolator())
            expandableLayout.setOnExpansionUpdateListener(this)
            expandButton = itemView.findViewById(R.id.expand_button2)
            expandButton.setOnClickListener(this)
        }

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem

            expandButton.isSelected = isSelected
            expandableLayout.setExpanded(isSelected, true)
        }

        fun bindItems(e : HourlyElement){
            val textTitle = itemView.findViewById(R.id.KL) as TextView
            val wind = itemView.findViewById(R.id.hour_wind) as TextView
            val waves = itemView.findViewById(R.id.hour_wave) as TextView
            val fog = itemView.findViewById(R.id.hour_fog) as TextView
            val temp = itemView.findViewById(R.id.hour_thermo) as TextView
            val tide = itemView.findViewById(R.id.hour_tide) as TextView
            val rain = itemView.findViewById(R.id.hour_water) as TextView
            val visibility = itemView.findViewById(R.id.hour_visibility) as TextView
            val humid = itemView.findViewById(R.id.hour_humid) as TextView

            textTitle.text = e.title
            wind.text = e.vindspeed
            waves.text = e.waves
            fog.text = e.fog
            temp.text = e.temp
            tide.text = e.tide
            rain.text = e.rain
            visibility.text = e.visi
            humid.text = e.humid
        }

        override fun onClick(view: View) {
            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
            if (holder != null) {
                holder.expandButton.isSelected = false
                holder.expandableLayout.collapse()
            }

            val position = adapterPosition

            if (position == selectedItem) {
                selectedItem = UNSELECTED
            } else {
                expandButton.isSelected = true
                expandableLayout.expand()
                selectedItem = position
            }
        }

        /*override fun onClick(view: View) {
            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
            if(holder != null) {
                holder.expandableLayout!!.expand()

                /*if (holder.expandableLayout!!.isExpanded) {
                    holder.expandableLayout!!.collapse()
                } else {
                    holder.expandableLayout!!.expand()
                }*/
            }
        }*/


        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("ExpandableLayout", "State: $state")
            if (state == ExpandableLayout.State.EXPANDING) {
//                    recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }
    }
}