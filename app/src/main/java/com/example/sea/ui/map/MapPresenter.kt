package com.example.sea.ui.map

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.animation.AnimationUtils
import com.example.sea.R
import com.example.sea.ui.base.BasePresenter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class MapPresenter(view: MapContract.View, private var activity: FragmentActivity, private var mapFragment: MapFragment, private var interactor: MapInteractor) : MapContract.Presenter, BasePresenter(activity) {
    private var view : MapContract.View? = view
    private var harborsShowing = false
    private var fabOpen = false

    override fun findLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if(checkPermission("location")) {
            fusedLocationClient.lastLocation.addOnSuccessListener(mapFragment)
        }
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            view!!.showCameraAnimation(currentLatLng)
        }
    }

    override fun onFABClick() {
        val showButton = AnimationUtils.loadAnimation(activity, R.anim.show_button)
        val hideButton = AnimationUtils.loadAnimation(activity, R.anim.hide_button)

        fabOpen = if(fabOpen) {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideAnimation(hideButton)
            false
        }
        else {
            view!!.showButtons()
            view!!.showTextView()
            view!!.showAnimation(showButton)
            true
        }
    }

    override fun onHarborButtonClick() {
        val hideButton = AnimationUtils.loadAnimation(activity, R.anim.hide_button)

        if(!harborsShowing) {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.showHarbors()
            view!!.hideAnimation(hideButton)
            harborsShowing = true
            fabOpen = false
        }
        else {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideHarbors()
            view!!.hideAnimation(hideButton)
            harborsShowing = false
            fabOpen = false
        }
    }

    override fun setUpHarborMarkers(harborsLayer: GeoJsonLayer) {
        for(feature in harborsLayer.features) {
            val pointStyle = GeoJsonPointStyle()
            pointStyle.title = feature.properties.toString().substring(6, feature.properties.toString().length-1)
            pointStyle.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            feature.pointStyle = pointStyle
        }
    }

    override fun getAddress(latitude: Double?, longitude: Double?) : String {
        val addresses: List<Address>
        val geoCoder = Geocoder(activity, Locale.getDefault())

        try {
            addresses = geoCoder.getFromLocation(latitude!!, longitude!!, 1)
        }
        catch (ioException: IOException) {
            // Nettverk problemer eller andre I/O problemer
            Log.e("E", "Error", ioException)
            return "Error"
        }

        if(addresses.isNotEmpty()) {
            interactor.setFoundAddress(true)
            val address = addresses[0]
            val addressFragments = with(address) {(0..maxAddressLineIndex).map { getAddressLine(it)}}
            return addressFragments.joinToString(separator = "\n")
        }

        interactor.setFoundAddress(false)
        return "$latitude, $longitude"
    }

    override fun onInfoWindowClick(position: LatLng?) {
        val format = DecimalFormat("#.###")
        view!!.setTitle("${format.format(position!!.latitude)}, ${format.format(position.longitude)}")
        view!!.changeTab()
    }

    override fun createInfoWindowAdapter(position: LatLng?, locationName: String) : CustomInfoWindowAdapter {
        return CustomInfoWindowAdapter(activity, position!!, interactor.getFoundAddress(), locationName)
    }
}