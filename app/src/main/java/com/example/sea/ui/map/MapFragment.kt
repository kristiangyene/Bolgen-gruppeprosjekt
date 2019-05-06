package com.example.sea.ui.map

import android.location.Location
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.example.sea.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.view_pager.*
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location>, GoogleMap.OnInfoWindowClickListener, MapContract.View {
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private lateinit var harborsLayer: GeoJsonLayer
    private lateinit var presenter: MapPresenter
    private val fileName = "com.example.sea"
    private var markers = mutableListOf<Marker>()
    private val colorPrimary = 0xFFEEEEEE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        presenter = MapPresenter(this, activity!!, this, MapInteractor(activity!!, fileName))

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fab.setOnClickListener {presenter.onFABClick()}

        fab_wind.setOnClickListener{presenter.onWindClick()}
        fab_rain.setOnClickListener{presenter.onRainClick()}
        fab_harbor.setOnClickListener{presenter.onHarborButtonClick()}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)
        map.setOnInfoWindowClickListener(this)

        if(presenter.checkPermission("location")) {
            map.isMyLocationEnabled = true
            map.setOnMyLocationButtonClickListener(this)
            presenter.findLastLocation()
        }

        restrictMap()
        setUpMapStyle()
        setUpHarborMarkers()
    }

    override fun onMarkerClick(p0: Marker?) : Boolean {
        if(marker != null && marker!!.id == p0!!.id) {
            return false
        }

        Toast.makeText(activity!!, "Lat: ${p0!!.position!!.latitude}, Long: ${p0.position!!.longitude}", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationButtonClick() = false

    override fun onInfoWindowClick(p0: Marker?) {
        presenter.onInfoWindowClick(p0)
    }

    // kalles hvis fusedLocationClient finner lastlocation
    override fun onSuccess(location: Location?) {
        presenter.onSuccess(location)
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }

        val locationName = presenter.getAddress(p0!!.latitude, p0.longitude)

        showMarker(p0, locationName)
        setupMarkerWindow(p0, locationName)
    }

    private fun setUpMapStyle() {
        val style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style)
        map.setMapStyle(style)
    }

    private fun restrictMap() {
        // Lager en LatLngBound som inkluderer Norge.
        val norway = LatLngBounds(LatLng(52.177844, -15.245368), LatLng(81.685035, 49.537870))

        // Begrenser kamera rundt Norge
        map.setLatLngBoundsForCameraTarget(norway)
        map.setMinZoomPreference(4.0f)
    }

    private fun setUpHarborMarkers() {
        harborsLayer = GeoJsonLayer(map, R.raw.geo_json_harbors, activity!!)
        presenter.setUpHarborMarkers(harborsLayer)
    }

    private fun showMarker(p0: LatLng?, locationName: String) {
        marker = map.addMarker(MarkerOptions().position(p0!!).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
    }

    private fun setupMarkerWindow(p0: LatLng?, locationName: String) {
        map.setInfoWindowAdapter(presenter.createInfoWindowAdapter(p0!!, locationName))
    }

    override fun getMarker() = marker

    override fun setTitle(text: String) {
        activity!!.toolbar.title = text
    }

    override fun showCameraAnimation(currentLatLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
    }

    override fun changeTab() {
        val tab = activity!!.findViewById<TabLayout>(R.id.tabs)
        tab.getTabAt(0)!!.select()
    }

    override fun hideMarkers() {
        if(markers.isNotEmpty()) {
            for (marker in markers) {
                marker.remove()
            }

            markers.clear()
        }
    }

    override fun showMarkers(data: String, lat: Double, long: Double, type : String) {
//        val markerOption =  MarkerOptions().title(data).position(LatLng(lat, long))
//        if(type == "rain") {
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(presenter.createRainMarker()))
//        }
//        else {
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(presenter.createWindMarker()))
//        }

        val iconFactory = IconGenerator(activity!!)
        iconFactory.setTextAppearance(R.style.markerTextStyle)
        iconFactory.setColor(colorPrimary.toInt())
        val marker = map.addMarker(MarkerOptions().position(LatLng(lat, long)))
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(data)))

//        val marker = map.addMarker(markerOption)
        markers.add(marker)
    }

    override fun showButtons() {
        fab_wind.show()
        fab_rain.show()
        fab_harbor.show()
    }

    override fun showTextView() {
        fab_wind_text.visibility = View.VISIBLE
        fab_rain_text.visibility = View.VISIBLE
        fab_harbor_text.visibility = View.VISIBLE
    }

    override fun showAnimation(showButton: Animation?) {
        fab.startAnimation(showButton)
    }

    override fun hideAnimation(hideButton: Animation?) {
        fab.startAnimation(hideButton)
    }

    override fun hideTextView() {
        fab_wind_text.visibility = View.GONE
        fab_rain_text.visibility = View.GONE
        fab_harbor_text.visibility = View.GONE
    }

    override fun hideHarbors() {
        harborsLayer.removeLayerFromMap()
    }

    override fun showHarbors() {
        harborsLayer.addLayerToMap()
    }

    override fun hideButtons() {
        fab_wind.hide()
        fab_rain.hide()
        fab_harbor.hide()
    }

    override fun onFailure(t: Throwable) {
        Log.d("Error: ", t.toString())
    }
}