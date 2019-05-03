package com.example.sea.pref

interface PreferencesHelper {
    fun getFirstStart() : Boolean
    fun setFirstStart(value : Boolean)

    fun getLatitude() : Float
    fun setLatitude(value : Float)

    fun getLongitude() : Float
    fun setLongitude(value : Float)

    fun getCeMark() : String?
    fun setCeMark(value : String)

    fun getTemperatureUnit() : String?
    fun setTemperatureUnit(value : String)

    fun getWindUnit() : String?
    fun setWindUnit(value : String)

    fun getVisibilityUnit() : String?
    fun setVisibilityUnit(value : String)

    fun getPressureUnit() : String?
    fun setPressureUnit(value : String)

    fun getWeatherPreference(key : String) : Boolean
    fun setWeatherPreference(key: String, value : Boolean)
}