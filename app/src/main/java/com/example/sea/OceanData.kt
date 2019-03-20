package com.example.sea

import com.google.gson.annotations.SerializedName

data class OceanData (
    @SerializedName("mox:forecast") val forecast : List<Forecast>,
    @SerializedName("mox:issueTime") val issueTime : IssueTime
)

data class IssueTime (
    @SerializedName("gml:TimeInstant") val timeInstant : TimeInstant,
    @SerializedName("gml:timePosition") val time : String
)

data class TimeInstant (
    @SerializedName("gml:timePosition") val time : String
)

data class Forecast (
    @SerializedName("metno:OceanForecast") val oceanForecast : OceanForecast
)

data class OceanForecast (
    @SerializedName("gml:id") val id : String,
    @SerializedName("mox:seaIcePresence") val seaIcePresence : Value,
    @SerializedName("mox:seaCurrentDirection") val seaCurrentDirection : Value,
    @SerializedName("mox:significantTotalWaveHeight") val significantTotalWaveHeight : Value,
    @SerializedName("mox:seaCurrentSpeed") val seaCurrentSpeed : Value,
    @SerializedName("mox:seaBottomTopography") val seaBottomTopography : Value,
    @SerializedName("mox:meanTotalWaveDirection") val meanTotalWaveDirection : Value,
    @SerializedName("mox:seaTemperature") val seaTemperature : Value
)

data class Value (
    val uom : String,
    val content : String
)
