package com.example.sea


data class LocationData (
    val product : Product,
    val created : String
) {
    data class Product (
        val time : List<Time>
    ) {
        data class Time (
            val to : String,
            val from : String,
            val location : Location
        ) {
            data class Location (
                val altitude : String,
                val latitude : String,
                val longitude : String,
                val temperature : LocationValue,
                val precipitation : LocationValue,
                val windDirection : LocationValue,
                val cloudiness : LocationValue,
                val pressure : LocationValue,
                val fog : LocationValue,
                val windSpeed : LocationValue,
                val areaMaxWindSpeed : LocationValue,
                val humidity : LocationValue,
                val dewpointTemperature : LocationValue,
                val windGust : LocationValue
            ) {
                data class LocationValue (
                    val value : String,
                    val unit : String,
                    val id : String,
                    val percent : String,
                    val deg : String,
                    val name : String,
                    val beaufort : String,
                    val minvalue : String,
                    val maxValue: LocationValue,
                    val mps : String
                )
            }
        }
    }
}
