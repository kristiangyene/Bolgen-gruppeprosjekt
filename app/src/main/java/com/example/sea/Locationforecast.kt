package com.example.sea
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
@Root(strict = false)
data class Locationforecast (
    @field:Element(name = "product")
    var product: pointData? = null
    ){
    data class pointData(
        @field:ElementList(inline = true)
        var time: List<Time>? = null
    ) {
        @Root(strict = false)
        data class Time(
            @field:Element(name = "location")
            var location: Location? = null
        ) {
            @Root(strict = false)
            data class Location(
                @field:Attribute(name = "altitude")
                var altitude: Double? = null,
                @field:Attribute(name = "latitude")
                var latitude: Double? = null,
                @field:Attribute(name = "longitude")
                var longitude: Double? = null
            )
        }
    }
}
