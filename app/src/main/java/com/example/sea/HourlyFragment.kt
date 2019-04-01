package net.cachapa.expandablelayoutdemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sea.R
import net.cachapa.expandablelayout.ExpandableLayout
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import com.example.sea.HourlyElement
import com.example.sea.ListAdapter
import kotlinx.android.synthetic.main.fragment_hourly.*
import kotlinx.android.synthetic.main.fragment_hourly2.*
import org.w3c.dom.Text
import kotlin.collections.ArrayList


class HourlyFragment : Fragment(), View.OnClickListener {

    private var expandableLayout2: ExpandableLayout? = null
    //val listOfThings = ArrayList<HourlyElement>()
    //elements.add(HourlyElement("Testing"))
    private val thelist = listOf(HourlyElement("KL15"), HourlyElement("KL16"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private fun partItemClicked(partItem : String) {
        expand_button2?.setOnClickListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
                                inflater.inflate(R.layout.fragment_hourly2,
                                container,
                                false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView node initialized here
        recyclerview1.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ListAdapter(thelist, {testvar : HourlyElement -> partItemClicked("Testing")})
        }
        expandableLayout2 = recyclerview1.findViewById(R.id.expandable_layout_2)
        expandableLayout2?.setOnExpansionUpdateListener { expansionFraction, state ->
            Log.d(
                "ExpandableLayout1",
                "State: $state"
            )
        }
        //expand_button2?.setOnClickListener(this)

    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hourly2, container, false)
        //val elements = ArrayList<HourlyElement>()
        //elements.add(HourlyElement("Testing"))
        //recyclerview1.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        //expand_button!!.adapter = ListAdapter(elements)
        //recyclerview1.adapter = ListAdapter(elements)
        expandableLayout2 = rootView.findViewById(R.id.expandable_layout_2)


        expandableLayout2!!.setOnExpansionUpdateListener { expansionFraction, state ->
            Log.d(
                "ExpandableLayout1",
                "State: $state"
            )
        }

        //val btn = rootView.findViewById<Button>(R.id.expand_button).setOnClickListener(this)
        rootView.findViewById<CardView>(R.id.expand_button2).setOnClickListener(this)
        //rootView.findViewById(R.id.expand_button).setOnClickListener(this)

        return rootView
    }*/

    override fun onClick(view: View) {
        if (expandableLayout2!!.isExpanded) {
            expandableLayout2!!.collapse()
        } else {
            expandableLayout2!!.expand()
        }
    }

    companion object {
        fun newInstance(): HourlyFragment = HourlyFragment()
    }
    /*
    fun expand(v: View) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            protected fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            protected fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }
    */
}
