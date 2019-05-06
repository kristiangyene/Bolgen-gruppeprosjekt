package com.example.sea.data.remote.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "textforecast", strict = false)
data class TextData(
    @field:ElementList(name = "time", inline = true, required = false)
    var time : List<Time>? = null
) {
    @Root(name = "time", strict = false)
    data class Time(
        @field:Attribute(name = "from", required = false)
        var from: String? = null,
        @field:Attribute(name = "to", required = false)
        var to: String? = null,
        @field:ElementList(name  = "forecasttype", inline = true, required = false)
        var forecast: List<Forecast>? = null
    ) {
        @Root(name = "forecasttype", strict = false)
        data class Forecast(
            @field:Attribute(name = "name", required = false)
            var name: String? = null,
            @field:ElementList(name = "location", inline = true, required = false)
            var location: List<Location>? = null
        ) {
            @Root(name = "location", strict = false)
            data class Location(
                @field:Attribute(name = "name", required = false)
                var name: String? = null,
                @field:Attribute(name = "id", required = false)
                var id: String? = null,
                @field:Text(required = false)
                var content : String? = null
            )
        }
    }
}
