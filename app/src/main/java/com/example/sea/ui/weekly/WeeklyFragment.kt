package com.example.sea.ui.weekly

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.sea.R
import com.example.sea.utils.ConnectionUtil

class WeeklyFragment : Fragment(), WeeklyContract.View {
    private val fileName = "com.example.sea"
    private val listWithData = ArrayList<WeeklyElement>()
    private lateinit var adapter: WeeklyAdapter
    private lateinit var rootView: View
    private lateinit var presenter: WeeklyContract.Presenter
    private lateinit var indeterminateBar : ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_weekly, container, false)

        setUpViews()

        presenter = WeeklyPresenter(this, WeeklyInteractor(activity!!, fileName))
        if(ConnectionUtil.checkNetwork(activity!!)) {
            if(savedInstanceState == null) {
                presenter.fetchData(true)
            }
            else {
                presenter.fetchData(false)
            }
        }
        else {
            return inflater.inflate(R.layout.no_internet, container, false)
        }

        return rootView
    }

    private fun setUpViews(){
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WeeklyAdapter(listWithData)
        recyclerView.adapter = adapter

        indeterminateBar = rootView.findViewById(R.id.indeterminateBar)
    }

    override fun setDataInRecyclerView(element: WeeklyElement) {
        listWithData.add(element)
    }

    override fun updateRecyclerView() {
        adapter.notifyDataSetChanged()
    }

    override fun getList(): ArrayList<WeeklyElement> = listWithData

    override fun onDestroy() {
        super.onDestroy()
        // presenter.onDestroy()
    }

    override fun onFailure(t: String?) {
        if(t != null) {
            Log.e("Error", t)
        }
    }

    override fun showProgress() {
        indeterminateBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        indeterminateBar.visibility = View.GONE
    }
}