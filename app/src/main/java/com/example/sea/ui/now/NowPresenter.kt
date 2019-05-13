package com.example.sea.ui.now

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.sea.data.remote.model.LocationData
import com.example.sea.R
import com.example.sea.data.remote.model.OceanData
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

class NowPresenter(view: NowContract.View, context: Context, private var interactor: NowContract.Interactor) : NowContract.Presenter, NowContract.Interactor.OnFinished {
    private var view: NowContract.View? = view
    private var context : Context? = context
    private var waveValue : Double? = null
    private var closestHarbor : String? = null
    private var closestHarborValue = Double.MAX_VALUE
    private var oceanDone = false
    private var locationDone = false
    private var riskValuesDone = false
    private var tidalDone = false
    private var tidalSelected = false
    private var tidalNear = false
    private var firstUse: Boolean = true

    override fun fetchData(onFirstStart: Boolean) {
        //Henter ut data fra LocationForecast api.
        view!!.showProgress()
        if(onFirstStart) {
            requestOceanData(interactor.getUserLatitude(), interactor.getUserLatitude())
            requestLocationData(interactor.getUserLatitude(), interactor.getUserLongitude())

            if(interactor.getWeatherPreference(context!!.getString(R.string.navigation_drawer_tide))) {
                tidalSelected = true
                requestTidalData(interactor.getUserLatitude(), interactor.getUserLongitude())
            }
        }
        else {
            requestOceanData(interactor.getLatitude(), interactor.getLongitude())
            requestLocationData(interactor.getLatitude(), interactor.getLongitude())

            if(interactor.getWeatherPreference(context!!.getString(R.string.navigation_drawer_tide))) {
                tidalSelected = true
                requestTidalData(interactor.getLatitude(), interactor.getLongitude())
            }
        }
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
        // Endrer retningen til norsk.
        windDirection = windDirection.replace("E", context!!.getString(R.string.east))
        windDirection = windDirection.replace("W", context!!.getString(R.string.west))

        var pos = 1
        if(view!!.getList().size == 0) {
            view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + " " + measurement, context!!.resources.getString(R.string.navigation_drawer_wind), windDirection))
        }
        else {
            if(view!!.getList().size > 1 && view!!.getList()[1].image == context!!.resources.getString(R.string.navigation_drawer_tide)) {
                view!!.setDataInRecyclerViewPosition(pos++, NowElement(String.format("%.1f", value) + " " + measurement, context!!.resources.getString(R.string.navigation_drawer_wind), windDirection))
            }
            else {
                view!!.setDataInRecyclerViewPosition(pos, NowElement(String.format("%.1f", value) + " " + measurement, context!!.resources.getString(R.string.navigation_drawer_wind), windDirection))
            }
        }

        var visibility =  context!!.getString(R.string.good_visibility)
        if(nowData[0].location.fog.percent.toDouble() > 25.0) {
            visibility = context!!.getString(R.string.bad_visibility)
        }
        view!!.setDataInRecyclerViewPosition(pos++, NowElement(visibility, context!!.resources.getString(R.string.navigation_drawer_visibility),null))

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

                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_weather) -> {
                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(nowData[1].location.precipitation.value + " mm", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_fog) -> {
                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(nowData[0].location.fog.percent + " %", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_cloudiness) -> {
                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(nowData[0].location.cloudiness.percent + " %", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_humidity) -> {
                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(nowData[0].location.humidity.value + " %", listOfStrings[item], ""))
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

                        view!!.setDataInRecyclerViewPosition(pos++, NowElement(String.format("%.1f", value) + " " + measurement, listOfStrings[item], ""))
                    }
                }
            }
        }

        if(!riskValuesDone) {
            setUpSeekbarValues()
            riskValuesDone = true
        }

        //Oppdatere trygghetsskala.
        calculateWindRisk(wind)

        locationDone = true
        if((oceanDone && !tidalSelected) || (oceanDone && !tidalNear) || (oceanDone && tidalNear && tidalSelected)) {
            view!!.hideProgress()
            view!!.updateRecyclerView()
        }
    }

    override fun onFinished(data: OceanData.Forecast.OceanForecast.OceanValue?) {
        if(data != null) {
            waveValue = data.content.toDouble()
            view!!.setDataInRecyclerViewStart(NowElement(data.content + " m", context!!.getString(R.string.navigation_drawer_wave), ""))
            calculateWavesRisk(data.content.toDouble())
        }
        else {
            waveValue = 0.0
            view!!.setDataInRecyclerViewStart(NowElement("-", context!!.getString(R.string.navigation_drawer_wave), ""))
            view!!.setSeekbarProgress(0)
        }

        if(!riskValuesDone) {
            setUpSeekbarValues()
            riskValuesDone = true
        }

        oceanDone = true
        if((locationDone && !tidalSelected) || (locationDone && !tidalNear) || (locationDone && tidalNear && tidalSelected)) {
            view!!.hideProgress()
            view!!.updateRecyclerView()
        }
    }

    // Viser tidevann for nåtid.
    override fun onFinished(data : String?) {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY).toString()
        var foundStart = false
        var startIndex = 0
        val line = data!!.split("\n")

        for (i in 8 until line.size - 1) {
            val number = line[i].split("\\s+".toRegex())
            if (number[4] != currentHour) {
                continue
            }
            else {
                foundStart = true
                startIndex = i
                break
            }
        }
        if (foundStart) {
            if (line[startIndex][line[startIndex].length - 6] == ' ') {
                view!!.setDataInRecyclerView(NowElement(line[startIndex].substring(line[startIndex].length - 5, line[startIndex].length), context!!.getString(R.string.navigation_drawer_tide), ""))
            }
            else {
                view!!.setDataInRecyclerView(NowElement(line[startIndex].substring(line[startIndex].length - 6, line[startIndex].length), context!!.getString(R.string.navigation_drawer_tide), ""))
            }
        }

        if(!riskValuesDone) {
            setUpSeekbarValues()
            riskValuesDone = true
        }

        tidalDone = true
        if(oceanDone && locationDone) {
            view!!.hideProgress()
            view!!.updateRecyclerView()
        }
    }

    override fun onFailure(t: String?) {
        if(t != null) {
            view!!.onFailure(t)
        }
    }

    override fun onDestroy() {
        view = null
    }

    override fun requestOceanData(latitude : Float, longitude : Float) {
        interactor.getOceanData(this, latitude , longitude)
    }

    override fun requestLocationData(latitude : Float, longitude : Float) {
        interactor.getLocationData(this, latitude , longitude)
    }

    override fun requestTidalData(latitude: Float, longitude: Float) {
        findNearestHarbor(interactor.getLatitude(), interactor.getLongitude())
        if(closestHarbor != null && closestHarborValue < 30000) {
            interactor.getTidalData(this, latitude , longitude, closestHarbor!!)
        }
    }

    override fun calculateWindRisk(value: Double?) {
        if(firstUse) {
            view!!.setSeekbarProgress(0)
            firstUse = false
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
        if(firstUse) {
            view!!.setSeekbarProgress(0)
            firstUse = false
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

    //Finner den nærmeste havna.
    override fun findNearestHarbor(latitude: Float, longitude: Float) {
        val currentLocation = Location("")
        val harbors = hashMapOf<String, LatLng>()

        currentLocation.latitude = latitude.toDouble()
        currentLocation.longitude = longitude.toDouble()

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

        for(harbor in harbors) {
            val harborLocation = Location("")
            harborLocation.latitude = harbor.value.latitude
            harborLocation.longitude = harbor.value.longitude
            val distance = currentLocation.distanceTo(harborLocation).toDouble()

            if (distance < closestHarborValue) {
                closestHarborValue = distance
                closestHarbor = harbor.key
                tidalNear = true
            }
        }
    }

    fun setUpSeekbarValues() {
        val screenWidthInPixels = context!!.resources.displayMetrics.widthPixels
        val pixelsPerNumber = (screenWidthInPixels.toDouble()-16)/10
        var numberWidth = ((pixelsPerNumber-3)/16).toInt()
        var str = "0".padEnd(numberWidth) + "10".padEnd(numberWidth) + "20".padEnd(numberWidth) + "30".padEnd(numberWidth) + "40".padEnd(numberWidth) + "50".padEnd(numberWidth) + "60".padEnd(numberWidth) + "80".padEnd(numberWidth) + "80".padEnd(numberWidth) + "90".padEnd(numberWidth) + "100"
        view!!.updateTextScale(str)
        while(view!!.getTextScaleLines() == 1) {
            numberWidth++
            str = "0".padEnd(numberWidth) + "10".padEnd(numberWidth) + "20".padEnd(numberWidth) + "30".padEnd(numberWidth) + "40".padEnd(numberWidth) + "50".padEnd(numberWidth) + "60".padEnd(numberWidth) + "80".padEnd(numberWidth) + "80".padEnd(numberWidth) + "90".padEnd(numberWidth) + "100"
            view!!.updateTextScale(str)
        }

        if(view!!.getTextScaleLines() != 1) {
            numberWidth--
            str = "0".padEnd(numberWidth) + "10".padEnd(numberWidth) + "20".padEnd(numberWidth) + "30".padEnd(numberWidth) + "40".padEnd(numberWidth) + "50".padEnd(numberWidth) + "60".padEnd(numberWidth) + "80".padEnd(numberWidth) + "80".padEnd(numberWidth) + "90".padEnd(numberWidth) + "100"
            view!!.updateTextScale(str)
        }
    }
}