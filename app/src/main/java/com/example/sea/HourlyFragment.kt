@file:Suppress("DEPRECATION")

package com.example.sea

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_hourly.*
import kotlinx.android.synthetic.main.fragment_weekly.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class HourlyFragment : Fragment() {
    private val listWithData = ArrayList<HourlyElement>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hourly, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview1)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HourlyAdapter(listWithData,recyclerView)
        threadCreation()
        return rootView
    }

    private fun threadCreation(){
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
        val formatTo = SimpleDateFormat("H")
        val output = locationData?.product?.time!!
        val checkList = ArrayList<Int>()
        for (i in output) {
            val from = formatterFrom.parse(i.to)
            val toFormatted = formatTo.format(from)
            val windSpeed = i.location?.windSpeed?.mps
            val fog = i.location?.fog?.percent
            val temp = i.location?.temperature?.value
            val humid = i.location?.humidity?.value
            val rainfall = output[1].location?.precipitation

            if (toFormatted.toInt() !in checkList) {
                checkList.add(toFormatted.toInt())
                listWithData.add(HourlyElement("KL$toFormatted", windSpeed + "m/s", "", "$fog%", temp+"ºC", "Tide", rainfall.value+rainfall.unit, "Visibility", "$humid%"))
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun ocean(oceanData : OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatTo = SimpleDateFormat("H")
        val output = oceanData?.forecast
        if (output != null) {
            for(i in output) {
                val hour = i.oceanForecast.validTime.timePeriod.begin
                val from = formatterFrom.parse(hour)
                val wavesFormat = formatTo.format(from)
                for(x in listWithData){
                    if(x.title.equals("KL$wavesFormat")){
                        val typo = i.oceanForecast.seaCurrentSpeed
                        x.waves = typo.content+typo.uom


                        //recyclerview1.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}