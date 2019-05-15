package com.example.sea.ui.base

interface BaseContract {
    interface Presenter {
        fun checkPermission(permissionOption: String): Boolean
        fun requestPermission(permissionOption: String)
    }

    interface Interactor {
        fun getFirstStart() : Boolean
        fun setFirstStart(value : Boolean)

        fun getLatitude() : Float
        fun setLatitude(value : Float)

        fun getLongitude() : Float
        fun setLongitude(value : Float)

        fun getCeMark() : String?
        fun setCeMark(value : String)

        fun getTemperaturUnit() : String?
        fun setTemperaturUnit(value : String)

        fun getWindUnit() : String?
        fun setWindUnit(value : String)

        fun getVisibilityUnit() : String?
        fun setVisibilityUnit(value : String)

        fun getPressureUnit() : String?
        fun setPressureUnit(value : String)

        fun getWeatherPreference(key : String) : Boolean
        fun setWeatherPreference(key : String, value: Boolean)

        fun getMapNeverClicked() : Boolean
        fun setMapNeverClicked(value: Boolean)

        fun getNetworkUsage() : Int?
        fun setNetworkUsage(value: Int)

        fun getUserLatitude() : Float
        fun setUserLatitude(value : Float)

        fun getUserLongitude() : Float
        fun setUserLongitude(value : Float)
    }
}