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
    @SerializedName("mox:seaIcePresence") val seaIcePresence : OceanValue,
    @SerializedName("mox:seaCurrentDirection") val seaCurrentDirection : OceanValue,
    @SerializedName("mox:significantTotalWaveHeight") val significantTotalWaveHeight : OceanValue,
    @SerializedName("mox:seaCurrentSpeed") val seaCurrentSpeed : OceanValue,
    @SerializedName("mox:seaBottomTopography") val seaBottomTopography : OceanValue,
    @SerializedName("mox:meanTotalWaveDirection") val meanTotalWaveDirection : OceanValue,
    @SerializedName("mox:seaTemperature") val seaTemperature : OceanValue
)

data class OceanValue (
    val uom : String,
    val content : String
)
