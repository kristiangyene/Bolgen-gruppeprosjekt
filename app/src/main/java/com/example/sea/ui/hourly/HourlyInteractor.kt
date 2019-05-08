package com.example.sea.ui.hourly

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.ui.base.BaseInteractor
import retrofit2.Call
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

        thread {
            val locationData = locationCall.execute().body()
            if(locationData != null) {
                finished.onFinished(locationData)
            }

            val oceanData = oceanCall.execute().body()
            if(oceanData != null) {
                finished.onFinished(oceanData)
            }

            if(harbor != null) {
                val tidalData = tidalCall!!.execute().body()
                if(tidalData != null) {
                    finished.onFinished(tidalData)
                }
            }
        }

    }
}