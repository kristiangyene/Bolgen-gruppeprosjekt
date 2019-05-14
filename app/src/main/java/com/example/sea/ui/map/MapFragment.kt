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
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location>, GoogleMap.OnInfoWindowClickListener, MapContract.View {
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var startMarker: Marker? = null
    private lateinit var harborsLayer: GeoJsonLayer
    private lateinit var presenter: MapPresenter
    private val fileName = "com.example.sea"
    private var markers = mutableListOf<Marker>()
    private val colorPrimary = 0xFFEEEEEE
    private lateinit var progress : ProgressBar
    private var markerClicked = false
    private var startMarkerClicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        presenter = MapPresenter(this, activity!!, this, MapInteractor(activity!!, fileName))
        progress = rootView.findViewById(R.id.indeterminateBar)

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
        else {
            setMarkerOnStart(presenter.createStartMarker())
        }

        restrictMap()
        setUpMapStyle()
        setUpHarborMarkers()
    }

    override fun onMarkerClick(p0: Marker?) : Boolean {
        if((marker != null && marker!!.id == p0!!.id)) {
            markerClicked = !markerClicked

            if(marker != null && !markerClicked) {
                marker!!.hideInfoWindow()
                return true
            }

            return false
        }
        else if(startMarker != null && startMarker!!.id == p0!!.id) {
            startMarkerClicked = !startMarkerClicked
            if(startMarker != null && !startMarkerClicked) {
                startMarker!!.hideInfoWindow()
                return true
            }

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

        setupMarker(p0, locationName)
    }

    private fun setUpMapStyle() {
        val style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style)
        map.setMapStyle(style)
    }

    override fun setMarkerOnStart(markerOptions: MarkerOptions?) {
        if(markerOptions != null) {
            startMarker = map.addMarker(markerOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.position, 8f))
            map.setInfoWindowAdapter(presenter.createInfoWindowAdapter(markerOptions.position, markerOptions.title))
        }
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

    private fun setupMarker(p0: LatLng?, locationName: String) {
        if(startMarker != null) {
            startMarker!!.remove()
        }

        marker = map.addMarker(MarkerOptions().position(p0!!).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
        map.setInfoWindowAdapter(presenter.createInfoWindowAdapter(p0, locationName))
    }

    override fun getMarker() = marker
    override fun getStartMarker() = startMarker

    override fun setTitle(text: String) {
        activity!!.toolbar_title.text = text
    }

    override fun showCameraAnimation(currentLatLng: LatLng, zoomLevel : Float) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))
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
        val iconFactory = IconGenerator(activity!!)
        iconFactory.setTextAppearance(R.style.markerTextStyle)
        iconFactory.setColor(colorPrimary.toInt())
        val marker = map.addMarker(MarkerOptions().position(LatLng(lat, long)))
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(data)))

        markers.add(marker)
    }

    override fun removeMarker(marker: Marker?) {
        marker?.remove()
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

    override fun onFailure(t: String?) {
        if(t != null) {
            Log.e("Error: ", t)
        }
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showMessage(text: String) {
        Toast.makeText(activity!!, text, Toast.LENGTH_LONG).show()
    }
}