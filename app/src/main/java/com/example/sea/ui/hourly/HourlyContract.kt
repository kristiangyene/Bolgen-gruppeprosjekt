package com.example.sea.ui.hourly

import com.example.sea.ui.base.BaseContract
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData

interface HourlyContract {
    interface View {
        fun setDataInRecyclerView(element: HourlyElement)
        fun updateRecyclerView()
        fun getList() : ArrayList<HourlyElement>
        fun onFailure(t: String?)
    }

    interface Presenter {
        fun onDestroy()
        fun fetchData(onStart: Boolean)
        fun findNearestHarbor(latitude: Float, longitude: Float)
        fun requestTidalData(latitude: Float, longitude: Float) : Boolean
    }

    interface Interactor : BaseContract.Interactor {
        fun fetchData(finished: OnFinished, latitude: Float, longitude: Float, harbor: String?)

        interface OnFinished {
            fun onFinished(data: OceanData?)
            fun onFinished(data: LocationData?)
            fun onFinished(data: String?)
            fun updateView()
            fun onFailure(t: String?)
        }
    }
}