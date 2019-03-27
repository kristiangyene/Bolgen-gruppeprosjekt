package com.example.sea

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*

import net.cachapa.expandablelayout.ExpandableLayout

class HourlyFragment1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hourly, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview1)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(SimpleAdapter(recyclerView))

        return rootView
    }

    private class SimpleAdapter(private val recyclerView: RecyclerView) :
        RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
        private var selectedItem = UNSELECTED

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.hourlylistview, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int {
            return 8
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
            ExpandableLayout.OnExpansionUpdateListener {
            private val expandableLayout: ExpandableLayout
            private val expandButton: CardView

            init {

                expandableLayout = itemView.findViewById(R.id.expandable_layout_2)
                expandableLayout.setInterpolator(OvershootInterpolator())
                expandableLayout.setOnExpansionUpdateListener(this)
                expandButton = itemView.findViewById(R.id.expand_button2)

                expandButton.setOnClickListener(this)
            }

            fun bind() {
                val position = adapterPosition
                val isSelected = position == selectedItem


                //expandButton.text = "$position. Tap to expand"
                expandButton.isSelected = isSelected
                expandableLayout.setExpanded(isSelected, true)

            }

            override fun onClick(view: View) {
                val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
                if (holder != null) {
                    holder.expandButton.isSelected = false
                    holder.expandableLayout.collapse()
                    //Toast.makeText(view.context, "Collapsing", LENGTH_SHORT).show()
                }

                val position = adapterPosition
                if (position == selectedItem) {
                    selectedItem = UNSELECTED
                } else {
                    expandButton.isSelected = true
                    expandableLayout.expand()
                    //Toast.makeText(view.context, "Expanding", LENGTH_SHORT).show()
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
                    recyclerView.smoothScrollToPosition(adapterPosition)
                }
            }
        }

        companion object {
            private val UNSELECTED = -1
        }
    }
}