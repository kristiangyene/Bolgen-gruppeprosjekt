package com.example.sea.ui.weekly

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sea.R
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class WeeklyFragment : Fragment() {
    private val listWithData = ArrayList<WeeklyElement>()
    private lateinit var adapter: WeeklyAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"


    //TODO: Bruke nåværende koordinater for OceanData og håndtere hvis man ikke er i sjøen.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weekly, container, false)
        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
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

    // Formaterer og plasserer data fra locationforecast
    @SuppressLint("SimpleDateFormat")
    private fun location(locationData : LocationData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val formatToDayText = SimpleDateFormat("EEE")

        val locationForecast = locationData?.product?.time!!
        val checkList = ArrayList<Int>()


        // Finner fram til vinddata for time '12' og plasserer dette i et nytt weeklyElement
        for (time in locationForecast) {
            val date = formatterFrom.parse(time.to)
            var hour = formatToHour.format(date)
            if (hour == "12") {
                val day = formatToDay.format(date)
                val dayText = formatToDayText.format(date).capitalize()

                var windSpeed = time.location?.windSpeed?.mps

                // Sjekker om data for samme tidspunkt ikke er allerede lagt til.
                if (day.toInt() !in checkList) {
                    checkList.add(day.toInt())
                    var windMeasurement: String
                    val windText = sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null)
                    if (windText == null || windText == "mps") windMeasurement =  "mps"
                    else{
                        windMeasurement = windText
                        windSpeed = (windSpeed.toDouble() * 3.6).toString()
                    }
                    listWithData.add(WeeklyElement(dayText, day, "${"%.1f".format(windSpeed.toDouble())} $windMeasurement", "-"))
                }
            }
        }
    }

    // Formaterer og plasserer data fra oceanforecast
    @SuppressLint("SimpleDateFormat")
    private fun ocean(oceanData : OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val oceanForecast = oceanData?.forecast

        // Finner fram til bølgedata for time '12' og plasserer dette i weeklyElement for tilsvarende dag.
        if (oceanForecast != null) {
            for(time in oceanForecast) {
                val date = formatterFrom.parse(time.oceanForecast.validTime.timePeriod.begin)
                val hour = formatToHour.format(date)
                if (hour == "12") {
                    val day = formatToDay.format(date)
                    for (x in listWithData) {
                        if (x.day.equals(day)) {
                            val waveValue = time.oceanForecast.significantTotalWaveHeight
                            // gir 'warning' men kræsjer om vi fjerner den
                            if(waveValue != null) x.waves = waveValue.content + " m"

                            //recyclerview2.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}