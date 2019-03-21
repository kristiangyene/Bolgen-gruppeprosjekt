package com.example.sea


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weatherapi/locationforecast/1.9/status?model=aromeecepsforecast/")
    fun getWeather(@Query("lat") lat: Double,
                   @Query("lon") lon: Double,
                   @Query("msl") msl: Int) : Call<Locationforecast>

}