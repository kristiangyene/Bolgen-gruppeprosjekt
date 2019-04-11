package com.example.sea

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.view_pager.*
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location>, GoogleMap.OnInfoWindowClickListener {

    private lateinit var map : GoogleMap
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var marker : Marker? = null
    private lateinit var lastLocation: Location
    private var lat : Double? = null
    private var long : Double? = null
    private var foundAddress = false
    private var fabOpen = false
    private var harborsShowing = false
    private lateinit var harborsLayer : GeoJsonLayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Setter språket til norsk
        val language = "no"
        val config = Configuration()
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.setLocale(locale)
        activity!!.createConfigurationContext(config)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val showButton = AnimationUtils.loadAnimation(activity!!, R.anim.show_button)
        val hideButton = AnimationUtils.loadAnimation(activity!!, R.anim.hide_button)

        fab.setOnClickListener {
            if(fabOpen) {
                hide(hideButton)
            }
            else {
                show(showButton)
            }
        }

        fab_waves.setOnClickListener{hide(hideButton)}
        fab_wind.setOnClickListener{hide(hideButton)}
        fab_rain.setOnClickListener{hide(hideButton)}
        fab_harbor.setOnClickListener{
            hide(hideButton)
            showHarbors()
        }
    }

    private fun showHarbors() {
        if(!harborsShowing) {
            harborsLayer.addLayerToMap()
            harborsShowing = true
        }
        else {
            harborsLayer.removeLayerFromMap()
            harborsShowing = false
        }
    }

    private fun show(showButton : Animation?) {
        fab.startAnimation(showButton)
        fab_waves.show()
        fab_wind.show()
        fab_rain.show()
        fab_harbor.show()
        fab_waves_text.visibility = View.VISIBLE
        fab_wind_text.visibility = View.VISIBLE
        fab_rain_text.visibility = View.VISIBLE
        fab_harbor_text.visibility = View.VISIBLE
        fabOpen = true
    }

    private fun hide(hideButton : Animation?) {
        fab_waves.hide()
        fab_wind.hide()
        fab_rain.hide()
        fab_harbor.hide()
        fab_waves_text.visibility = View.GONE
        fab_wind_text.visibility = View.GONE
        fab_rain_text.visibility = View.GONE
        fab_harbor_text.visibility = View.GONE
        fab.startAnimation(hideButton)
        fabOpen = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)

        if(checkPermission()) {
            map.isMyLocationEnabled = true
            map.setOnMyLocationButtonClickListener(this)

            fusedLocationClient.lastLocation.addOnSuccessListener(this)
        }

        // Lager en LatLngBound som inkluderer Norge.
        val norway = LatLngBounds(
            LatLng(52.177844, -15.245368),
            LatLng(81.685035, 49.537870)
        )

        // Begrenser kamera rundt Norge
        map.setLatLngBoundsForCameraTarget(norway)
        map.setMinZoomPreference(4.0f)

        // Setter map style
        val style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style)
        map.setMapStyle(style)
        map.setOnInfoWindowClickListener(this@MapFragment)

        // setter opp geografiske egenskaper i GeoJSON format
        harborsLayer = GeoJsonLayer(map, R.raw.geo_json_harbors, activity!!)
        for(feature in harborsLayer.features) {
            val pointStyle = GeoJsonPointStyle()
            pointStyle.title = feature.properties.toString().substring(6, feature.properties.toString().length-1)
            pointStyle.icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            feature.pointStyle = pointStyle
        }

    }

    override fun onSuccess(location: Location?) {
        if (location != null) {
            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
        }
    }

    private fun getAddress(lat : Double, long : Double) : String {
        val addresses: List<Address>
        val geocoder = Geocoder(activity!!, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(lat, long, 1)
        }
        catch (ioException: IOException) {
            // Nettverk problemer eller andre I/O problemer
            Log.e("E", "Error", ioException)
            return "Fant ikke addressen"
        }
        catch (illegalArgumentException: IllegalArgumentException) {
            // Fanger ugyldige latitude eller  longitude verdier, oppstår ikke. Men kan oppstå hvis en bruker kan oppgi latitude og longitude verdier
            Log.e("E", "Error", illegalArgumentException)
            return "Fant ikke addressen"
        }

        if(addresses.isNotEmpty()) {
            foundAddress = true
            val address = addresses[0]
            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            val addressFragments = with(address) {(0..maxAddressLineIndex).map { getAddressLine(it)}}
            return addressFragments.joinToString(separator = "\n")
        }

        foundAddress = false
        return "$lat , $long"
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }

        lat = p0!!.latitude
        long = p0.longitude

        val locationName = getAddress(p0.latitude, p0.longitude)
        marker = map.addMarker(MarkerOptions().position(p0).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)

        val format = DecimalFormat("#.###")
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(activity!!, format.format(lat!!), format.format(long!!), foundAddress, locationName))
        map.setOnInfoWindowClickListener(this)
    }

    override fun onInfoWindowClick(p0: Marker?) {
        val format = DecimalFormat("#.###")

        activity!!.toolbar.title = "${format.format(lat)} , ${format.format(long)}"
        val tab = activity!!.findViewById<TabLayout>(R.id.tabs)
        tab.getTabAt(0)!!.select()
    }

    override fun onMarkerClick(p0: Marker?) = false
    override fun onMyLocationButtonClick() = false

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}