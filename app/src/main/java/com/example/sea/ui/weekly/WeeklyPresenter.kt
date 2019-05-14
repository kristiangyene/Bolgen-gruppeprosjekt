package com.example.sea.ui.weekly

import android.annotation.SuppressLint
import com.example.sea.data.remote.model.OceanData
import com.example.sea.data.remote.model.LocationData
import java.text.SimpleDateFormat

class WeeklyPresenter(view: WeeklyContract.View, private var interactor: WeeklyContract.Interactor) : WeeklyContract.Presenter, WeeklyContract.Interactor.OnFinished {
    private var oceanDone = false
    private var locationDone = false

    override fun updateView() {
        view!!.updateRecyclerView()
    }

    private var view : WeeklyContract.View? = view

    override fun onDestroy() {
        view = null
    }

    override fun fetchData(onStart: Boolean) {
        view!!.showProgress()

        if(onStart) {
            interactor.fetchData(this, interactor.getUserLatitude(), interactor.getUserLongitude())
        }
        else {
            interactor.fetchData(this, interactor.getUserLatitude(), interactor.getUserLongitude())
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onFinished(data: OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val oceanForecast = data?.forecast

        // Finner fram til bølgedata for time '12' og plasserer dette i weeklyElement for tilsvarende dag.
        if (oceanForecast != null) {
            for(time in oceanForecast) {
                val date = formatterFrom.parse(time.oceanForecast.validTime.timePeriod.begin)
                val hour = formatToHour.format(date)
                if (hour == "12") {
                    val day = formatToDay.format(date)
                    for (x in view!!.getList()) {
                        if (x.day.equals(day)) {
                            val waveValue = time.oceanForecast.significantTotalWaveHeight
                            // gir 'warning' men kræsjer om vi fjerner den
                            if(waveValue != null) {
                                x.waves = waveValue.content + " m"
                            }
                        }
                    }
                }
            }
        }

        oceanDone = true
        if(oceanDone && locationDone) {
            view!!.hideProgress()
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onFinished(data: LocationData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val formatToDay = SimpleDateFormat("dd")
        val formatToHour = SimpleDateFormat("H")
        val formatToDayText = SimpleDateFormat("EEE")

        val locationForecast = data?.product?.time!!
        val checkList = ArrayList<Int>()

        // Finner fram til vinddata for time '12' og plasserer dette i et nytt weeklyElement
        for (time in locationForecast) {
            val date = formatterFrom.parse(time.to)
            val hour = formatToHour.format(date)
            if (hour == "12") {
                val day = formatToDay.format(date)
                val dayText = formatToDayText.format(date).capitalize()

                var windSpeed = time.location?.windSpeed?.mps

                // Sjekker om data for samme tidspunkt ikke er allerede lagt til.
                if (day.toInt() !in checkList) {
                    checkList.add(day.toInt())
                    var windMeasurement: String
                    val windText = interactor.getWindUnit()

                    if (windText == null || windText == "mps") {
                        windMeasurement =  "mps"
                    }
                    else if(windText == "mph") {
                        windMeasurement = windText
                        windSpeed = (windSpeed.toDouble() * 2.236936).toString()
                    }
                    else {
                        windMeasurement = windText
                        windSpeed = (windSpeed.toDouble() * 3.6).toString()
                    }

                    view!!.setDataInRecyclerView(WeeklyElement(dayText, day, "${"%.1f".format(windSpeed.toDouble())} $windMeasurement", "-"))
                }
            }
        }

        locationDone = true
        if(oceanDone && locationDone) {
            view!!.hideProgress()
        }
    }

    override fun onFailure(t: String?) {
        view!!.onFailure(t)
    }
}
