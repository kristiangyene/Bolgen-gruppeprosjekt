package com.example.sea.ui.now

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.example.sea.R
import com.example.sea.utils.ConnectionUtil

class NowFragment : Fragment(), NowContract.View {
    private lateinit var adapter: NowAdapter
    private val listOfElements = ArrayList<NowElement>()
    private lateinit var rootView: View
    private lateinit var seekbar: SeekBar
    private lateinit var presenter: NowContract.Presenter
    private val fileName = "com.example.sea"
    private lateinit var indeterminateBar : ProgressBar
    private lateinit var textScale : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_now, container, false)

        setupViews()

        presenter = NowPresenter(this, activity!!, NowInteractor(activity!!, fileName))
        if(ConnectionUtil.checkNetwork(activity!!)) {
            if(savedInstanceState == null) {
                presenter.fetchData(true)
            }
            else {
                presenter.fetchData(false)
            }
        }

        return rootView
    }

    override fun updateRecyclerView() {
        adapter.notifyDataSetChanged()
    }

    override fun setDataInRecyclerView(element: NowElement) {
        listOfElements.add(element)
    }

    override fun setDataInRecyclerViewStart(element: NowElement) {
        listOfElements.add(0, element)
    }

    override fun setDataInRecyclerViewPosition(index : Int, element: NowElement) {
        listOfElements.add(index, element)
    }

    override fun getList(): ArrayList<NowElement> = listOfElements

    override fun setSeekbarProgress(progress: Int) {
        if(seekbar.progress < progress || progress == 0) {
            seekbar.progress = progress
            seekbar.refreshDrawableState()
        }
    }

    override fun onFailure(t: String?) {
        if(t != null) {
            Log.d("Failure: ", t)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // presenter.onDestroy()
    }

    private fun setupViews() {
        seekbar = rootView.findViewById(R.id.seekbar)
        seekbar.isEnabled = false

        textScale = rootView.findViewById(R.id.textScale)

        indeterminateBar = rootView.findViewById(R.id.indeterminateBar)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        adapter = NowAdapter(listOfElements, activity!!)
        recyclerView.adapter = adapter
    }

    override fun updateTextScale(text: String) {
        textScale.text = text
    }

    override fun getTextScaleLines(): Int = textScale.lineCount

    override fun showProgress() {
        indeterminateBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        indeterminateBar.visibility = View.GONE
    }
}