package com.example.sea

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient() {
    fun getClient() : WeatherService {
        val client = Retrofit.Builder()
            .baseUrl("https://in2000-apiproxy.ifi.uio.no/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java) // Retrofit lager interface-et

        return client
    }
}
