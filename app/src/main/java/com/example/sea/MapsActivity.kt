package com.example.sea

import android.content.pm.PackageManager
import android.content.res.Configuration
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

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker : Marker? = null
    lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val tag = "MapActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Setter språket til norsk
        val language = "no"
        val config = Configuration()
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.setLocale(locale)
        this.createConfigurationContext(config)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    private fun initMap() {
        Log.d(tag, "initMap: initializing map")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        // TODO: Finne grensen til Norge så vi kan begrense kartet
        // Lager en LatLngBound som inkulderer Norge.
//        val Norway = LatLngBounds(LatLng(63.364096, 10.228577), LatLng(66.130335, 16.001404))
//        // Begrenser kamera rundt Norge
//        mMap.setLatLngBoundsForCameraTarget(Norway)
//        mMap.setMinZoomPreference(4.0f)

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)

        getLocationPermission()
        if(checkPermission()) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
                }
            }
        }
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
        return ContextCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MapsActivity, "Permission accepted", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@MapsActivity, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onMapClick(p0: LatLng?) {
        if(marker != null) {
            marker!!.remove()
        }

        val locationName = getCity(p0!!.latitude, p0.longitude)
        marker = mMap.addMarker(MarkerOptions().position(p0).title(locationName))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 8f), 2000, null)
    }

    private fun getCity(lat : Double, long : Double) : String {
        val addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())
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

    // TODO: håndtere klikk på marker eller lokasjon?
    override fun onMarkerClick(p0: Marker?) = false
    override fun onMyLocationButtonClick() = false

    // TODO: be brukeren å skru på location på mobilen hvis den er skrudd av
    // TODO: oppdatering av lokasjon?
    // TODO: Mulighet til å søke steder eller skrive koordinater?
}
