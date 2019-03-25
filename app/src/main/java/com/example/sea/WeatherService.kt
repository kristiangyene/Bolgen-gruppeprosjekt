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

    @Headers("User-Agent: Gruppe17")
    @GET("weatherapi/locationforecast/1.9/.json")
    fun getLocationData(@Query("lat") lat: Double,
                        @Query("lon") lon: Double,
                        @Query("msl") msl: Double) : Call<LocationData> // msl parameteret er valgfri, send inn null hvis du ikke vil sende inn msl verdi

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
    fun getTidalWater(@Query("harbor") harbor: String,
                      @Query("content_type") contentType: String,
                      @Query("datatype") datatype: String) : Call<String>
}