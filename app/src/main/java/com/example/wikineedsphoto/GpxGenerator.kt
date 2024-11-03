package com.example.wikineedsphoto

import android.util.Xml
import java.io.StringWriter

class GpxGenerator {
    private val latitudeRegexp = " (.*)\\)"
    private val longitudeRegexp = "\\((.*) "

    fun generateGpxFromWikidataResult(locations : List<Binding>) : String
    {
        val xmlSerializer = Xml.newSerializer()
        val writer = StringWriter()
        xmlSerializer.setOutput(writer)
        xmlSerializer.startDocument("UTF-8", false)
        xmlSerializer.startTag("http://www.topografix.com/GPX/1/1", "gpx")
        xmlSerializer.attribute("", "version", "1.1")
        xmlSerializer.attribute("","creator", "WikiShootMe-to-GPX")

        for (location in locations)
        {
            xmlSerializer.startTag("", "wpt")
            val coordinates : Coordinates = getCoordinates(location)
            xmlSerializer.attribute("","lat", coordinates.latitudeString)
            xmlSerializer.attribute("","lon", coordinates.longitudeString)
            xmlSerializer.startTag("", "name")
            xmlSerializer.text(location.qLabel?.value)
            xmlSerializer.endTag("", "name")
            xmlSerializer.startTag("", "desc")
            xmlSerializer.text(getDescription(location))
            xmlSerializer.endTag("", "desc")
            xmlSerializer.endTag("", "wpt")
        }

        xmlSerializer.endTag("http://www.topografix.com/GPX/1/1", "gpx")
        xmlSerializer.endDocument()

        return writer.toString()
    }

    private fun getCoordinates(location: Binding): Coordinates {
        val coordinatesString = location.location?.value ?: return Coordinates()
        val latitude = Regex(latitudeRegexp).find(coordinatesString)?.groupValues?.get(1) ?: ""
        val longitude = Regex(longitudeRegexp).find(coordinatesString)?.groupValues?.get(1) ?: ""
        return Coordinates(latitude.toDouble(), longitude.toDouble())
    }

    private fun getDescription(location: Binding): String {
        val url = location.q?.value
        val description = location.desc?.value ?: ""
        val instanceOf = location.instanceOfLabels?.value ?: ""
        val gpxDescription = "$instanceOf<br>$description<br>$url"
        return gpxDescription
    }


}