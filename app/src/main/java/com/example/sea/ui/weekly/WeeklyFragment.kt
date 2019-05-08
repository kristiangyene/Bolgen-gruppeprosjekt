package com.example.sea.ui.weekly

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sea.R

class WeeklyFragment : Fragment(), WeeklyContract.View {
    private val listWithData = ArrayList<WeeklyElement>()
    private lateinit var adapter: WeeklyAdapter
    private val fileName = "com.example.sea"
    private lateinit var rootView: View
    private lateinit var presenter: WeeklyContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_weekly, container, false)

        setUpViews()

        presenter = WeeklyPresenter(this, WeeklyInteractor(activity!!, fileName))
        presenter.fetchData()

        return rootView
    }

    override fun setDataInRecyclerView(element: WeeklyElement) {
        listWithData.add(element)
    }

    override fun updateRecyclerView() {
        adapter.notifyDataSetChanged()
    }

    override fun getList(): ArrayList<WeeklyElement> {
        return listWithData
    }

    private fun setUpViews(){

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WeeklyAdapter(listWithData)
        recyclerView!!.adapter = adapter
    }
}