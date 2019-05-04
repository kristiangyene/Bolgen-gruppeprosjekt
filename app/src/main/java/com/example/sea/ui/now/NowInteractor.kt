package com.example.sea.ui.now

import android.content.Context
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.OceanData
import retrofit2.Call
import retrofit2.Response

class NowInteractor(context: Context, fileName: String) : NowContract.Interactor, BaseInteractor(context, fileName) {
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
}