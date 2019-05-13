package com.example.sea.ui.now

import android.content.Context
import android.util.Log
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.OceanData
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NowInteractor(context: Context, fileName: String) : NowContract.Interactor, BaseInteractor(context, fileName) {
    // Henter ut data fra OceanForecast api
    override fun getOceanData(finished : NowContract.Interactor.OnFinished, latitude : Float, longitude : Float) {
        val retrofit = RetrofitClient().getClient("json")
        retrofit.getOceanDataObservable(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<OceanData>() {
                override fun onCompleted() {}

                override fun onError(t: Throwable) {
                    finished.onFailure(t.message)

                    if(t is HttpException) {
                        finished.onFailure(t.response().errorBody()?.string())
                    }
                }

                override fun onNext(response: OceanData) {
                    finished.onFinished(response?.forecast?.get(0)?.oceanForecast?.significantTotalWaveHeight)
                }
            })
    }

    // Henter data fra LocationForecast api
    override fun getLocationData(finished : NowContract.Interactor.OnFinished, latitude : Float, longitude : Float) {
        val retrofit = RetrofitClient().getClient("json")
        retrofit.getLocationDataObservable(latitude, longitude, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<LocationData>() {
                override fun onCompleted() {}

                override fun onError(t: Throwable) {
                    finished.onFailure(t.message)

                    if(t is HttpException) {
                        finished.onFailure(t.response().errorBody()?.string())
                    }
                }

                override fun onNext(response: LocationData) {
                    finished.onFinished(response)
                }
            })
    }

    // Henter data om tidevann dersom det er en havn i nærheten
    override fun getTidalData(finished : NowContract.Interactor.OnFinished, latitude: Float, longitude: Float, harbor : String) {
        Log.d("Kart", "getTidalData $harbor")
        val retrofit = RetrofitClient().getClient("string")
        retrofit.getTidalWaterObservable(harbor)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<String>() {
                override fun onCompleted() {}

                override fun onError(t: Throwable) {
                    finished.onFailure(t.message)

                    if(t is HttpException) {
                        finished.onFailure(t.response().errorBody()?.string())
                    }
                }

                override fun onNext(response: String) {
                    Log.d("Kart", "onNext $harbor")
                    finished.onFinished(response, harbor)
                }
            })
    }
}
