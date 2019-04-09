package com.example.sea

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class WeeklyFragment : Fragment() {
    private val listWithData = ArrayList<WeeklyElement>()
    private lateinit var adapter: WeeklyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weekly, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WeeklyAdapter(listWithData)
        recyclerView!!.adapter = adapter
        threadcreation()

        return rootView
    }

    private fun threadcreation(){
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(60.1F, 9.58F, null )
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


        for (i in output) {
            var time = formatToHour.format(formatterFrom.parse(i.to))
            if (time == "12") {
                val from = formatterFrom.parse(i.to)
                val toFormatted = formatToDay.format(from)
                var windspeed = i.location?.windSpeed?.mps
                val month = formatToMonth.format((formatterFrom.parse(i.to)))

                if (toFormatted.toInt() !in checkList) {
                    checkList.add(toFormatted.toInt())
                    listWithData.add(WeeklyElement("$toFormatted.$month", "$toFormatted", "$windspeed m/s", "-"))
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
                    val day = formatToDay.format(formatterFrom.parse(i.oceanForecast.validTime.timePeriod.begin))
                    for (x in listWithData) {
                        if (x.day.equals(day)) {
                            val typo = i.oceanForecast.significantTotalWaveHeight

                            // gir 'warning' men kræsjer om vi fjerner den
                            if(typo != null) x.waves = typo.content+typo.uom

                            //recyclerview2.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}