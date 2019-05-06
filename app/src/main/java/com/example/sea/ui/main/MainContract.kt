package com.example.sea.ui.main

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.view.MenuItem
import com.example.sea.ui.base.BaseContract
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest

interface MainContract {
    interface View {
        fun setupDrawer()
        fun setupNavigationMenu()
        fun updatePreviewTextView(text : String, itemId : Int)
        fun launchSMSApp(intent : Intent)
        fun loadSettingsScreen()
        fun updateTitle(text : String)
        fun showLocationSettingsMessage(exception: ResolvableApiException, checkValue : Int)
        fun showMessage(text : String, length : Int)
        fun getActivity() : Activity
    }

    interface Presenter {
        fun firstStart()
        fun checkFirstStart() : Boolean
        fun sendSMS()
        fun onDrawerCeClick(menuItem: MenuItem)
        fun onDrawerTemperatureClick(menuItem: MenuItem)
        fun onDrawerWindClick(menuItem: MenuItem)
        fun onDrawerPressureClick(menuItem: MenuItem)
        fun onDrawerPreferencesClick(menuItem: MenuItem)
        fun onDrawerSettingsClick()
        fun setupPreviewText()

        fun checkPermission(permissionOption: String): Boolean
        fun requestPermission(permissionOption: String)
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
        fun createLocationRequest()
        fun getLocation(locationRequest: LocationRequest)
        fun stopUpdate()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun onResume()
        fun onPause()
        fun onDestroy()
    }

    interface Interactor : BaseContract.Interactor
}