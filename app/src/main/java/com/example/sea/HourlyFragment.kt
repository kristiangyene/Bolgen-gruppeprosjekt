@file:Suppress("DEPRECATION")

package com.example.sea

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class HourlyFragment : Fragment() {
    private val listWithData = ArrayList<HourlyElement>()
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private var startTimeFound = false
    private var startTime : String? = null
    private val harbors = hashMapOf<String, LatLng>()
    private var closestHarbor : String? = null
    private var closestHarborValue = Double.MAX_VALUE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hourly, container, false)

        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview1)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HourlyAdapter(listWithData,recyclerView)

        val currentLocation = Location("")
        currentLocation.latitude = sharedPreferences.getFloat("lat", 60F).toDouble()
        currentLocation.longitude = sharedPreferences.getFloat("long", 11F).toDouble()

        harbors["andenes"] = LatLng(69.326233, 16.139759)
        harbors["andenes"] = LatLng(69.326233, 16.139759)
        harbors["bergen"] = LatLng(60.392353, 5.312078)
        harbors["bodø"] = LatLng(67.289953, 14.396987)
        harbors["hammerfest"] = LatLng(70.664762, 23.683317)
        harbors["harstad"] = LatLng(68.801332, 16.548197)
        harbors["heimsjø"] = LatLng(63.425898, 9.095695)
        harbors["helgeroa"] = LatLng(58.994180, 9.856801)
        harbors["honningsvåg"] = LatLng(70.981345, 25.968195)
        harbors["kabelvåg"] = LatLng(68.230406, 14.566273)
        harbors["kristiansund"] = LatLng(63.114018, 7.736651)
        harbors["måløy"] = LatLng(61.933380, 5.113401)
        harbors["narvik"] = LatLng(68.427514, 17.426371)
        harbors["oscarsborg"] = LatLng(59.681414, 10.625639)
        harbors["oslo"] = LatLng(59.904023, 10.738040)
        harbors["rørvik"] = LatLng(64.859678, 11.237081)
        harbors["stavanger"] = LatLng(58.972168, 5.727195)
        harbors["tregde"] = LatLng(58.009595, 7.545120)
        harbors["tromsø"] = LatLng(69.647098, 18.960921)
        harbors["trondheim"] = LatLng(63.440268, 10.417503)
        harbors["vardø"] = LatLng(70.374517, 31.103911)
        harbors["ålesund"] = LatLng(62.475094, 6.150923)


        for (harbor in harbors) {
            val harborLocation = Location("")
            harborLocation.latitude = harbor.value.latitude
            harborLocation.longitude = harbor.value.longitude
            val distance = currentLocation.distanceTo(harborLocation).toDouble()

            if(distance < closestHarborValue) {
                closestHarborValue = distance
                closestHarbor = harbor.key
                Log.d("Ahmed", "$closestHarbor $closestHarborValue")
            }
        }


        threadCreation()
        return rootView
    }

    private fun threadCreation(){
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(sharedPreferences.getFloat("lat", 60F), sharedPreferences.getFloat("long", 11F), null)
        val oceanCall = client.getOceanData(60.10, 5.0)
        var tidalCall : Call<String>? = null
        if(closestHarbor != null) {
            tidalCall = RetrofitClient().getClient("string").getTidalWater(closestHarbor!!)
        }

        thread {
            val bodyLocation = locationCall.execute().body()
            location(bodyLocation)
            val bodyOcean = oceanCall.execute().body()
            ocean(bodyOcean)

            if(closestHarbor != null && closestHarborValue < 30000) {
                val bodyTidal = tidalCall!!.execute().body()
                tidal(bodyTidal!!)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun location(locationData: LocationData?) {
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

            if(!startTimeFound) {
                startTime = toFormatted
                startTimeFound = true
            }

            if (toFormatted.toInt() !in checkList) {
                checkList.add(toFormatted.toInt())
                listWithData.add(
                    HourlyElement(
                        "Kl $toFormatted",
                        "$windSpeed m/s",
                        "-",
                        "$fog %",
                        temp + "ºC",
                        "-",
                        rainfall.value + rainfall.unit,
                        "Visibility",
                        "$humid %"
                    )
                )
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun ocean(oceanData: OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatTo = SimpleDateFormat("H")
        val output = oceanData?.forecast

        if (output != null) {
            for (i in output) {
                val hour = i.oceanForecast.validTime.timePeriod.begin
                val from = formatterFrom.parse(hour)
                val wavesFormat = formatTo.format(from)
                for (x in listWithData) {
                    if (x.title.equals("Kl $wavesFormat")) {
                        val typo = i.oceanForecast.significantTotalWaveHeight
                        if (typo != null) x.waves = typo.content + typo.uom


                        //recyclerview1.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // Viser tidevann for de neste 24 timene
    private fun tidal(bodyTidal : String) {
        var foundStart = false
        var startIndex = 0
        val line = bodyTidal.split("\n")
        var counter = 0

        for (i in 8 until line.size - 1) {
            val number = line[i].split("\\s+".toRegex())
            if (number[4] != startTime) {
                continue
            }
            else {
                foundStart = true
                startIndex = i
                break
            }
        }

        if (foundStart) {
            for (i in startIndex until startIndex + 24) {
                if (line[i][line[i].length - 6] == ' ') {
                    listWithData[counter++].tide = line[i].substring(line[i].length - 5, line[i].length)
                }
                else {
                    listWithData[counter++].tide = line[i].substring(line[i].length - 6, line[i].length)
                }
            }
        }

    }
}
