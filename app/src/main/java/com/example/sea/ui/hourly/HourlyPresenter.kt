package com.example.sea.ui.hourly

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.sea.R
import com.example.sea.data.remote.model.OceanData
import com.example.sea.data.remote.model.LocationData
import com.google.android.gms.maps.model.LatLng
import com.google.type.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class HourlyPresenter(view: HourlyContract.View, private var context: Context, private var interactor: HourlyContract.Interactor) : HourlyContract.Presenter, HourlyContract.Interactor.OnFinished {
    private var view : HourlyContract.View? = view
    private var startTimeFound = false
    private var startTime : String? = null
    private var correcTime = false
    override fun onDestroy() {
        view = null
    }

    override fun fetchData() {
        val harbor = findNearestHarbor()
        interactor.setData(this, interactor.getLatitude(), interactor.getLongitude(), harbor)
    }

    private fun findNearestHarbor() : String? {
        val harbors = hashMapOf<String, LatLng>()
        var closestHarbor: String? = null
        var closestHarborValue = Double.MAX_VALUE

        val currentLocation = Location("")
        currentLocation.latitude = interactor.getLatitude().toDouble()
        currentLocation.longitude = interactor.getLongitude().toDouble()

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
            }
        }
        return closestHarbor
    }

    @SuppressLint("SimpleDateFormat")
    override fun onFinished(data: OceanData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val formatTo = SimpleDateFormat("H")
        val output = data?.forecast
        var counter = 0
        if (output != null) {
            for (i in output) {
                val hour = i.oceanForecast.validTime.timePeriod.begin
                val from = formatterFrom.parse(hour)
                val wavesFormat = formatTo.format(from)

                for (x in view!!.getList()) {
                    if (x.title.equals("Kl $wavesFormat")) {
                        val typo = i.oceanForecast.significantTotalWaveHeight
                        if (typo != null) {
                            x.waves = typo.content + " m"
                            //view!!.getList()[counter++].waves = typo.content
                        }



                    }
                }
            }
        }
        view!!.updateRecyclerView()
        //return view
    }

    // Viser tidevann for de neste 24 timene
    override fun onFinished(data: String?) {
        var foundStart = false
        var startIndex = 0
        val line = data!!.split("\n")
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
                    view!!.getList()[counter++].tide = line[i].substring(line[i].length - 5, line[i].length)
                }
                else {
                    view!!.getList()[counter++].tide = line[i].substring(line[i].length - 6, line[i].length)
                }
            }
        }
        //view!!.updateRecyclerView()


    }

    @SuppressLint("SimpleDateFormat")
    override fun onFinished(data: LocationData?) {
        val formatterFrom = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //val check = SimpleDateFormat("yyyy-MM-dd")
        val formatTo = SimpleDateFormat("H")
        val output = data?.product?.time!!
        val checkList = ArrayList<Int>()
        //var now = Calendar.getInstance().time
        for (i in output) {
            val from = formatterFrom.parse(i.to)
            val toFormatted = formatTo.format(from)
            //val checked = check.format(from)
            var windSpeed = i.location?.windSpeed?.mps
            val fog = i.location?.fog?.percent
            var temp = i.location?.temperature?.value
            val humid = i.location?.humidity?.value
            val rainfall = output[1].location?.precipitation
            var visibility = context.getString(R.string.good_visibility)
            if (!startTimeFound) {
                startTime = toFormatted
                startTimeFound = true
            }
            //val pattern = SimpleDateFormat("EEE MMM d HH:mm:ss z+z:z yyyy")
            //val actualtime = pattern.parse(now.toString())
            //val gethour = formatTo.format(actualtime)

            val date = Date()
            val formatdate = formatTo.format(date)
            //Log.d("datefromcompare", formatdate)
            val difference = formatTo.format(from)
            //Log.d("datetocompare", difference)

            if(difference == formatdate && correcTime == false) correcTime = true
            else if(difference != formatdate && correcTime == false) continue

            if (toFormatted.toInt() !in checkList) {
                checkList.add(toFormatted.toInt())
                if (fog.toDouble() > 25.0) visibility = context.getString(R.string.bad_visibility)
                var windMeasurement: String
                val windText = interactor.getWindUnit()
                if (windText == null || windText == "mps") windMeasurement = "mps"
                else if (windText == "mph") {
                    windMeasurement = windText
                    windSpeed = (windSpeed.toDouble() * 2.236936).toString()
                } else {
                    windMeasurement = windText
                    windSpeed = (windSpeed.toDouble() * 3.6).toString()
                }
                var tempMeasurement: String
                val temperatureText = interactor.getTemperaturUnit()
                if (temperatureText == null || temperatureText == "˚C") {
                    tempMeasurement = "˚C"
                } else {
                    tempMeasurement = temperatureText
                    temp = ((temp.toDouble() * 1.8) + 32).toString()
                }
                view!!.setDataInRecyclerView(
                    HourlyElement(
                        "Kl $toFormatted",
                        "${"%.1f".format(windSpeed.toDouble())} $windMeasurement",
                        "-",
                        "$fog%",
                        "${"%.1f".format(temp.toDouble())} $tempMeasurement",
                        "-",
                        rainfall.value + " " + rainfall.unit,
                        visibility,
                        "$humid%"
                    )
                )

            }
        }
    }
}