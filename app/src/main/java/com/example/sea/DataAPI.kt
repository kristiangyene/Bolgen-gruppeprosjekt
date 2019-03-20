package com.example.sea

import android.widget.Toast
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataAPI() {
    fun fetchData(activity: MainActivity) {
        // Call. Lager REST Clienten, setter base URL-en, konverter og servicen, og til slutt så kaller vi
        val retrofit = Retrofit.Builder()
            .baseUrl("https://in2000-apiproxy.ifi.uio.no/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java) // Retrofit lager interface-et

//        call.execute er synchronous, og kjører på tråden man er allerede på, og det er ikke mulig å drive men nettverk operasjoner på main tråden så
//        vil vi få en exception, vil freeze appen.
//        Bruker heller retrofit.enqueue får å kjøre på background tråd, vi trenger ikke å lage en background tråd, dette ordner Retrofit

        val client = Retrofit.Builder()
            .baseUrl("https://in2000-apiproxy.ifi.uio.no/" )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        val callOceanData = client.getOceanData(60.1, 5.0)
        callOceanData.enqueue(object : Callback<OceanData> {
            override fun onResponse(call: Call<OceanData>, response: Response<OceanData>) {
                if(response.isSuccessful && response.code() == 200) {
                    val product =  response.body()?.forecast?.get(0)?.oceanForecast?.temperature?.uom
                }
            }

            override fun onFailure(call: Call<OceanData>, t: Throwable) {
                Toast.makeText(activity, "rip", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

data class OceanData (
    @SerializedName("xmlns:xlink") val link : String,
    @SerializedName("mox:forecast") val forecast : List<Forecast>,
    @SerializedName("gml:description") val description : String,
    @SerializedName("xmlns:gml") val gml : String,
    @SerializedName("gml:id") val id : String
)

data class Forecast (
    @SerializedName("metno:OceanForecast") val oceanForecast : OceanForecast
)

data class OceanForecast (
    @SerializedName("gml:id") val id : String,
    @SerializedName("mox:seaTemperature") val temperature : SeaTemperature
)

data class SeaTemperature (
    val uom : String,
    val content : Double
)