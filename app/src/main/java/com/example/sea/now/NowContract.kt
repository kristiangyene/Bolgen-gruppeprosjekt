package com.example.sea.now

import com.example.sea.base.BaseContract
import com.example.sea.map.LocationData
import com.example.sea.service.model.OceanData

interface NowContract {
    interface View {
        fun onFailure(t : Throwable)
        fun setSeekbarProgress(progress : Int)
        fun setDataInRecyclerView(element : NowElement)
        fun updateRecyclerView()
    }

    interface Presenter {
        fun onDestroy()
        fun requestLocationData(latitude : Float, longitude : Float)
        fun requestOceanData(latitude : Double, longitude : Double)
        fun calculateWavesRisk(value : Double?)
        fun calculateWindRisk(value : Double?)
        fun calculateRisk(value : Double?, content : String) : Int
        fun fetchData()
    }

    interface Interactor : BaseContract.Interactor {
        fun getOceanData(finished : OnFinished, latitude : Double, longitude : Double)
        fun getLocationData(finished : OnFinished, latitude : Float, longitude : Float)

        interface OnFinished {
            fun onFinished(data : OceanData.Forecast.OceanForecast.OceanValue?)
            fun onFinished(data : LocationData?)
            fun onFailure(t: Throwable)
        }
    }
}