package com.example.sea.ui.weekly

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData
import com.example.sea.ui.base.BaseInteractor
import retrofit2.Call
import retrofit2.Response

class WeeklyInteractor(context: Context, fileName: String) : WeeklyContract.Interactor, BaseInteractor(context, fileName) {
    override fun getData(finished: WeeklyContract.Interactor.OnFinished, latitude: Float, longitude: Float) {
        val client = RetrofitClient().getClient("json")
        val callLocation = client.getLocationData(latitude, longitude, null)

        callLocation.enqueue(object : retrofit2.Callback<LocationData> {
            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    finished.onFinished(response.body())
                    getOceanData(latitude, longitude, finished)
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                finished.onFailure(t)
            }
        })

//        val callOcean = client.getOceanData(latitude.toDouble(), longitude.toDouble())
//        doAsync {
//            finished.onFinished((callLocation.execute().body()))
//            finished.onFinished((callOcean.execute().body()))
//            uiThread { finished.updateView() }
//        }
    }

    fun getOceanData(latitude: Float, longitude: Float, finished: WeeklyContract.Interactor.OnFinished) {
        val client = RetrofitClient().getClient("json")
        val callOcean = client.getOceanData(latitude.toDouble(), longitude.toDouble())

        callOcean.enqueue(object : retrofit2.Callback<OceanData> {
            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    finished.onFinished(response.body())
                    finished.updateView()
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                finished.onFailure(t)
            }
        })
    }
}