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

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location>, GoogleMap.OnInfoWindowClickListener, MapContract.View {
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private lateinit var harborsLayer: GeoJsonLayer
    private lateinit var presenter: MapPresenter
    private val fileName = "com.example.sea"

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

        fab_waves.setOnClickListener{presenter.onFABClick()}
        fab_wind.setOnClickListener{presenter.onFABClick()}
        fab_rain.setOnClickListener{presenter.onFABClick()}
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

    override fun onMarkerClick(p0: Marker?) = false
    override fun onMyLocationButtonClick() = false

    override fun onInfoWindowClick(p0: Marker?) {
        presenter.onInfoWindowClick(p0!!.position)
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

    override fun showCameraAnimation(currentLatLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
    }

    private fun showMarker(p0: LatLng?, locationName: String) {
        marker = map.addMarker(MarkerOptions().position(p0!!).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
    }

    private fun setupMarkerWindow(p0: LatLng?, locationName: String) {
        map.setInfoWindowAdapter(presenter.createInfoWindowAdapter(p0!!, locationName))
    }

    override fun setTitle(text: String) {
        activity!!.toolbar.title = text
    }

    override fun changeTab() {
        val tab = activity!!.findViewById<TabLayout>(R.id.tabs)
        tab.getTabAt(0)!!.select()
    }

    override fun showButtons() {
        fab_waves.show()
        fab_wind.show()
        fab_rain.show()
        fab_harbor.show()
    }

    override fun showTextView() {
        fab_waves_text.visibility = View.VISIBLE
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
        fab_waves_text.visibility = View.GONE
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
        fab_waves.hide()
        fab_wind.hide()
        fab_rain.hide()
        fab_harbor.hide()
    }
}