package com.example.sea


import android.annotation.SuppressLint
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
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_hourly.*
import kotlinx.android.synthetic.main.fragment_weekly.*
import net.cachapa.expandablelayout.ExpandableLayout
import java.text.SimpleDateFormat
import kotlin.concurrent.thread


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class WeeklyFragment : Fragment() {
    private val listWithData = ArrayList<WeeklyElement>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weekly, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = WeeklyFragment.SimpleAdapter(listWithData, recyclerView)
        threadcreation()

        // Inflate the layout for this fragment
        return rootView
    }

    private fun threadcreation(){
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(60.10, 9.58, null )
        val oceanCall = client.getOceanData(60.10, 5.0)
        thread {
            val bodyLocation = locationCall.execute().body()
            location(bodyLocation)
            val bodyOcean = oceanCall.execute().body()
            ocean(bodyOcean)

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun location(locationData : LocationData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val formatToMonth = SimpleDateFormat("MM")
        val output = locationData?.product?.time!!
        val checkList = ArrayList<Int>()

        if (output != null) {
            for (i in output) {
                var time = formatToHour.format(formatterFrom.parse(i.to))
                if (time == "12") {
                    val from = formatterFrom.parse(i.to)
                    val toFormatted = formatToDay.format(from)
                    var windspeed = i.location?.windSpeed?.mps
                    val month = formatToMonth.format((formatterFrom.parse(i.to)))

                    if (toFormatted.toInt() !in checkList) {
                        checkList.add(toFormatted.toInt())
                        listWithData.add(WeeklyElement("$toFormatted.$month", "$toFormatted", windspeed + "m", "-"))
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun ocean(oceanData : OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val output = oceanData?.forecast

        if (output != null) {
            for(i in output) {
                val hour = formatToHour.format(formatterFrom.parse(i.oceanForecast.validTime.timePeriod.begin))
                    if (hour == "12") {
                        val hour = i.oceanForecast.validTime.timePeriod.begin
                        val from = formatterFrom.parse(hour)
                        val day = formatToDay.format(from)

                        for (x in listWithData) {
                            if (x.day.equals(day)) {
                                val typo = i.oceanForecast.significantTotalWaveHeight
                                if(typo != null)
                                    x.waves = typo.content+typo.uom

                                recyclerview2.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
            }
        }
    }


    private class SimpleAdapter(private val list: List<WeeklyElement>, private val recyclerView: RecyclerView) :
        RecyclerView.Adapter<WeeklyFragment.SimpleAdapter.ViewHolder>() {
        private var selectedItem = UNSELECTED

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.weekly_listview, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val time: WeeklyElement = list[position]
            holder.bindItems(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


            fun bindItems(e : WeeklyElement){
                val textTitle = itemView.findViewById(R.id.KL) as TextView
                val vind = itemView.findViewById(R.id.hour_vind) as TextView
                val waves = itemView.findViewById(R.id.hour_bølge) as TextView


                textTitle.text = e.title
                vind.text = e.windspeed
                waves.text = e.waves
            }

        }

        companion object {
            private val UNSELECTED = -1
        }
    }
}