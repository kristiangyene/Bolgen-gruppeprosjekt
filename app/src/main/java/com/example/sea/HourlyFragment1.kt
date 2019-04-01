@file:Suppress("DEPRECATION")

package com.example.sea

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import co.metalab.asyncawait.async
import kotlinx.android.synthetic.main.fragment_hourly.*

import net.cachapa.expandablelayout.ExpandableLayout
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class HourlyFragment1 : Fragment() {
    val listWithData = ArrayList<HourlyElement>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_hourly, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview1)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(SimpleAdapter(listWithData,recyclerView))
        threadcreation()
        return rootView
    }


    fun threadcreation(){
        val client = RetrofitClient().getClient("json")
        val locationCall = client.getLocationData(60.10, 9.58, null )
        val oceanCall = client.getOceanData(60.10, 5.0)
         thread {
             val bodyLocation = locationCall.execute().body()
             location(bodyLocation)
             val bodyOcean = oceanCall.execute().body()
             ocean(bodyOcean)

         }


    }

    /*
    fun fetchDataLocation(){
        //val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)
        val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val format_to = SimpleDateFormat("H")
        //formatter.setTimeZone(TimeZone.getTimeZone("GMT"))

        val client = RetrofitClient().getClient()
        //val locationcCall = client.getLocationData(60.10, 9.58, null)
        //var test = HourlyElement(null, null, null)
        //val checkList = ArrayList<Int>()
        val locationCall = client.getLocationData(60.10, 9.58, null)
        val oceanCall = client.getOceanData(60.10, 5.0)

        thread {
            val bodyLocation = locationCall.execute().body()
            location(bodyLocation)
            val bodyOcean = oceanCall.execute().body()
            ocean(bodyOcean)

        }

//        locationcCall.enqueue(object : Callback<LocationData> {
//            override fun onFailure(call: Call<LocationData>, t: Throwable) {
//                Toast.makeText(activity!!, "SHIT LOC", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
//                if (response.isSuccessful && response.code() == 200) {
//                    val output = response.body()?.product?.time
//                    val checkList = ArrayList<Int>()
//                    //
//                    try {
//                        if (output != null) {
//                            for (i in output) {
//                                val from = formatter_from.parse(i.to)
//                                val toFormatted = format_to.format(from)
//                                var windspeed = i.location?.windSpeed?.mps
//                                //Toast.makeText(activity!!, windspeed, Toast.LENGTH_LONG).show()
//
//                                if (toFormatted.toInt() !in checkList) {
//                                    checkList.add(toFormatted.toInt())
//                                    //test.title = ("KL" + toFormatted)
//                                    //test.vindspeed = windspeed+"m/s"
//                                    //listWithData.add(test)
//                                    listWithData.add(HourlyElement("KL" + toFormatted, windspeed + "m/s", ""))
//                                    recyclerview1.adapter?.notifyDataSetChanged()
//
//                                }
//                            }
//                        }
//                        //
//                        // finishedData = listWithData
//                        //Toast.makeText(activity!!, "Lokasjon" + listWithData.isEmpty().toString(), Toast.LENGTH_SHORT).show()
//
//
//                        //val windspeed = output?.get(0)?.location?.windSpeed?.mps
//                        //val a = format_to.format(d)
//                        //listWithData.add(HourlyElement("KL" + a))
//                        //Toast.makeText(activity!!, windspeed, Toast.LENGTH_LONG).show()
//
//
//                    } catch (e: ParseException) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace()
//                    }
//
//                    //val d  = formatter_from.parse(output?.get(0)?.to)
//                    //val finishedformat = format_to.format(d)
//
//                    //Toast.makeText(activity!!, output?.get(0)?.to, Toast.LENGTH_LONG).show()
//
//                }
//            }
//        })

        //Thread.sleep(1000)
        //val oceanCall = client.getOceanData(60.10, 5.0)
    }
    */
    private fun location(locationData : LocationData?) {
        val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val format_to = SimpleDateFormat("H")
        val output = locationData?.product?.time!!
        val checkList = ArrayList<Int>()
        if (output != null) {
            for (i in output) {
                val from = formatter_from.parse(i.to)
                val toFormatted = format_to.format(from)
                var windspeed = i.location?.windSpeed?.mps
                var fog = i.location?.fog?.percent
                var temp = i.location?.temperature?.value
                var humid = i.location?.humidity?.value
                var rainfall = output[1].location?.precipitation
                //Toast.makeText(activity!!, windspeed, Toast.LENGTH_LONG).show()

                if (toFormatted.toInt() !in checkList) {
                    checkList.add(toFormatted.toInt())
                    //test.title = ("KL" + toFormatted)
                    //test.vindspeed = windspeed+"m/s"
                    //listWithData.add(test)
                    listWithData.add(HourlyElement("KL" + toFormatted, windspeed + "m/s", "",fog+"%", temp+"ºC", "Tide", rainfall.value+rainfall.unit, "Visibility", humid+"%"))
                    //recyclerview1.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun ocean(oceanData : OceanData?) {
        val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val format_to = SimpleDateFormat("H")
        val output = oceanData?.forecast
        if (output != null) {
            for(i in output) {
                val hour = i.oceanForecast.validTime.timePeriod.begin
                val from = formatter_from.parse(hour)
                val wavesformat = format_to.format(from)
                for(x in listWithData){
                    if(x.title.equals("KL"+wavesformat)){
                        val typo = i.oceanForecast.seaCurrentSpeed
                        x.waves = typo.content+typo.uom
                        //recyclerview1.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }



    /*
    fun fetchOceandata(){
        val client = RetrofitClient().getClient()
        val formatter_from = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val format_to = SimpleDateFormat("H")
        val oceanCall = client.getOceanData(60.10, 5.0)
        oceanCall.enqueue(object : Callback<OceanData>{
            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                Toast.makeText(activity!!, "SHIT OC", Toast.LENGTH_LONG).show()

            }

            override fun onResponse (call: Call<OceanData>, response: Response<OceanData>) {
                if(response.isSuccessful && response.code() == 200){
                    //val output = response.body()?.product?.time
                    val output = response.body()?.forecast
                    val checkList = ArrayList<Int>()
                    val fakeData = listWithData
                    //Toast.makeText(activity!!, output.toString(), Toast.LENGTH_LONG).show()
                    //Toast.makeText(activity!!, "Ocean"+listWithData.isEmpty().toString(), Toast.LENGTH_SHORT).show()
                    if (output != null) {
                        for(i in output) {
                            val hour = i.oceanForecast.validTime.timePeriod.begin
                            val from = formatter_from.parse(hour)
                            val wavesformat = format_to.format(from)
                            //Toast.makeText(activity!!, "Listesjekk"+listWithData[0].title, Toast.LENGTH_SHORT).show()
                            //Toast.makeText(activity!!, "Wavessjekk"+wavesformat, Toast.LENGTH_SHORT).show()


                            for(x in listWithData){
                                //Log.d("Abe","Hei")
                                //Toast.makeText(activity!!, "Før if", Toast.LENGTH_SHORT).show()

                                if(x.title.equals("KL"+wavesformat)){
                                    //Toast.makeText(activity!!, "Etter if", Toast.LENGTH_SHORT).show()

                                    checkList.add(wavesformat.toInt())
                                    val typo = i.oceanForecast.seaCurrentSpeed
                                    x.waves = typo.content+typo.uom
                                    recyclerview1.adapter?.notifyDataSetChanged()


                                }
                                //recyclerView.adapter?.notifyDataSetChanged()
                            }
                            //val testlist = checkList
                            //val hour = i.oceanForecast.validTime.timePeriod.begin

                            //Toast.makeText(activity!!, hour, Toast.LENGTH_LONG).show()

                            /*                            val from = formatter_from.parse(hour)
                                                        println(from)
                                                        val wavesformat = format_to.format(from).toInt()

                                                        if(wavesformat in testlist){
                                                            val typo = i.oceanForecast.seaCurrentSpeed
                                                            test.waves = (typo.content+typo.uom)
                                                            listWithData.add(test)
                                                            testlist.remove(wavesformat)
                                                        }*/

                            //Toast.makeText(activity!!, typo.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
        })

    }
     */
    private class SimpleAdapter(private val list: List<HourlyElement>, private val recyclerView: RecyclerView) :
        RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
        private var selectedItem = UNSELECTED

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.hourlylistview, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val time: HourlyElement = list[position]
            holder.bind(time)
            holder.bindItems(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
            ExpandableLayout.OnExpansionUpdateListener {
            private val expandableLayout: ExpandableLayout
            private val expandButton: CardView

            init {

                expandableLayout = itemView.findViewById(R.id.expandable_layout_2)
                expandableLayout.setInterpolator(OvershootInterpolator())
                expandableLayout.setOnExpansionUpdateListener(this)
                expandButton = itemView.findViewById(R.id.expand_button2)

                expandButton.setOnClickListener(this)
            }

            fun bind(time: HourlyElement) {
                val position = adapterPosition
                val isSelected = position == selectedItem


                //expandButton.text = "$position. Tap to expand"
                expandButton.isSelected = isSelected
                expandableLayout.setExpanded(isSelected, true)

            }

            fun bindItems(e : HourlyElement){
                val textTitle = itemView.findViewById(R.id.KL) as TextView
                val vind = itemView.findViewById(R.id.hour_vind) as TextView
                val waves = itemView.findViewById(R.id.hour_bølge) as TextView
                val fog = itemView.findViewById(R.id.hour_fog) as TextView
                val temp = itemView.findViewById(R.id.hour_thermo) as TextView
                val tide = itemView.findViewById(R.id.hour_tide) as TextView
                val rain = itemView.findViewById(R.id.hour_water) as TextView
                val visi = itemView.findViewById(R.id.hour_visi) as TextView
                val humid = itemView.findViewById(R.id.hour_humid) as TextView


                textTitle.text = e.title
                vind.text = e.vindspeed
                waves.text = e.waves
                fog.text = e.fog
                temp.text = e.temp
                tide.text = e.tide
                rain.text = e.rain
                visi.text = e.visi
                humid.text = e.humid
            }

            override fun onClick(view: View) {
                val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
                if (holder != null) {
                    holder.expandButton.isSelected = false
                    holder.expandableLayout.collapse()
                    //Toast.makeText(view.context, "Collapsing", LENGTH_SHORT).show()
                }

                val position = adapterPosition
                if (position == selectedItem) {
                    selectedItem = UNSELECTED
                } else {
                    expandButton.isSelected = true
                    expandableLayout.expand()
                    //Toast.makeText(view.context, "Expanding", LENGTH_SHORT).show()
                    selectedItem = position
                }
            }

            /*override fun onClick(view: View) {
                val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
                if(holder != null) {
                    holder.expandableLayout!!.expand()

                    /*if (holder.expandableLayout!!.isExpanded) {
                        holder.expandableLayout!!.collapse()
                    } else {
                        holder.expandableLayout!!.expand()
                    }*/
                }
            }*/


            override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
                Log.d("ExpandableLayout", "State: $state")
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(adapterPosition)
                }
            }
        }

        companion object {
            private val UNSELECTED = -1
        }
    }
}