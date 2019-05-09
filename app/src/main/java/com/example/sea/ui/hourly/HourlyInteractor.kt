package com.example.sea.ui.hourly

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData
import com.example.sea.ui.base.BaseInteractor
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread

class HourlyInteractor(context: Context, fileName: String) : HourlyContract.Interactor, BaseInteractor(context, fileName) {
    override fun getData(finished: HourlyContract.Interactor.OnFinished, latitude: Float, longitude: Float, harbor: String?) {
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(latitude, longitude, null)
        val oceanCall = client.getOceanData(latitude.toDouble(), longitude.toDouble())
        var tidalCall : Call<String>? = null
        if(harbor != null) {
            tidalCall = RetrofitClient().getClient("string").getTidalWater(harbor)
        }

        /*thread {
            val locationData = locationCall.execute().body()
            if(locationData != null) {
                finished.onFinished(locationData)
            }

            val oceanData = oceanCall.execute().body()
            if(oceanData != null) {
                finished.onFinished(oceanData)
            }

            //Thread.sleep(1000)
           // HourlyContract.Interactor!!.View!!.updateRecyclerView()
            if(harbor != null) {
                val tidalData = tidalCall!!.execute().body()
                if(tidalData != null) {
                    finished.onFinished(tidalData)
                }
            }

        }*/
        val callLocation = client.getLocationData(latitude, longitude, null)
        callLocation.enqueue(object : retrofit2.Callback<LocationData> {

            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    finished.onFinished(response.body())
                    getOceanData(latitude, longitude, finished)
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                //finished.onFailure(t)
            }
        })
    }
    fun getOceanData(latitude: Float, longitude: Float, finished: HourlyContract.Interactor.OnFinished) {
        val client = RetrofitClient().getClient("json")
        val callOcean = client.getOceanData(latitude.toDouble(), longitude.toDouble())
        callOcean.enqueue(object : retrofit2.Callback<OceanData> {

            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>){
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    finished.onFinished(response.body())
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                //finished.onFailure(t)
            }
        })
    }
}