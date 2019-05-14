package com.example.sea.ui.now

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.sea.data.remote.model.LocationData
import com.example.sea.R
import com.example.sea.data.remote.model.OceanData
import java.util.*
import kotlin.collections.ArrayList

class NowPresenter(view: NowContract.View, context: Context, private var interactor: NowContract.Interactor) : NowContract.Presenter, NowContract.Interactor.OnFinished {
    private var view: NowContract.View? = view
    private var context : Context? = context
    private var waveValue : Double? = null
    private var FirstUse: Boolean = true

    override fun fetchData() {
        //Henter ut data fra LocationForecast api.
        view!!.setSeekbarProgress(0)
        requestLocationData(interactor.getLatitude(), interactor.getLongitude())
        requestOceanData(interactor.getLatitude().toDouble(), interactor.getLongitude().toDouble())
        requestTidalData(interactor.getLatitude(), interactor.getLongitude())
    }

    override fun onFinished(data: LocationData?) {
        val listOfStrings: ArrayList<String> = arrayListOf(context!!.getString(R.string.navigation_drawer_tide), context!!.getString(R.string.navigation_drawer_temperature2), context!!.getString(R.string.navigation_drawer_weather), context!!.getString(R.string.navigation_drawer_fog), context!!.getString(R.string.navigation_drawer_humidity), context!!.getString(R.string.navigation_drawer_cloudiness), context!!.getString(R.string.navigation_drawer_pressure2))
        val nowData = data?.product?.time!!
        val wind : Double?
        var measurement: String
        var value = nowData[0].location.windSpeed.mps.toDouble()
        val windText = interactor.getWindUnit()
        wind = value

        if (windText == null || windText == "mps") {
            measurement =  "mps"
        }
        else if(windText == "mph"){
            measurement = windText
            value *= 2.236936
        }
        else{
            measurement = windText
            value *= 3.6
        }

        var windDirection = nowData[0].location.windDirection.name
        //Endrer retningen til norsk.
        windDirection = windDirection.replace("E", context!!.getString(R.string.east))
        windDirection = windDirection.replace("W", context!!.getString(R.string.west))

        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + " " + measurement, context!!.resources.getString(R.string.navigation_drawer_wind), windDirection))

        var visibility =  context!!.getString(R.string.good_visibility)
        if(nowData[0].location.fog.percent.toDouble() > 25.0) {
            visibility = context!!.getString(R.string.bad_visibility)
        }
        view!!.setDataInRecyclerView(NowElement(visibility, context!!.resources.getString(R.string.navigation_drawer_visibility),null))

        for(item in 0 until listOfStrings.size){
            if(interactor.getWeatherPreference(listOfStrings[item])){
                when {
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_temperature2) ->{
                        value = nowData[0].location.temperature.value.toDouble()

                        val temperatureText = interactor.getTemperaturUnit()
                        if (temperatureText == null || temperatureText == "˚C") {
                            measurement = "˚C"
                        }
                        else {
                            measurement = temperatureText
                            value = (value * 1.8) + 32
                        }

                        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_weather) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[1].location.precipitation.value + " mm", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_fog) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.fog.percent + " %", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_cloudiness) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.cloudiness.percent + " %", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_humidity) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.humidity.value + " %", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_pressure2) -> {
                        value = nowData[0].location.pressure.value.toDouble()

                        val pressureText = interactor.getPressureUnit()
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

                        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], ""))
                    }
                }
            }
        }

        //Oppdaterer adapter og trygghetsskala.
        view!!.updateRecyclerView()
        calculateWindRisk(wind)
    }

    override fun onFinished(data: OceanData.Forecast.OceanForecast.OceanValue?) {
        if(data != null) {
            waveValue = data.content.toDouble()
            view!!.setDataInRecyclerView(NowElement(data.content + " m", context!!.getString(R.string.navigation_drawer_wave), ""))
            view!!.updateRecyclerView()
            calculateWavesRisk(data.content.toDouble())
        }
        else {
            waveValue = 0.0
            view!!.setDataInRecyclerView(NowElement("-", context!!.getString(R.string.navigation_drawer_wave), ""))
            view!!.updateRecyclerView()
        }
    }

    // Viser tidevann for nåtid.
    override fun onFinished(data : String?, harbor: String?, closestHarborValue: Double) {
        if(interactor.getWeatherPreference(context!!.getString(R.string.navigation_drawer_tide))) {
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY).toString()
            var foundStart = false
            var startIndex = 0
            val line = data!!.split("\n")

            for (i in 8 until line.size - 1) {
                val number = line[i].split("\\s+".toRegex())
                if (number[4] != currentHour) {
                    continue
                } else {
                    foundStart = true
                    startIndex = i
                    break
                }
            }
            if (foundStart && (harbor != null && closestHarborValue < 30000)) {
                if (line[startIndex][line[startIndex].length - 6] == ' ') {
                    view!!.setDataInRecyclerView(
                        NowElement(
                            line[startIndex].substring(
                                line[startIndex].length - 5,
                                line[startIndex].length
                            ), context!!.getString(R.string.navigation_drawer_tide), harbor.capitalize()
                        )
                    )
                } else {
                    view!!.setDataInRecyclerView(
                        NowElement(
                            line[startIndex].substring(
                                line[startIndex].length - 6,
                                line[startIndex].length
                            ), context!!.getString(R.string.navigation_drawer_tide), harbor.capitalize()
                        )
                    )
                }
            } else view!!.setDataInRecyclerView(
                NowElement(
                    "-",
                    context!!.getString(R.string.navigation_drawer_tide),
                    ""
                )
            )
        }
    }

    override fun onFailure(t: Throwable) {
        view!!.onFailure(t)
    }

    override fun onDestroy() {
        view = null
    }

    override fun requestOceanData(latitude : Double, longitude : Double) {
        interactor.getOceanData(this, latitude , longitude)
    }

    override fun requestLocationData(latitude : Float, longitude : Float) {
        interactor.getLocationData(this, latitude , longitude)
    }

    override fun requestTidalData(latitude: Float, longitude: Float) {
        interactor.getTidalData(this, latitude , longitude)
    }

    override fun calculateWindRisk(value: Double?) {
        if(FirstUse){
            view!!.setSeekbarProgress(0)
            FirstUse = false
        }
        if(waveValue != null && waveValue == 0.0) {
            view!!.setSeekbarProgress(0)
        }
        else {
            val risk = calculateRisk(value, "wind")
            view!!.setSeekbarProgress(risk)
        }
    }

    override fun calculateWavesRisk(value : Double?) {
        if(FirstUse){
            view!!.setSeekbarProgress(0)
            FirstUse = false
        }
        val risk = calculateRisk(value, "waves")
        view!!.setSeekbarProgress(risk)
    }

    /*
     Metode som kalkurerer om det er trygt eller ikke på nåværende/valgt posisjon for trygghetsskalaen. Trygghetsskalaen
     regnes ut fra vind og bølgehøyde, og den prioriterer den av de som har høyest måleverdi.
    */
    override fun calculateRisk(value: Double?, content : String): Int {
        val ceText = interactor.getCeMark()
        val text: String?
        val risk: Double?

        if(ceText == null) {
            return -1
        }

        if(value == null) {
            return -1
        }

        text = ceText.split(" ")[0]
        //valgte disse verdiene for CE-merke A fra Beauforts skala hvor de skal tåle opp mot orkan.
        when(text){
            "A"-> {
                risk = if(content == "wind") {
                    (value/32.6)*100
                }
                else {
                    (value/16)*100
                }

                if(risk > 100) {
                    return 100
                }
                else if(risk < 0) {
                    return 0
                }
                return risk.toInt()
            }
            "B"-> {
                risk = if(content == "wind") {
                    (value/20.7)*100
                }
                else {
                    (value/4)*100
                }

                if(risk > 100) {
                    return 100
                }
                else if(risk < 0) {
                    return 0
                }
                return risk.toInt()
            }
            "C"-> {
                risk = if(content == "wind") {
                    (value/13.8)*100
                }
                else {
                    (value/2)*100
                }

                if(risk > 100) {
                    return 100
                }
                else if(risk < 0) {
                    return 0
                }
                return risk.toInt()
            }
            "D"-> {

                risk = if(content == "wind") {
                    (value/7.7)*100
                }
                else {
                    (value/0.3)*100
                }

                if(risk > 100) {
                    return 100
                }
                else if(risk < 0) {
                    return 0
                }
                return risk.toInt()
            }
        }
        return -1
    }
}