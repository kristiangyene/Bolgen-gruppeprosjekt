package com.example.sea.now

import android.content.Context
import com.example.sea.map.LocationData
import com.example.sea.R
import com.example.sea.service.model.OceanData

class NowPresenter(view: NowContract.View, context: Context, private var interactor: NowContract.Interactor) : NowContract.Presenter, NowContract.Interactor.OnFinished {
    private var view: NowContract.View? = view
    private var context : Context? = context

    override fun fetchData() {
        requestLocationData(interactor.getLatitude(), interactor.getLongitude())
//        requestOceanData(interactor.getLatitude().toDouble(), interactor.getLongitude().toDouble())
        requestOceanData(60.10, 5.0)
    }

    override fun onFinished(data: LocationData?) {
        val listOfStrings: ArrayList<String> = arrayListOf(context!!.getString(R.string.navigation_drawer_tide), context!!.getString(R.string.navigation_drawer_temperature2), context!!.getString(R.string.navigation_drawer_weather), context!!.getString(R.string.navigation_drawer_fog), context!!.getString(R.string.navigation_drawer_humidity), context!!.getString(R.string.navigation_drawer_cloudiness), context!!.getString(R.string.navigation_drawer_pressure2))

        val nowData = data?.product?.time!!
        val wind : Double?
        var measurement: String
        var value = nowData[0].location.windSpeed.mps.toDouble()
        val windText = interactor.getWindUnit()
        if (windText == null || windText == "Km/h") {
            measurement =  "Km/h"
            value *= 3.6
        }
        else if(windText == "Mph"){
            measurement = windText
            value *= 2.236936
        }
        else {
            measurement = windText
        }

        wind = value
        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + measurement, context!!.resources.getString(R.string.navigation_drawer_wind), nowData[0].location.windDirection.name))

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
                            value *= 33.8
                        }

                        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + measurement, listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_weather) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[1].location.precipitation.value + "mm", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_fog) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.fog.percent + "%", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_cloudiness) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.cloudiness.percent + "%", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_humidity) -> {
                        view!!.setDataInRecyclerView(NowElement(nowData[0].location.humidity.value + "%", listOfStrings[item], ""))
                    }
                    listOfStrings[item] == context!!.resources.getString(R.string.navigation_drawer_pressure2) -> {
                        value = nowData[0].location.pressure.value.toDouble()

                        val pressureText = interactor.getPressureUnit()
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

                        view!!.setDataInRecyclerView(NowElement(String.format("%.1f", value) + measurement, listOfStrings[item], ""))
                    }
                }
            }
        }

        view!!.updateRecyclerView()
        calculateWindRisk(wind)
    }

    override fun onFinished(data: OceanData.Forecast.OceanForecast.OceanValue?) {
        view!!.setDataInRecyclerView(NowElement(data?.content + "m", context!!.getString(R.string.navigation_drawer_wave), ""))
        view!!.updateRecyclerView()
        calculateWavesRisk(data?.content?.toDouble())
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

    override fun calculateWindRisk(value: Double?) {
        val risk = calculateRisk(value, "wind")
        view!!.setSeekbarProgress(risk)
    }

    override fun calculateWavesRisk(value : Double?) {
        val risk = calculateRisk(value, "waves")
        view!!.setSeekbarProgress(risk)
    }

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

        when(text){
            "A"-> {
                risk = if(content == "wind") {
                    (value/32.6)*100
                } else {
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
                } else {
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
                } else {
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
                } else {
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