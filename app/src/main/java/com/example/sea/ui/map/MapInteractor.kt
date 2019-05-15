package com.example.sea.ui.map

import android.content.Context
import com.example.sea.data.remote.RetrofitClient
import com.example.sea.data.remote.model.LocationData
import com.example.sea.ui.base.BaseInteractor
import com.example.sea.ui.main.MainContract
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MapInteractor(context: Context, fileName : String) : MainContract.Interactor, MapContract.Interactor, BaseInteractor(context, fileName) {
    private var sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun getFoundAddress() : Boolean {
        return sharedPreferences.getBoolean("address", false)
    }

    override fun setFoundAddress(value: Boolean) {
        sharedPreferences.edit().putBoolean("address", value).apply()
    }

    override fun getLocationData(finished : MapContract.Interactor.OnFinished, latitude : Float, longitude : Float) {
        val retrofit = RetrofitClient().getClient("json")
        retrofit.getLocationData(latitude, longitude, null)
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
}