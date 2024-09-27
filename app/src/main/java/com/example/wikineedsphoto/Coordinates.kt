package com.example.wikineedsphoto

import java.util.Locale

class Coordinates {

    companion object {
        private const val COORDINATES_STRING_FORMAT = "%.6f"
    }

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor()

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    val latitudeString: String
        get() = coordinateToString(latitude)

    val longitudeString: String
        get() = coordinateToString(longitude)

    override fun toString(): String {
        val latString = coordinateToString(latitude)
        val lonString = coordinateToString(longitude)
        return "$latString,$lonString"
    }

    private fun coordinateToString(coordinate: Double): String {
        return String.format(Locale.US, COORDINATES_STRING_FORMAT, coordinate)
    }
}