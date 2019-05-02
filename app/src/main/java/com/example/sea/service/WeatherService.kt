package com.example.sea.service


import com.example.sea.LocationData
import com.example.sea.service.model.OceanData
import com.example.sea.service.model.SunData
import com.example.sea.service.model.TextData
import com.example.sea.service.model.WindData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Definerer API endepunkter
interface WeatherService {
    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/oceanforecast/0.9/.json")
    fun getOceanData(@Query("lat") lat: Float,
                   @Query("lon") lon: Float) : Call<OceanData>

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/locationforecast/1.9/.json")
    fun getLocationData(@Query("lat") lat: Float,
                        @Query("lon") lon: Float,
                        @Query("msl") msl: Float?) : Call<LocationData> // msl parameteret er valgfri, send inn null hvis du ikke vil sende inn msl verdi

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/spotwind/1.0/.json")
    fun getWindData() : Call<WindData>

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/textforecast/2.0/")
    fun getTextData(@Query("forecast") forecast: String) : Call<TextData>

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/sunrise/2.0/")
    fun getSunData(@Query("lat") lat: Double,
                   @Query("lon") lon: Double,
                   @Query("height") height: Int?,
                   @Query("date") date: String,
                   @Query("offset") offset: String,
                   @Query("days") days: Int?) : Call<SunData>

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/tidalwater/1.1/")
    fun getTidalWater(@Query("harbor") harbor: String) : Call<String>
}