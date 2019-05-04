package com.example.sea.ui.map

import android.location.Location
import android.view.animation.Animation
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer

interface MapContract {
    interface View {
        fun showButtons()
        fun showTextView()
        fun showHarbors()
        fun showAnimation(showButton : Animation?)
        fun hideButtons()
        fun hideTextView()
        fun hideHarbors()
        fun hideAnimation(hideButton : Animation?)
        fun showCameraAnimation(currentLatLng: LatLng)
        fun setTitle(text: String)
        fun changeTab()
    }

    interface Presenter {
        fun onHarborButtonClick()
        fun onFABClick()
        fun setUpHarborMarkers(harborsLayer : GeoJsonLayer)
        fun onSuccess(location: Location?)
        fun findLastLocation()
        fun getAddress(latitude : Double?, longitude : Double?) : String
        fun onInfoWindowClick(position: LatLng?)
        fun createInfoWindowAdapter(position: LatLng?, locationName : String) : CustomInfoWindowAdapter
    }

    interface Interactor{
        fun getFoundAddress() : Boolean
        fun setFoundAddress(value : Boolean)
    }
}