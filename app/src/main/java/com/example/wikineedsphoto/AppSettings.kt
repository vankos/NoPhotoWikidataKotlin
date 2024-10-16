package com.example.wikineedsphoto

import LocationHelper
import QueryService
import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppSettings (
    searchRadiusDegrees : Double,
    descriptionExclusions : String
    ) {

    var searchRadiusDegrees by mutableStateOf(searchRadiusDegrees)
    var descriptionExclusions by mutableStateOf(descriptionExclusions)
    val isNotBusy = true
    val buttonText = "Get GPX"

    fun getGpxCommand(context: Context) {

        val locationHelper = LocationHelper(context);
        locationHelper.getCurrentLocation { location ->
            val coordinates = Coordinates(location!!.latitude, location.longitude)
            val queryResult = QueryService.getWikiLocationsForLocation(coordinates, searchRadiusDegrees);
            val locations: List<Binding>? = queryResult?.results?.bindings
            val exclusions = descriptionExclusions.split("\n")
            val locationFilter = LocationFilter()
            if(locations == null){
                return@getCurrentLocation
            }

            val locationsWithoutImage = locationFilter.filterByDoesntHaveImage(locations)

        }

    }
}