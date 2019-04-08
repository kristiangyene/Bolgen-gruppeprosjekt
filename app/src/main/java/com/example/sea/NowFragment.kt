package com.example.sea

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.GridLayoutManager
import android.widget.SeekBar
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response


@Suppress("NAME_SHADOWING")
class NowFragment : Fragment(){

    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private var recyclerView: RecyclerView? = null
    lateinit var adapter: NowAdapter
    private val listOfElements = ArrayList<NowElement>()
    private lateinit var rootView: View
    private lateinit var listOfStrings: ArrayList<String>
    private var wavehight: Double = 0.0
    private var wind: Double = 0.0
    private var risiko: Int = 0
    private lateinit var seekbar: SeekBar



    //TODO: lage metode som finner nåværende koordinasjoner.

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_now, container, false)

        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        seekbar = rootView.findViewById<SeekBar>(R.id.seekbar)
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
        fetchLocationData(60.146973929322854, 4.713134765625)
        fetchOceanData(60.146973929322854, 4.713134765625)
        seekbar.setOnTouchListener { _, _ -> true }


        return rootView
    }

    private fun fetchLocationData(latitude: Double, longitude: Double) {

        val call = RetrofitClient().getClient("json").getLocationData(latitude, longitude, null)
        call.enqueue(object : retrofit2.Callback<LocationData> {

            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>){

                if (response.isSuccessful && response.code() == 200){
                    val data = response.body()?.product?.time!!
                    val measurement: String
                    var value = data[0].location.windSpeed.mps.toDouble()
                    val windText = sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null)
                    if (windText == null || windText == "Km/h") {
                        measurement =  "Km/h"
                        value *= 3.6
                    }
                    else if(windText == "Mph"){
                        measurement = windText
                        value *= 2.236936
                    }else measurement = windText
                    listOfElements.add(NowElement(String.format("%.1f", value) + measurement, resources.getString(R.string.navigation_drawer_wind), data[0].location.windDirection.name))
                    for(item in 0 until listOfStrings.size){
                        if(sharedPreferences.getBoolean(listOfStrings[item], false)){
                            when {
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_temperature2) ->{
                                    val measurement: String
                                    var value = data[0].location.temperature.value.toDouble()
                                    val temperatureText = sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null)
                                    if (temperatureText == null || temperatureText == "˚C") {
                                        measurement = "˚C"
                                    }
                                    else {
                                        measurement = temperatureText
                                        value *= 33.8
                                    }

                                    listOfElements.add(
                                    NowElement(String.format("%.1f", value) + measurement, listOfStrings[item], "")
                                )}
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_weather) -> listOfElements.add(
                                    NowElement(data[1].location.precipitation.value + "mm", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_fog) -> listOfElements.add(
                                    NowElement(data[0].location.fog.percent + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_cloudiness) -> listOfElements.add(
                                    NowElement(data[0].location.cloudiness.percent + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_humidity) -> listOfElements.add(
                                    NowElement(data[0].location.humidity.value + "%", listOfStrings[item], "")
                                )
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_pressure2) ->{
                                    val measurement: String?
                                    var value = data[0].location.pressure.value.toDouble()
                                    val pressureText = sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null)
                                    if (pressureText == null || pressureText == "HPa") {
                                        measurement =  "HPa"
                                    }
                                    else if(pressureText == "bar") {
                                        measurement = pressureText
                                        value *= 0.001
                                    }
                                    else if(pressureText == "mmHg"){
                                        measurement = pressureText
                                        value *=  0.75006
                                    }
                                    else{
                                        measurement = pressureText
                                    }
                                    listOfElements.add(
                                    NowElement(String.format("%.1f", value) + measurement, listOfStrings[item], "")
                                )}
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                    wind = value
                    risiko = calculaterisk().toInt()
                    seekbar.progress = risiko
                    seekbar.refreshDrawableState()

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
                    wavehight = data?.significantTotalWaveHeight?.content.toString().toDouble()
                    listOfElements.add(NowElement(data?.significantTotalWaveHeight?.content + "m", resources.getString(R.string.navigation_drawer_wave), ""))
                    adapter.notifyDataSetChanged()
                    risiko = calculaterisk().toInt()
                    seekbar.progress = risiko
                    seekbar.refreshDrawableState()
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                Toast.makeText(context, "Response failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private  fun calculaterisk():Double{
        val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
        val max =100.0
        val min =0.0
        when(ceMarkText){
            "A - Vindstyrke: < 20,8sm Bølgehøyde: < 4m"-> {
                val farevind: Double = (wind/32.6)*100 //m/s valgte disse verdiene Beauforts skala hvor de skal tåle opp mot orkan
                val farebolge: Double =(wavehight/16)*100   //m
                if(farevind >=100 || farebolge >= 100) return max
                else if(farebolge > farevind) return farebolge
                else return farevind
            }
            "B - Vindstyrke: 20,7sm Bølgehøyde: 4m"-> {
                val farevind: Double = (wind/20.7)*100 //m/s
                val farebolge: Double  = (wavehight/4)*100 //m
                if(farevind >=100 || farebolge >= 100) return max
                else if(farebolge > farevind) return farebolge
                else return farevind
            }
            "C - Vindstyrke: 13,8sm Bølgehøyde: 2m"-> {
                val farevind: Double = (wind/13.8)*100 //m/s
                val farebolge: Double  = (wavehight/2)*100 //m
                if(farevind >=100 || farebolge >= 100) return max
                else if(farebolge > farevind) return farebolge
                else return farevind

            }
            "D - Vindstyrke: > 7,7sm Bølgehøyde: 0,3m"-> {
                val farevind: Double = (wind/7.7)*100 //m/s
                val farebolge: Double  = (wavehight/0.3)*100 //m
                if(farevind >=100 || farebolge >= 100) return max
                else if(farebolge > farevind)
                    return farebolge
                else return farevind
            }
        }
        return min
    }
}


