package com.example.sea

data class WindData (
    val product : Product
) {
    data class Product (
        val time : List<Time>,
        val observation : String
    ) {
        data class Time (
            val location : List<Location>,
            val valid : String
        ) {
            data class Location (
                val spotWind : List<SpotWind>,
                val name : String
            ) {
                data class SpotWind (
                    val temperature : String,
                    val flightlevel : String,
                    val windSpeed : String,
                    val windDirection : String
                )
            }
        }
    }
}