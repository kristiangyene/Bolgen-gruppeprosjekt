@file:Suppress("DEPRECATION")

package com.example.sea.ui.hourly
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sea.R
import com.google.android.gms.maps.model.LatLng

class HourlyFragment : Fragment(), HourlyContract.View {
    private val listWithData = ArrayList<HourlyElement>()
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private val harbors = hashMapOf<String, LatLng>()
    private var closestHarbor: String? = null
    private var closestHarborValue = Double.MAX_VALUE
    private lateinit var rootView: View
    private lateinit var presenter: HourlyContract.Presenter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_hourly, container, false)

        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        setUpViews()
        presenter = HourlyPresenter(this, activity!!, HourlyInteractor(activity!!, fileName))
        presenter.fetchData()

//        val currentLocation = Location("")
//        currentLocation.latitude = sharedPreferences.getFloat("lat", 60F).toDouble()
//        currentLocation.longitude = sharedPreferences.getFloat("long", 11F).toDouble()
//
//        harbors["andenes"] = LatLng(69.326233, 16.139759)
//        harbors["andenes"] = LatLng(69.326233, 16.139759)
//        harbors["bergen"] = LatLng(60.392353, 5.312078)
//        harbors["bodø"] = LatLng(67.289953, 14.396987)
//        harbors["hammerfest"] = LatLng(70.664762, 23.683317)
//        harbors["harstad"] = LatLng(68.801332, 16.548197)
//        harbors["heimsjø"] = LatLng(63.425898, 9.095695)
//        harbors["helgeroa"] = LatLng(58.994180, 9.856801)
//        harbors["honningsvåg"] = LatLng(70.981345, 25.968195)
//        harbors["kabelvåg"] = LatLng(68.230406, 14.566273)
//        harbors["kristiansund"] = LatLng(63.114018, 7.736651)
//        harbors["måløy"] = LatLng(61.933380, 5.113401)
//        harbors["narvik"] = LatLng(68.427514, 17.426371)
//        harbors["oscarsborg"] = LatLng(59.681414, 10.625639)
//        harbors["oslo"] = LatLng(59.904023, 10.738040)
//        harbors["rørvik"] = LatLng(64.859678, 11.237081)
//        harbors["stavanger"] = LatLng(58.972168, 5.727195)
//        harbors["tregde"] = LatLng(58.009595, 7.545120)
//        harbors["tromsø"] = LatLng(69.647098, 18.960921)
//        harbors["trondheim"] = LatLng(63.440268, 10.417503)
//        harbors["vardø"] = LatLng(70.374517, 31.103911)
//        harbors["ålesund"] = LatLng(62.475094, 6.150923)
//
//
//        for (harbor in harbors) {
//            val harborLocation = Location("")
//            harborLocation.latitude = harbor.value.latitude
//            harborLocation.longitude = harbor.value.longitude
//            val distance = currentLocation.distanceTo(harborLocation).toDouble()
//
//            if(distance < closestHarborValue) {
//                closestHarborValue = distance
//                closestHarbor = harbor.key
//                Log.d("Ahmed", "$closestHarbor $closestHarborValue")
//            }
//        }


        return rootView
    }

    private fun setUpViews() {
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview1)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HourlyAdapter(listWithData, recyclerView)
    }

    override fun setDataInRecyclerView(element: HourlyElement) {
        listWithData.add(element)
    }

    override fun updateRecyclerView() {
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun getList(): ArrayList<HourlyElement> {
        return listWithData
    }
}