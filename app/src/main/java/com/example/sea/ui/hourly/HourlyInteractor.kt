package com.example.sea.ui.hourly

import android.content.Context
import android.util.Log
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.HourlyModel
import com.example.sea.ui.base.BaseInteractor
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class HourlyInteractor(context: Context, fileName: String) : HourlyContract.Interactor, BaseInteractor(context, fileName) {

    override fun fetchData(finished: HourlyContract.Interactor.OnFinished, latitude: Float, longitude: Float, harbor: String?) {
        Log.d("Kart", "$latitude, $longitude")
        val clientJson = RetrofitClient().getClient("json")
        val clientString = RetrofitClient().getClient("string")

        val location = clientJson
            .getLocationData(latitude, longitude, null)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val ocean = clientJson
            .getOceanData(latitude, longitude)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        var tidal: Observable<String>? = null
        if(harbor != null) {
            tidal = clientString
                .getTidalWater(harbor)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        }

        val combined: Observable<HourlyModel>
        combined = if(harbor != null) {
            Observable.zip(location, ocean, tidal) { locationData, oceanData, tidalData -> HourlyModel(locationData, oceanData, tidalData) }
        }
        else {
            Observable.zip(location, ocean) { locationData, oceanData -> HourlyModel(locationData, oceanData, null) }
        }

        combined.subscribe(object : Subscriber<HourlyModel>() {
            override fun onNext(data: HourlyModel?) {
                finished.onFinished(data?.locationData)
                finished.onFinished(data?.oceanData)

                if(harbor != null) {
                    finished.onFinished(data?.tidalData)
                }
            }

            override fun onCompleted() {
                finished.updateView()
            }

            override fun onError(t: Throwable) {
                finished.onFailure(t.message)

                if(t is HttpException) {
                    finished.onFailure(t.response().errorBody()?.string())
                }
            }
        })
    }
}