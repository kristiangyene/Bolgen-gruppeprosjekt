package com.example.sea.data.local

import android.content.Context

class AppPreferencesHelper(context: Context, fileName : String) : PreferencesHelper {
    private var sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    override fun getFirstStart(): Boolean {
        return sharedPreferences.getBoolean("firstTime", true)
    }

    override fun setFirstStart(value: Boolean) {
        sharedPreferences.edit().putBoolean("firstTime", value).apply()
    }

    override fun getLatitude(): Float {
        return sharedPreferences.getFloat("lat", 60F)
    }

    override fun setLatitude(value: Float) {
        sharedPreferences.edit().putFloat("lat", value).apply()
    }

    override fun getLongitude(): Float {
        return sharedPreferences.getFloat("long", 11F)
    }

    override fun setLongitude(value: Float) {
        sharedPreferences.edit().putFloat("long", value).apply()
    }

    override fun getCeMark(): String? {
        return sharedPreferences.getString("CE", null)
    }

    override fun setCeMark(value: String) {
        sharedPreferences.edit().putString("CE", value).apply()
    }

    override fun getTemperatureUnit(): String? {
        return sharedPreferences.getString("temp", null)
    }

    override fun setTemperatureUnit(value: String) {
        sharedPreferences.edit().putString("temp", value).apply()
    }

    override fun getWindUnit(): String? {
        return sharedPreferences.getString("wind", null)
    }

    override fun setWindUnit(value: String) {
        sharedPreferences.edit().putString("wind", value).apply()
    }

    override fun getVisibilityUnit(): String? {
        return sharedPreferences.getString("visibility", null)
    }

    override fun setVisibilityUnit(value: String) {
        sharedPreferences.edit().putString("visibility", value).apply()
    }

    override fun getPressureUnit(): String? {
        return sharedPreferences.getString("pressure", null)
    }

    override fun setPressureUnit(value: String) {
        sharedPreferences.edit().putString("pressure", value).apply()
    }

    override fun getWeatherPreference(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun setWeatherPreference(key: String, value : Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
}