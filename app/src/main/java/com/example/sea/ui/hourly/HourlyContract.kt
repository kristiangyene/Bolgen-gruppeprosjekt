package com.example.sea.ui.hourly

import com.example.sea.ui.base.BaseContract
import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData

interface HourlyContract {
    interface View {
        fun setDataInRecyclerView(element : HourlyElement)
        fun updateRecyclerView()
        fun getList() : ArrayList<HourlyElement>
    }

    interface Presenter {
        fun onDestroy()
        fun fetchData()
    }

    interface Interactor : BaseContract.Interactor {
        fun setData(finished : OnFinished, latitude : Float, longitude : Float, harbor: String?)

        interface OnFinished {
            fun onFinished(data : OceanData?)
            fun onFinished(data : LocationData?)
            fun onFinished(data : String?)
        }
    }
}