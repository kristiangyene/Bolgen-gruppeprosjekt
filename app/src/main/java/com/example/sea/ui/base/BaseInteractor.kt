package com.example.sea.ui.base

import android.content.Context
import com.example.sea.data.local.AppPreferencesHelper

open class BaseInteractor(context: Context, fileName : String) : BaseContract.Interactor {
    private var helper = AppPreferencesHelper(context, fileName)

    override fun getFirstStart(): Boolean {
        return helper.getFirstStart()
    }

    override fun setFirstStart(value: Boolean) {
        helper.setFirstStart(value)
    }

    override fun getLatitude(): Float {
        return helper.getLatitude()
    }

    override fun setLatitude(value: Float) {
        helper.setLatitude(value)
    }

    override fun getLongitude(): Float {
        return helper.getLongitude()
    }

    override fun setLongitude(value: Float) {
        helper.setLongitude(value)
    }

    override fun getCeMark(): String? {
        return helper.getCeMark()
    }

    override fun setCeMark(value: String) {
        helper.setCeMark(value)
    }

    override fun getTemperaturUnit(): String? {
        return helper.getTemperatureUnit()
    }

    override fun setTemperaturUnit(value: String) {
        helper.setTemperatureUnit(value)
    }

    override fun getWindUnit(): String? {
        return helper.getWindUnit()
    }

    override fun setWindUnit(value: String) {
        helper.setWindUnit(value)
    }

    override fun getVisibilityUnit(): String? {
        return helper.getVisibilityUnit()
    }

    override fun setVisibilityUnit(value: String) {
        helper.setVisibilityUnit(value)
    }

    override fun getPressureUnit(): String? {
        return helper.getPressureUnit()
    }

    override fun setPressureUnit(value: String) {
        helper.setPressureUnit(value)
    }

    override fun getWeatherPreference(key: String): Boolean {
        return helper.getWeatherPreference(key)
    }

    override fun setWeatherPreference(key: String, value: Boolean) {
        helper.setWeatherPreference(key, value)
    }

    override fun getMapNeverClicked(): Boolean {
        return helper.getMapNeverClicked()
    }

    override fun setMapNeverClicked(value: Boolean) {
        helper.setMapNeverClicked(value)
    }

    override fun getNetworkUsage(): Int? {
        return helper.getNetworkUsage()
    }

    override fun setNetworkUsage(value: Int) {
        helper.setNetworkUsage(value)
    }

    override fun getUserLatitude(): Float {
        return helper.getUserLatitude()
    }

    override fun setUserLatitude(value: Float) {
        helper.setUserLatitude(value)
    }

    override fun getUserLongitude(): Float {
        return helper.getUserLongitude()
    }

    override fun setUserLongitude(value: Float) {
        helper.setUserLongitude(value)
    }
}