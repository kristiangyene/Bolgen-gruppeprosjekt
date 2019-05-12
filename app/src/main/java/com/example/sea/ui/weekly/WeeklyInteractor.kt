package com.example.sea.ui.weekly

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.WeeklyModel
import com.example.sea.ui.base.BaseInteractor
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.Observable

class WeeklyInteractor(context: Context, fileName: String) : WeeklyContract.Interactor, BaseInteractor(context, fileName) {
    override fun fetchData(finished: WeeklyContract.Interactor.OnFinished, latitude: Float, longitude: Float) {
        val client = RetrofitClient().getClient("json")

        val location = client
            .getLocationDataObservable(latitude, longitude, null)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val ocean = client
            .getOceanDataObservable(latitude, longitude)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val combined = Observable.zip(location, ocean) { locationData, oceanData -> WeeklyModel(locationData, oceanData) }
        combined.subscribe(object : Subscriber<WeeklyModel>() {
            override fun onNext(data: WeeklyModel?) {
                finished.onFinished(data?.locationData)
                finished.onFinished(data?.oceanData)
            }

            override fun onCompleted() {
                finished.updateView()
            }

            override fun onError(t: Throwable) {
                finished.onFailure(t.message)
            }
        })
    }
}