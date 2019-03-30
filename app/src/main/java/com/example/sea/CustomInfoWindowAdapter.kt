package com.example.sea

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(val context : Context, val lat : String, val long : String) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker?): View {
        val view = (context as Activity).layoutInflater.inflate(R.layout.custom_map_marker, null)

        view.findViewById<TextView>(R.id.latitude).text = lat
        view.findViewById<TextView>(R.id.longitude).text = long

        return view
    }
    override fun getInfoWindow(p0: Marker?) = null
}
