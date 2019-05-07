package com.example.sea.ui.weekly

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.ui.base.BaseInteractor
import kotlin.concurrent.thread

class WeeklyInteractor(context: Context, fileName: String) : WeeklyContract.Interactor, BaseInteractor(context, fileName) {
    override fun getData(finished: WeeklyContract.Interactor.OnFinished, latitude: Float, longitude: Float) {
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(latitude, longitude, null)
        val oceanCall = client.getOceanData(latitude.toDouble(), longitude.toDouble())

        thread {
            val locationData = locationCall.execute().body()
            if(locationData != null) {
                finished.onFinished(locationData)
            }

            val oceanData = oceanCall.execute().body()
            if(oceanData != null) {
                finished.onFinished(oceanData)
            }
        }

    }
}