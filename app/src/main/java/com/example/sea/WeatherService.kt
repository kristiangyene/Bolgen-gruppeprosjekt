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

    @GET("weatherapi/locationforecast/1.9/.json")
    fun getLocationData(@Query("lat") lat: Double,
                        @Query("lon") lon: Double,
                        @Query("msl") msl: Double) : Call<LocationData> // msl parameteret er valgfri, send inn null hvis du ikke vil sende inn msl verdi

    @GET("weatherapi/spotwind/1.0/.json")
    fun getWindData() : Call<WindData>
}