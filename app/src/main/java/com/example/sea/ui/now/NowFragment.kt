package com.example.sea.ui.now

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import com.example.sea.R

class NowFragment : Fragment(), NowContract.View {
    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: NowAdapter
    private val listOfElements = ArrayList<NowElement>()
    private lateinit var rootView: View
    private lateinit var seekbar: SeekBar
    private lateinit var presenter: NowContract.Presenter
    private val fileName = "com.example.sea"
    private lateinit var indeterminateBar : ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_now, container, false)

        setupViews()

        presenter = NowPresenter(this, activity!!, NowInteractor(activity!!, fileName))
        presenter.fetchData()

        return rootView
    }

    override fun updateRecyclerView() {
        adapter.notifyDataSetChanged()
    }

    override fun setDataInRecyclerView(element: NowElement) {
        listOfElements.add(element)
    }

    override fun setSeekbarProgress(progress: Int) {
        if(seekbar.progress < progress || progress == 0) {
            seekbar.progress = progress
            seekbar.refreshDrawableState()
        }
    }

    override fun onFailure(t: Throwable) {
        Log.d("Failure: ", t.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun setupViews() {
        seekbar = rootView.findViewById(R.id.seekbar)
        seekbar.isEnabled = false

        indeterminateBar = rootView.findViewById(R.id.indeterminateBar)

        recyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = GridLayoutManager(context, 1)
        adapter = NowAdapter(listOfElements, activity!!)
        recyclerView!!.adapter = adapter
    }

    override fun showProgress() {
        indeterminateBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        indeterminateBar.visibility = View.GONE
    }
}