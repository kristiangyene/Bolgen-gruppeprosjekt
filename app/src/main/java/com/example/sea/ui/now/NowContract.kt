package com.example.sea.ui.now

import com.example.sea.ui.base.BaseContract
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData

interface NowContract {
    interface View {
        fun onFailure(t : String?)
        fun setSeekbarProgress(progress : Int)
        fun setDataInRecyclerView(element : NowElement)
        fun setDataInRecyclerViewStart(element : NowElement)
        fun setDataInRecyclerViewPosition(index : Int, element : NowElement)
        fun updateRecyclerView()
        fun showProgress()
        fun hideProgress()
        fun getList() : ArrayList<NowElement>
        fun updateTextScale(text: String)
        fun getTextScaleLines() : Int
    }

    interface Presenter {
        fun fetchData(onFirstStart: Boolean)
        fun requestLocationData(latitude : Float, longitude : Float)
        fun requestOceanData(latitude : Float, longitude : Float)
        fun requestTidalData(latitude : Float, longitude : Float)
        fun calculateWavesRisk(value : Double?)
        fun calculateWindRisk(value : Double?)
        fun calculateRisk(value : Double?, content : String) : Int
        fun findNearestHarbor(latitude: Float, longitude: Float)
        fun onDestroy()
    }

    interface Interactor : BaseContract.Interactor {
        fun getOceanData(finished : OnFinished, latitude : Float, longitude : Float)
        fun getLocationData(finished : OnFinished, latitude : Float, longitude : Float)
        fun getTidalData(finished : OnFinished, latitude : Float, longitude : Float, harbor : String)

        interface OnFinished {
            fun onFinished(data : OceanData.Forecast.OceanForecast.OceanValue?)
            fun onFinished(data: LocationData?)
            fun onFinished(data : String?, harbor: String?)
            fun onFailure(t: String?)
        }
    }
}