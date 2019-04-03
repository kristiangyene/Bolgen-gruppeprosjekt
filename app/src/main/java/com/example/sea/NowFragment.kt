package com.example.sea

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response



class NowFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: NowAdapter
    private val listOfElements = ArrayList<Widget>()
    private lateinit var rootView: View
    private lateinit var listOfStrings: ArrayList<String>

    //TODO: Bruke nåværende koordinater for OceanData og håndtere hvis man ikke er i sjøen.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_now, container, false)
        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        listOfStrings = arrayListOf(
            getString(R.string.navigation_drawer_tide),
            getString(R.string.navigation_drawer_temperature2),
            getString(R.string.navigation_drawer_weather),
            getString(R.string.navigation_drawer_fog),
            getString(R.string.navigation_drawer_humidity),
            getString(R.string.navigation_drawer_cloudiness),
            getString(R.string.navigation_drawer_pressure2))

        recyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = GridLayoutManager(context, 1)
        adapter = NowAdapter(listOfElements)
        recyclerView!!.adapter = adapter
        fetchLocationData(sharedPreferences.getFloat("lat", 60.0F), sharedPreferences.getFloat("long", 11F))
        fetchOceanData(60.10, 5.0)

        return rootView
    }

    private fun fetchLocationData(latitude: Float, longitude: Float) {

        val call = RetrofitClient().getClient("json").getLocationData(latitude, longitude, null)
        call.enqueue(object : retrofit2.Callback<LocationData> {

            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>){
                if (response.isSuccessful && response.code() == 200){
                    val data = response.body()?.product?.time!!
                    listOfElements.add(Widget(data[0].location.windSpeed.mps + "mph", resources.getString(R.string.navigation_drawer_wind), data[0].location.windDirection.name))
                    for(item in 0 until listOfStrings.size){
                        if(sharedPreferences.getBoolean(listOfStrings[item], false)){
                            when {
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_temperature2) ->  listOfElements.add(
                                    Widget(data[0].location.temperature.value + "˚C", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_weather) -> listOfElements.add(
                                    Widget(data[1].location.precipitation.value + "mm", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_fog) -> listOfElements.add(
                                    Widget(data[0].location.fog.percent + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_cloudiness) -> listOfElements.add(
                                    Widget(data[0].location.cloudiness.percent + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_humidity) -> listOfElements.add(
                                    Widget(data[0].location.humidity.value + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_pressure) -> listOfElements.add(
                                    Widget(data[0].location.pressure.value + "HPa", listOfStrings[item], "")
                                )
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                Toast.makeText(context, "Response failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchOceanData(latitude: Double, longitude: Double) {

        val call = RetrofitClient().getClient("json").getOceanData(latitude, longitude)
        call.enqueue(object : retrofit2.Callback<OceanData> {

            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>){
                if (response.isSuccessful && response.code() == 200){
                    val data = response.body()?.forecast?.get(0)?.oceanForecast
                    listOfElements.add(Widget(data?.significantTotalWaveHeight?.content + "m", resources.getString(R.string.navigation_drawer_wave), ""))
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                Toast.makeText(context, "Response failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


