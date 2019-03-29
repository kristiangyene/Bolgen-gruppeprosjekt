package com.example.sea

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.view_pager.*
import java.io.IOException
import java.text.DecimalFormat
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location> {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker : Marker? = null
    private lateinit var lastLocation: Location
    private var lat : Double? = null
    private var long : Double? = null
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)

        getLocationPermission()
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

        this.lat = lat
        this.long = long

        if(addresses.isNotEmpty()) {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            val addressFragments = with(address) { (0..maxAddressLineIndex).map { getAddressLine(it)}}
            return addressFragments.joinToString(separator = "\n")
        }
        return "$lat , $long"
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }

        val locationName = getAddress(p0!!.latitude, p0.longitude)
        marker = map.addMarker(MarkerOptions().position(p0).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
    }

    override fun onMarkerClick(p0: Marker?) : Boolean {
        val format = DecimalFormat("#.###")
        activity!!.toolbar.title = "${format.format(lat)} , ${format.format(long)}"
        val tab = activity!!.findViewById<TabLayout>(R.id.tabs)
        tab.getTabAt(0)!!.select()
        return true
    }

    override fun onMyLocationButtonClick() = false

    private fun getLocationPermission() {
        if(checkPermission()) {
            Log.e("permission", "Permission already granted.")
        }
        else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            MapFragment.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MapFragment.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity!!, "Permission accepted", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(activity!!, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}