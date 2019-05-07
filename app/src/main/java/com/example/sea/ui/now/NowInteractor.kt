package com.example.sea.ui.now

import android.content.Context
import android.location.Location
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.OceanData
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Response


class NowInteractor(context: Context, fileName: String) : NowContract.Interactor, BaseInteractor(context, fileName) {
    private val harbors = hashMapOf<String, LatLng>()
    private var closestHarbor : String? = null
    private var closestHarborValue = Double.MAX_VALUE


    //Henter ut data fra OceanForecast api.
    override fun getOceanData(finished : NowContract.Interactor.OnFinished, latitude : Double, longitude : Double) {
        val call = RetrofitClient().getClient("json").getOceanData(latitude, longitude)
        call.enqueue(object : retrofit2.Callback<OceanData> {

            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>){
                if (response.isSuccessful && response.code() == 200){
                    finished.onFinished(response.body()?.forecast?.get(0)?.oceanForecast?.significantTotalWaveHeight)
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                finished.onFailure(t)
            }
        })
    }

    /*
     Enqueue() sender asynkront forespørselen og gir beskjed om appen din med tilbakekalling når et svar kommer
     tilbake: onresponse dersom man får respons, og onfailure om ikke. Siden denne forespørselen er asynkron,
     håndterer Retrofit den på en bakgrunnstråd.
     */
    override fun getLocationData(finished : NowContract.Interactor.OnFinished, latitude : Float, longitude : Float) {
        val call = RetrofitClient().getClient("json").getLocationData(latitude, longitude, null)
        call.enqueue(object : retrofit2.Callback<LocationData> {

            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>){
                if (response.isSuccessful && response.code() == 200){
                    finished.onFinished(response.body())
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                finished.onFailure(t)
            }
        })
    }

    //Henter data om tidevann dersom det er en havn i nærheten.
    override fun getTidalData(finished : NowContract.Interactor.OnFinished, latitude: Float, longitude: Float) {
        setClosestHarbor(latitude, longitude)
            val call = RetrofitClient().getClient("string").getTidalWater(closestHarbor!!)
            call.enqueue(object : retrofit2.Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if ((response.isSuccessful && response.code() == 200) && (closestHarbor != null && closestHarborValue < 30000)) {
                        finished.onFinished(response.body())
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    finished.onFailure(t)
                }
            })
        }


    //Finner den nærmeste havna.
    override fun setClosestHarbor(latitude: Float, longitude: Float) {
        val currentLocation = Location("")
        currentLocation.latitude = latitude.toDouble()
        currentLocation.longitude = longitude.toDouble()
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
    }
}