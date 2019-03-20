package com.example.sea

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import android.support.v7.widget.GridLayoutManager



class NowFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private lateinit var strings : HashMap<String, String>
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    var listOfElements: ArrayList<Widget> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_now, container, false)
        sharedPreferences = activity!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)


        strings = hashMapOf(resources.getString(R.string.navigation_drawer_visibility) to "",
            resources.getString(R.string.navigation_drawer_wind) to "", resources.getString(R.string.navigation_drawer_wave) to "")

        //adding default widgets
        strings.forEach{ (key, value) ->
            val element = Widget(value, key)
            listOfElements.add(element)
        }
        getOtherWidgets()
        recyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = GridLayoutManager(context, 1)
        adapter = NowAdapter(listOfElements)
        recyclerView!!.adapter = adapter
        return rootView

    }
    fun getOtherWidgets(){
        val parameters = arrayOf(
            resources.getString(R.string.navigation_drawer_tide),
            "Grader",
            resources.getString(R.string.navigation_drawer_weather),
            resources.getString(R.string.navigation_drawer_fog),
            resources.getString(R.string.navigation_drawer_humidity),
            resources.getString(R.string.navigation_drawer_cloudiness))
        for(item in 0 until parameters.size) {
            if(sharedPreferences.getBoolean(parameters[item], false) && parameters[item] !in strings){
                strings.put(parameters[item], "")
                val element = Widget("", parameters[item])
                listOfElements.add(element)
            }
            strings.remove(parameters[item])
        }
    }
}

