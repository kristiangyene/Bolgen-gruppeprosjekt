package com.example.sea

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng




class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {
    override fun onMarkerClick(p0: Marker?) = false
    override fun onMyLocationButtonClick() = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permission = 1
    private var marker : Marker? = null
    private val tag = "MapActivity"
    private var persmissionSuccess = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
        if(persmissionSuccess) {
            initMap()
        }
    }


    private fun getLocationPermission() {
        if(checkPermission()) {
            Log.e("permission", "Permission already granted.")
            persmissionSuccess = true
        }
        else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), permission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permission -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MapsActivity, "Permission accepted", Toast.LENGTH_LONG).show()
                    persmissionSuccess = true
                }
                else {
                    Toast.makeText(this@MapsActivity, "Permission denied", Toast.LENGTH_LONG).show()
                    persmissionSuccess = false
                }
            }
        }
    }

    private fun initMap() {
        Log.d(tag, "initMap: initializing map")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getCity(lat : Double, long : Double) : String? {
        var addresses: List<Address> = emptyList()
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(lat, long, 1)
        }
        catch (ioException: IOException) {
            // Catch network or other I/O problems.
            Log.e("E", "Error", ioException)
        }
        catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e("E", "Error", illegalArgumentException)
        }

        val address = addresses[0]
        // Fetch the address lines using getAddressLine, join them, and send them to the thread.
        val addressFragments = with(address) { (0..maxAddressLineIndex).map { getAddressLine(it)}}

        return addressFragments.joinToString(separator = "\n")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show()
        Log.d(tag, "onMapReady: map is ready")
        mMap = googleMap
        getLocation()
        if(checkPermission()) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)
        }

        // Lager en LatLngBound som inkulderer Norge.
//        val Norway = LatLngBounds(LatLng(63.364096, 10.228577), LatLng(66.130335, 16.001404))
//        // Begrenser kamera rundt Norge
//        mMap.setLatLngBoundsForCameraTarget(Norway)
//        mMap.setMinZoomPreference(4.0f)

        // Legger en marker i posisjon og beveger kamera til posisjonen
//        if(location == null) {
//            Toast.makeText(this, "Klarte ikke å finne posisjon", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            val locationName = getCity(location!!.latitude, location!!.longitude)
//            if(locationName != null) {
//                marker = mMap.addMarker(MarkerOptions().position(location!!).title(locationName))
//            }
//            else {
//                marker = mMap.addMarker(MarkerOptions().position(location!!).title("Fant ikke noe"))
//            }
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location!!, 8.0f))
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location!!.longitude), 8.0f), 2000, null)
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(), 8.0f), 2000, null)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)
    }

    private fun getLocation() {
        if(checkPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location != null) {

                }
            }
        }
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }
        val locationName = getCity(p0!!.latitude, p0.longitude)
        if(locationName != null) {
//            marker = mMap.addMarker(MarkerOptions().position(p0).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            marker = mMap.addMarker(MarkerOptions().position(p0).title(locationName))
        }
        else {
            marker = mMap.addMarker(MarkerOptions().position(p0).title("Fant ikke noe"))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8.0f), 2000, null)
    }

}
