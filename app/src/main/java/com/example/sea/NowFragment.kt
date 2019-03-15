package com.example.sea


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import android.support.v7.widget.GridLayoutManager




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NowFragment : Fragment() {
    val strings: HashMap<String, String> = hashMapOf("visibility" to "2.4km", "wind" to "5.0m/s", "waves" to "3.0m")
    private var recyclerView: RecyclerView? = null

    private var adapter: RecyclerView.Adapter<*>? = null
    var listOfElements: ArrayList<Widget> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_now, container, false)


        //adding default widgets
        strings.forEach{ (key, value) ->
            val element = Widget(value, key)
            listOfElements.add(element)

        }
        recyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = GridLayoutManager(context, 1) as RecyclerView.LayoutManager?
        adapter = NowAdapter(listOfElements)
        recyclerView!!.adapter = adapter
        return rootView

    }
}

