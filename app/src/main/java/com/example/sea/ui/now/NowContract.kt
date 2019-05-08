package com.example.sea.ui.now

import com.example.sea.ui.base.BaseContract
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData

interface NowContract {
    interface View {
        fun onFailure(t : Throwable)
        fun setSeekbarProgress(progress : Int)
        fun setDataInRecyclerView(element : NowElement)
        fun setDataInRecyclerViewStart(element : NowElement)
        fun updateRecyclerView()
    }

    interface Presenter {
        fun onDestroy()
        fun requestLocationData(latitude : Float, longitude : Float)
        fun requestOceanData(latitude : Double, longitude : Double)
        fun requestTidalData(latitude : Float, longitude : Float)
        fun calculateWavesRisk(value : Double?)
        fun calculateWindRisk(value : Double?)
        fun calculateRisk(value : Double?, content : String) : Int
        fun fetchData()
        fun findNearestHarbor(latitude: Float, longitude: Float)
    }

    interface Interactor : BaseContract.Interactor {
        fun getOceanData(finished : OnFinished, latitude : Double, longitude : Double)
        fun getLocationData(finished : OnFinished, latitude : Float, longitude : Float)
        fun getTidalData(finished : OnFinished, latitude : Float, longitude : Float, harbor : String)

        interface OnFinished {
            fun onFinished(data : OceanData.Forecast.OceanForecast.OceanValue?)
            fun onFinished(data : LocationData?)
            fun onFinished(data : String?)
            fun onFailure(t: Throwable)
        }
    }
}