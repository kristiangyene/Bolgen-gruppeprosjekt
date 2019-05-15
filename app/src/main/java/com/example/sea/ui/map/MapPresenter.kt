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
import com.google.android.gms.maps.model.MarkerOptions
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
    private var countDone = 0
    private var numberOfCalls = 0
    private var location : Location? = null
    private var markerStart = false
    private var lastLatitude: Float? = null
    private var lastLongitude: Float? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var zoomLevel = 8f

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
            view!!.showCameraAnimation(LatLng(64.622456, 18.488198), 4.3f)
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

        if(!rainShowing || (lastLatitude != null && lastLongitude != null  && view!!.getMarker() != null && lastLatitude != view!!.getMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getMarker()!!.position.longitude.toFloat())) {
            if(rainShowing) {
                view!!.hideMarkers()
            }

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

            if(ConnectionUtil.checkNetwork(activity)) {
                if(view!!.getMarker() != null || view!!.getStartMarker() != null) {
                    if(view!!.getMarker() != null) {
                        if(lastLatitude != view!!.getMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getMarker()!!.position.longitude.toFloat()) {
                            lastLatitude = view!!.getMarker()!!.position.latitude.toFloat()
                            lastLongitude = view!!.getMarker()!!.position.longitude.toFloat()

                            windData.clear()
                            rainData.clear()
                            latitudeData.clear()
                            longitudeData.clear()

                            requestLocationData(lastLatitude!!, lastLongitude!!)
                        }
                        else {
                            loadData("rain")
                        }
                    }
                    else {
                        if(lastLatitude != view!!.getStartMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getStartMarker()!!.position.longitude.toFloat()) {
                            lastLatitude = view!!.getStartMarker()!!.position.latitude.toFloat()
                            lastLongitude = view!!.getStartMarker()!!.position.longitude.toFloat()

                            windData.clear()
                            rainData.clear()
                            latitudeData.clear()
                            longitudeData.clear()

                            requestLocationData(lastLatitude!!, lastLongitude!!)
                        }
                        else {
                            loadData("rain")
                        }
                    }
                }
                else if(location != null) {
                    if(lastLatitude != location!!.latitude.toFloat() && lastLongitude != location!!.longitude.toFloat()) {
                        lastLatitude = location!!.latitude.toFloat()
                        lastLongitude = location!!.longitude.toFloat()

                        windData.clear()
                        rainData.clear()
                        latitudeData.clear()
                        longitudeData.clear()

                        requestLocationData(location!!.latitude.toFloat(), location!!.longitude.toFloat())
                    }
                    else {
                        loadData("rain")
                    }
                }
                else {
                    view!!.showMessage("Trykk på kartet for å kunne se værmelding om det valgte stedet")
                }
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

        if(!windShowing || (lastLatitude != null && lastLongitude != null  && view!!.getMarker() != null && lastLatitude != view!!.getMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getMarker()!!.position.longitude.toFloat())) {
            if(windShowing) {
                view!!.hideMarkers()
            }

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

            if(ConnectionUtil.checkNetwork(activity)) {
                if(view!!.getMarker() != null || view!!.getStartMarker() != null) {
                    if(view!!.getMarker() != null) {
                        if(lastLatitude != view!!.getMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getMarker()!!.position.longitude.toFloat()) {
                            lastLatitude = view!!.getMarker()!!.position.latitude.toFloat()
                            lastLongitude = view!!.getMarker()!!.position.longitude.toFloat()

                            windData.clear()
                            rainData.clear()
                            latitudeData.clear()
                            longitudeData.clear()

                            requestLocationData(lastLatitude!!, lastLongitude!!)
                        }
                        else {
                            loadData("wind")
                        }
                    }
                    else {
                        if(lastLatitude != view!!.getStartMarker()!!.position.latitude.toFloat() && lastLongitude != view!!.getStartMarker()!!.position.longitude.toFloat()) {
                            lastLatitude = view!!.getStartMarker()!!.position.latitude.toFloat()
                            lastLongitude = view!!.getStartMarker()!!.position.longitude.toFloat()

                            windData.clear()
                            rainData.clear()
                            latitudeData.clear()
                            longitudeData.clear()

                            requestLocationData(lastLatitude!!, lastLongitude!!)
                        }
                        else {
                            loadData("wind")
                        }
                    }
                }
                else if(location != null) {
                    if(lastLatitude != location!!.latitude.toFloat() && lastLongitude != location!!.longitude.toFloat()) {
                        lastLatitude = location!!.latitude.toFloat()
                        lastLongitude = location!!.longitude.toFloat()

                        windData.clear()
                        rainData.clear()
                        latitudeData.clear()
                        longitudeData.clear()

                        requestLocationData(location!!.latitude.toFloat(), location!!.longitude.toFloat())
                    }
                    else {
                        loadData("wind")
                    }
                }
                else {
                    view!!.showMessage(activity.getString(R.string.click_on_map))
                }
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

    private fun requestLocationData(latitude: Float, longitude: Float) {
        view!!.showProgress()
        this.latitude = latitude.toDouble()
        this.longitude = longitude.toDouble()

        var number = 2
        when (interactor.getNetworkUsage()) {
            0 -> {
                numberOfCalls = 8 + 1
                number = 2
                zoomLevel = 6.5f
            }
            1 -> {
                numberOfCalls = 8*2 + 1
                number = 3
                zoomLevel = 5.5f
            }
            2 -> {
                numberOfCalls = 8*3 + 1
                number = 4
                zoomLevel = 5f
            }
        }

        interactor.getLocationData(this, latitude, longitude)

        for(i in 1 until number) {
            interactor.getLocationData(this, latitude+i, longitude)
            interactor.getLocationData(this, latitude-i, longitude)

            interactor.getLocationData(this, latitude, longitude+i*2)
            interactor.getLocationData(this, latitude, longitude-i*2)

            interactor.getLocationData(this, latitude+i, longitude+i*2)
            interactor.getLocationData(this, latitude+i, longitude-i*2)

            interactor.getLocationData(this, latitude-i, longitude+i*2)
            interactor.getLocationData(this, latitude-i, longitude-i*2)
        }
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
        view!!.showCameraAnimation(LatLng(latitude!!, longitude!!), zoomLevel)
    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            val currentLatLng = LatLng(location.latitude, location.longitude)
            this.location = location

            if(!markerStart) {
                view!!.showCameraAnimation(currentLatLng, 8f)
            }
        }

        val markerOptions = createStartMarker()
        view!!.setMarkerOnStart(markerOptions)
    }

    override fun findLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if(checkPermission("location")) {
            fusedLocationClient.lastLocation.addOnSuccessListener(mapFragment)
        }
    }

    override fun getAddress(latitude: Double?, longitude: Double?) : String {
        interactor.setMapNeverClicked(true)
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
        if(marker != null && (view!!.getMarker() != null && marker.id == view!!.getMarker()!!.id) || (view!!.getStartMarker() != null && marker!!.id == view!!.getStartMarker()!!.id)) {
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
            view!!.showMarkers(activity.getString(R.string.rain) + ": " + nowData[1].location.precipitation.value + " mm", nowData[0].location.latitude.toDouble(), nowData[0].location.longitude.toDouble(), "rain")
        }
        else if(windShowing) {
            view!!.showMarkers(activity.getString(R.string.wind) + ": " + String.format("%.1f", value) + " " + measurement, nowData[0].location.latitude.toDouble(), nowData[0].location.longitude.toDouble(), "wind")
        }

        rainData.add(activity.getString(R.string.rain) + ": " + nowData[1].location.precipitation.value + " mm")
        windData.add(String.format(activity.getString(R.string.wind) + ": " + "%.1f", value) + " " + measurement)
        latitudeData.add(nowData[0].location.latitude.toDouble())
        longitudeData.add(nowData[0].location.longitude.toDouble())

        countDone++

        if(countDone == numberOfCalls) {
            view!!.hideProgress()
            countDone = 0
            view!!.showCameraAnimation(LatLng(latitude!!, longitude!!), zoomLevel)
        }
    }

    override fun onFailure(t: String?) {
        if(t != null) {
            view!!.onFailure(t)
        }
    }

    override fun createStartMarker() : MarkerOptions? {
        if((location != null && interactor.getLatitude() != location!!.latitude.toFloat() && interactor.getLongitude() != location!!.longitude.toFloat()) || location == null && interactor.getMapNeverClicked()) {
            val locationName = getAddress(interactor.getLatitude().toDouble(), interactor.getLongitude().toDouble())
            markerStart = true
            return MarkerOptions().position(LatLng(interactor.getLatitude().toDouble(), interactor.getLongitude().toDouble())).title(locationName)
        }

        return null
    }
}