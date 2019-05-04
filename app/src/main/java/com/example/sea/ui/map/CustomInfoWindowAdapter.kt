package com.example.sea.ui.map

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.sea.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.text.DecimalFormat

class CustomInfoWindowAdapter(val context : Context, val position: LatLng?, private val foundAddress : Boolean, private val address : String) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View? {
        val format = DecimalFormat("#.###")

        if(p0!!.position.latitude == position!!.latitude && p0.position.longitude == position.longitude) {
            val view = (context as Activity).layoutInflater.inflate(R.layout.custom_map_marker, null)

            view.findViewById<TextView>(R.id.latitude).text = format.format(position.latitude)
            view.findViewById<TextView>(R.id.longitude).text = format.format(position.longitude)
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
