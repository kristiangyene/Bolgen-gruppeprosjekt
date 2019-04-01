package com.example.sea

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
data class SunData(
    @field:Attribute(name = "xmlns:xsi", required = false)
    var link: String? = null,

    @field:Attribute(name = "xsi:noNamespaceSchemaLocation", required = false)
    var no : String? = null,

    @field:Element(name = "location", required = false)
    var location : Location? = null
) {
    @Root(name = "location", strict = false)
    data class Location(
        @field:Attribute(name = "latitude", required = false)
        var latitude: String? = null,
        @field:Attribute(name = "longitude", required = false)
        var longitude: String? = null,
        @field:ElementList(name = "time", inline = true)
        var time : List<Time>? = null
    ) {
        @Root(name = "time", strict = false)
        data class Time(
            @field:Attribute(name = "date", required = false)
            var date : String? = null,
            @field:Element(name = "sunrise", required = false)
            var sunrise : Sunrise? = null,
            @field:Element(name = "sunset", required = false)
            var sunset : Sunset? = null
        ) {
            @Root(name = "sunrise", strict = false)
            data class Sunrise (
                @field:Attribute(name = "time", required = false)
                var time : String? = null,
                @field:Attribute(name = "desc", required = false)
                var desc : String? = null
            )

            @Root(name = "sunset", strict = false)
            data class Sunset (
                @field:Attribute(name = "time", required = false)
                var time : String? = null,
                @field:Attribute(name = "desc", required = false)
                var desc : String? = null
            )

        }
    }
}
