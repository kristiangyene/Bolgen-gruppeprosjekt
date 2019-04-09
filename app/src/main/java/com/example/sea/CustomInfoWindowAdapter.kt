package com.example.sea

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import java.text.DecimalFormat

class CustomInfoWindowAdapter(val context : Context, val lat : String, val long : String, val foundAddress : Boolean, val address : String) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View? {
        val format = DecimalFormat("#.###")
        val markerLat = p0!!.position.latitude
        val markerLong = p0.position.longitude

        if(format.format(markerLat) == lat && format.format(markerLong) == long) {
            val view = (context as Activity).layoutInflater.inflate(R.layout.custom_map_marker, null)

            view.findViewById<TextView>(R.id.latitude).text = lat
            view.findViewById<TextView>(R.id.longitude).text = long
            if(foundAddress) {
                view.findViewById<TextView>(R.id.address).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.address).text = address
            }
            else {
                view.findViewById<TextView>(R.id.address).visibility = View.GONE
            }

            return view
        }

        return null
    }
    override fun getInfoWindow(p0: Marker?) = null
}
