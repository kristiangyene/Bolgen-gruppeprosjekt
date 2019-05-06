@file:Suppress("DEPRECATION")

package com.example.sea.data.remote

import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Suppress("DEPRECATION")
class RetrofitClient {
    fun getClient(formatString : String) : WeatherService {
        val format : Converter.Factory?
        when (formatString) {
            "xml" -> format = SimpleXmlConverterFactory.create()
            "json" -> format = GsonConverterFactory.create()
            else -> format = ScalarsConverterFactory.create()
        }

        return Retrofit.Builder()
            .baseUrl("https://in2000-apiproxy.ifi.uio.no/")
            .addConverterFactory(format!!)
            .build()
            .create(WeatherService::class.java)
    }
}
