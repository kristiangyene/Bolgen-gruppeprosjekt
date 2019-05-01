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


class NowFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private var recyclerView: RecyclerView? = null
    lateinit var adapter: NowAdapter
    private val listOfElements = ArrayList<NowElement>()
    private lateinit var rootView: View
    private lateinit var listOfStrings: ArrayList<String>
    private var waveheight: Double = 0.0
    private var wind: Double = 0.0
    private var risiko: Int = 0
    private lateinit var seekbar: SeekBar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_now, container, false)
        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        setViews(rootView)
        recyclerView!!.layoutManager = GridLayoutManager(context, 1)
        adapter = NowAdapter(listOfElements)
        recyclerView!!.adapter = adapter
        fetchData()
        return rootView
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setViews(rootView: View){
        recyclerView = rootView.findViewById(R.id.recycler_view)
        seekbar = rootView.findViewById(R.id.seekbar)
        //Fryser trygghetsskalaen.
        seekbar.setOnTouchListener { _, _ -> true }
    }


    private fun fetchData(){
        fetchOceanData(sharedPreferences.getFloat("lat", 60.0F), sharedPreferences.getFloat("long", 11F))
        fetchLocationData(sharedPreferences.getFloat("lat", 60.0F), sharedPreferences.getFloat("long", 11F))
    }


    //Henter ut data fra LocationForecast api.
    private fun fetchLocationData(latitude: Float, longitude: Float) {
        val call = RetrofitClient().getClient("json").getLocationData(latitude, longitude, null)
        /*Enqueue() sender asynkront forespørselen og gir beskjed om appen din med tilbakekalling når et svar kommer
        tilbake: onresponse dersom man får respons, og onfailure om ikke. Siden denne forespørselen er asynkron,
        håndterer Retrofit den på en bakgrunnstråd.*/
        call.enqueue(object : retrofit2.Callback<LocationData> {

            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>){
                if (response.isSuccessful && response.code() == 200){
                    //Henter ut data kun én gang.
                    val data = response.body()?.product?.time!!
                    var measurement: String
                    var value = data[0].location.windSpeed.mps.toDouble()
                    val windText = sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null)
                    if (windText == null || windText == "mps") measurement =  "mps"
                    else if(windText == "mph"){
                        measurement = windText
                        value *= 2.236936
                    }else{
                        measurement = windText
                        value *= 3.6
                    }
                    var windDirection = data[0].location.windDirection.name
                    //Endrer retningen til norsk.
                    windDirection = windDirection.replace("E", "Ø")
                    windDirection = windDirection.replace("W", "V")
                    listOfElements.add(NowElement(String.format("%.1f", value) + " " + measurement, resources.getString(R.string.navigation_drawer_wind), windDirection))
                    var visibility =  "God sikt"
                    if(data[0].location.fog.percent.toDouble() > 25.0) visibility = "Dårlig sikt"
                    listOfElements.add(NowElement(visibility, getString(R.string.navigation_drawer_visibility),null))
                    listOfStrings = arrayListOf(
                        getString(R.string.navigation_drawer_tide),
                        getString(R.string.navigation_drawer_temperature2),
                        getString(R.string.navigation_drawer_weather),
                        getString(R.string.navigation_drawer_fog),
                        getString(R.string.navigation_drawer_humidity),
                        getString(R.string.navigation_drawer_cloudiness),
                        getString(R.string.navigation_drawer_pressure2))
                    for(item in 0 until listOfStrings.size){
                        if(sharedPreferences.getBoolean(listOfStrings[item], false)){
                            when {
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_temperature2) ->{
                                    value = data[0].location.temperature.value.toDouble()
                                    val temperatureText = sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null)
                                    if (temperatureText == null || temperatureText == "˚C") {
                                        measurement = "˚C"
                                    }
                                    else {
                                        measurement = temperatureText
                                        value = (value * 1.8) + 32
                                    }

                                    listOfElements.add(
                                        NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], "")
                                    )}
                                listOfStrings[item] == resources.getString(R.string.navigation_drawer_weather) -> listOfElements.add(
                                    NowElement(data[1].location.precipitation.value + " " + "mm", listOfStrings[item], "")
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
                                    value = data[0].location.pressure.value.toDouble()
                                    val pressureText = sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null)
                                    if (pressureText == null || pressureText == "hPa") {
                                        measurement =  "hPa"
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
                                        NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], "")
                                    )}
                            }
                        }
                    }
                    //Oppdaterer adapter og trygghetsskala.
                    adapter.notifyDataSetChanged()
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


    //Henter ut data fra OceanForecast api.
    private fun fetchOceanData(latitude: Float, longitude: Float) {

        val call = RetrofitClient().getClient("json").getOceanData(latitude, longitude)
        call.enqueue(object : retrofit2.Callback<OceanData> {

            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>){
                if (response.isSuccessful && response.code() == 200){
                    val data = response.body()?.forecast?.get(0)?.oceanForecast
                    if(data?.significantTotalWaveHeight?.content != null){
                        waveheight = data?.significantTotalWaveHeight?.content.toString().toDouble()
                        listOfElements.add(NowElement("$waveheight m", resources.getString(R.string.navigation_drawer_wave), ""))
                    }
                    else{
                        waveheight = 0.0
                        listOfElements.add(NowElement("-", resources.getString(R.string.navigation_drawer_wave), ""))
                    }
                    //Oppdaterer adapter og trygghetsskala.
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



    /*
    Metode som kalkurerer om det er trygt eller ikke på nåværende/valgt posisjon for trygghetsskalaen. Trygghetsskalaen
    regnes ut fra vind og bølgehøyde, og den prioriterer den av de som har høyest måleverdi.
     */
   private  fun calculaterisk():Double{
        val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
        val max =100.0
        val min =0.0
        when(ceMarkText){
            "A - Vindstyrke: < 20,8sm Bølgehøyde: < 4m"-> {
                //valgte disse verdiene for CE-merke A fra Beauforts skala hvor de skal tåle opp mot orkan.
                val dangerWind: Double = (wind/32.6)*100 //m/s
                val dangerWave: Double =(waveheight/16)*100 //m
                return if(dangerWind >=100 || dangerWave >= 100) max
                else if(dangerWave > dangerWind) dangerWave
                else dangerWind
            }
            "B - Vindstyrke: 20,7sm Bølgehøyde: 4m"-> {
                val dangerWind: Double = (wind/20.7)*100 //m/s
                val dangerWave: Double  = (waveheight/4)*100 //m
                return if(dangerWind >=100 || dangerWave >= 100) max
                else if(dangerWave > dangerWind) dangerWave
                else dangerWind
            }
            "C - Vindstyrke: 13,8sm Bølgehøyde: 2m"-> {
                val dangerWind: Double = (wind/13.8)*100 //m/s
                val dangerWave: Double  = (waveheight/2)*100 //m
                return if(dangerWind >=100 || dangerWave >= 100) max
                else if(dangerWave > dangerWind) dangerWave
                else dangerWind

            }
            "D - Vindstyrke: > 7,7sm Bølgehøyde: 0,3m"-> {
                val dangerWind: Double = (wind/7.7)*100 //m/s
                val dangerWave: Double  = (waveheight/0.3)*100 //m
                return if(dangerWind >=100 || dangerWave >= 100) max
                else if(dangerWave > dangerWind)
                    dangerWave
                else dangerWind
            }
        }
        return min
    }
}



