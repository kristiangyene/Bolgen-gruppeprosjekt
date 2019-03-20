package com.example.sea


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Definerer API endepunkter
interface WeatherService {
    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/oceanforecast/0.9/.json")
    fun getOceanData(@Query("lat") lat: Double,
                   @Query("lon") lon: Double) : Call<OceanData>
}