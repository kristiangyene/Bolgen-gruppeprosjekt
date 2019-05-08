package com.example.sea.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.animation.AnimationUtils
import com.example.sea.R
import com.example.sea.data.remote.model.LocationData
import com.example.sea.ui.base.BasePresenter
import com.example.sea.utils.ConnectionUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import java.io.IOException
import java.text.DecimalFormat
import java.util.Locale

class MapPresenter(view: MapContract.View, private var activity: FragmentActivity, private var mapFragment: MapFragment, private var interactor: MapInteractor) : MapContract.Presenter, BasePresenter(activity), MapContract.Interactor.OnFinished {
    private var view : MapContract.View? = view
    private var harborsShowing = false
    private var rainShowing = false
    private var windShowing = false
    private var fabOpen = false
    private val windData = mutableListOf<String>()
    private val rainData = mutableListOf<String>()
    private val latitudeData = mutableListOf<Double>()
    private val longitudeData = mutableListOf<Double>()
    private var requested: Boolean = false

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

            if(rainShowing) {
                view!!.hideMarkers()
                rainShowing = false
            }

            if(windShowing) {
                view!!.hideMarkers()
                windShowing = false
            }
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

    override fun onRainClick() {
        val hideButton = AnimationUtils.loadAnimation(activity, R.anim.hide_button)

        if(!rainShowing ) {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideAnimation(hideButton)
            rainShowing  = true
            fabOpen = false

            if(harborsShowing) {
                view!!.hideHarbors()
                harborsShowing = false
            }

            if(windShowing) {
                view!!.hideMarkers()
                windShowing = false
            }

            if(!requested && ConnectionUtil.checkNetwork(activity)) {
                requestLocationData()
                requested = true
            }
            else {
                loadData("rain")
            }

        }
        else {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideMarkers()
            view!!.hideAnimation(hideButton)
            rainShowing  = false
            fabOpen = false
        }
    }

    override fun onWindClick() {
        val hideButton = AnimationUtils.loadAnimation(activity, R.anim.hide_button)

        if(!windShowing ) {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideAnimation(hideButton)
            windShowing  = true
            fabOpen = false

            if(rainShowing) {
                view!!.hideMarkers()
                rainShowing = false
            }

            if(harborsShowing) {
                view!!.hideHarbors()
                harborsShowing = false
            }

            if(!requested && ConnectionUtil.checkNetwork(activity)) {
                requestLocationData()
                requested = true
            }
            else {
                loadData("wind")
            }
        }
        else {
            view!!.hideButtons()
            view!!.hideTextView()
            view!!.hideMarkers()
            view!!.hideAnimation(hideButton)
            windShowing  = false
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

    override fun createRainMarker() : Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(activity.resources,
            activity.resources.getIdentifier("rain_black", "drawable", activity.packageName))
        return Bitmap.createScaledBitmap(imageBitmap, 75, 75, false)
    }

    override fun createWindMarker(): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(activity.resources,
            activity.resources.getIdentifier("wind_black", "drawable", activity.packageName))
        return Bitmap.createScaledBitmap(imageBitmap, 75, 75, false)
    }

    private fun requestLocationData() {
        interactor.getLocationData(this, interactor.getLatitude(), interactor.getLongitude())
        interactor.getLocationData(this, interactor.getLatitude()+1, interactor.getLongitude()) // 111.19, Nord
        interactor.getLocationData(this, interactor.getLatitude()-1, interactor.getLongitude()) // 111.19, Sør
        interactor.getLocationData(this, interactor.getLatitude(), interactor.getLongitude()+2) // 111.19, Øst
        interactor.getLocationData(this, interactor.getLatitude(), interactor.getLongitude()-2) // 111.19, Vest
    }

    private fun loadData(type : String) {
        if(type == "rain") {
            for (i in 0 until rainData.size) {
                view!!.showMarkers(rainData[i], latitudeData[i], longitudeData[i], "rain")
            }
        }
        else {
            for (i in 0 until windData.size) {
                view!!.showMarkers(windData[i], latitudeData[i], longitudeData[i], "wind")
            }
        }
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            view!!.showCameraAnimation(currentLatLng)
        }
    }

    override fun findLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if(checkPermission("location")) {
            fusedLocationClient.lastLocation.addOnSuccessListener(mapFragment)
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

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker != null && view!!.getMarker() != null && marker.id == view!!.getMarker()!!.id) {
            val position = marker.position
            val format = DecimalFormat("#.###")
            interactor.setLatitude(position!!.latitude.toFloat())
            interactor.setLongitude(position.longitude.toFloat())
            view!!.setTitle("${format.format(position.latitude)}, ${format.format(position.longitude)}")
            view!!.changeTab()
        }
    }

    override fun createInfoWindowAdapter(position: LatLng?, locationName: String) : CustomInfoWindowAdapter {
        return CustomInfoWindowAdapter(activity, position!!, interactor.getFoundAddress(), locationName)
    }

    override fun onFinished(data: LocationData?) {
        val nowData = data?.product?.time!!
        val measurement: String
        var value = nowData[0].location.windSpeed.mps.toDouble()
        val windText = interactor.getWindUnit()


        if (windText == null || windText == "Km/h") {
            measurement =  "Km/h"
            value *= 3.6
        }
        else if(windText == "Mph"){
            measurement = windText
            value *= 2.236936
        }
        else {
            measurement = windText
        }

        if(rainShowing) {
            view!!.showMarkers(activity.getString(R.string.rain) + ": " + nowData[1].location.precipitation.value, nowData[0].location.latitude.toDouble(), nowData[0].location.longitude.toDouble(), "rain")
        }
        else if(windShowing) {
            Log.d("Ahmed", "Wind: " + String.format("%.1f", value) + measurement)
            view!!.showMarkers(activity.getString(R.string.wind) + ": " + String.format("%.1f", value) + measurement, nowData[0].location.latitude.toDouble(), nowData[0].location.longitude.toDouble(), "wind")
        }

        rainData.add(activity.getString(R.string.rain) + ": " + nowData[1].location.precipitation.value)
        windData.add(String.format(activity.getString(R.string.wind) + ": " + "%.1f", value) + measurement)
        latitudeData.add(nowData[0].location.latitude.toDouble())
        longitudeData.add(nowData[0].location.longitude.toDouble())
    }

    override fun onFailure(t: Throwable) {
        view!!.onFailure(t)
    }
}