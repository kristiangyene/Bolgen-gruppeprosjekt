package com.example.sea.ui.map

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.LocationData
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.ui.main.MainContract
import retrofit2.Call
import retrofit2.Response

class MapInteractor(context: Context, fileName : String) : MainContract.Interactor, MapContract.Interactor, BaseInteractor(context, fileName) {
    private var sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun getFoundAddress() : Boolean {
        return sharedPreferences.getBoolean("address", false)
    }

    override fun setFoundAddress(value: Boolean) {
        sharedPreferences.edit().putBoolean("address", value).apply()
    }

    override fun getLocationData(finished : MapContract.Interactor.OnFinished, latitude : Float, longitude : Float) {
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