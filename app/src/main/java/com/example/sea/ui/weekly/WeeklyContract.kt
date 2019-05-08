package com.example.sea.ui.weekly

import com.example.sea.data.remote.model.LocationData
import com.example.sea.data.remote.model.OceanData
import com.example.sea.ui.base.BaseContract

interface WeeklyContract {

    interface View {
        fun setDataInRecyclerView(element : WeeklyElement)
        fun updateRecyclerView()
        fun getList() : ArrayList<WeeklyElement>
    }

    interface Presenter {
        fun onDestroy()
        fun fetchData()
    }

    interface Interactor : BaseContract.Interactor {
        fun getData(finished : OnFinished, latitude : Float, longitude : Float)

        interface OnFinished {
            fun onFinished(data : OceanData?)
            fun onFinished(data : LocationData?)
        }
    }
}