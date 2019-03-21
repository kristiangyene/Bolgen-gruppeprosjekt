package com.example.sea

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
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
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnSuccessListener<Location> {
    override fun onSuccess(location: Location?) {
        if (location != null) {
            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
        }
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker : Marker? = null
    private lateinit var lastLocation: Location
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }

        val locationName = getCity(p0!!.latitude, p0.longitude)
        marker = map.addMarker(MarkerOptions().position(p0).title(locationName))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
    }

    private fun getCity(lat : Double, long : Double) : String {
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
            val address = addresses[0]
            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            val addressFragments = with(address) { (0..maxAddressLineIndex).map { getAddressLine(it)}}
            return addressFragments.joinToString(separator = "\n")
        }
        return "Fant ikke addressen"
    }

    override fun onMarkerClick(p0: Marker?) = false
    override fun onMyLocationButtonClick() = false

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
        val Norway = LatLngBounds(
            LatLng(52.177844, -15.245368),
            LatLng(81.685035, 49.537870)
        )

        // Begrenser kamera rundt Norge
        map.setLatLngBoundsForCameraTarget(Norway)
        map.setMinZoomPreference(4.0f)

        // Setter map style
        val style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style)
        map.setMapStyle(style)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

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