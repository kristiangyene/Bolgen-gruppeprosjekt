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
        //recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.adapter = HourlyAdapter(listWithData, recyclerView)

    }

    override fun getList(): ArrayList<HourlyElement> {
        return listWithData
    }
}