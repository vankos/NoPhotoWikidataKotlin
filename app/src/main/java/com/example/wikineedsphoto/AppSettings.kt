package com.example.wikineedsphoto

import LocationHelper
import QueryService
import android.R.string
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.charset.Charset


class AppSettings (
    searchRadiusDegrees : Double,
    descriptionExclusions : String
    ) {

    var searchRadiusDegrees by mutableStateOf(searchRadiusDegrees)
    var descriptionExclusions by mutableStateOf(descriptionExclusions)
    val isNotBusy = true
    val buttonText = "Get GPX"
    val DefualtGpxFileNamePrefix = "NoPhotoLocations_.";

    fun getGpxCommand(context: Context) {

        val locationHelper = LocationHelper(context);
        locationHelper.getCurrentLocation { location ->
            val coordinates = Coordinates(location!!.latitude, location.longitude)
            val queryResult = QueryService.getWikiLocationsForLocation(coordinates, searchRadiusDegrees);
            val locations: List<Binding>? = queryResult?.results?.bindings
            val locationFilter = LocationFilter()
            if(locations == null){
                return@getCurrentLocation
            }

            val locationsWithoutImage = locationFilter.filterByDoesntHaveImage(locations)
            val exclusions = descriptionExclusions.split("\n")
            val filteredLocations =
                locationFilter.filterByHaveExclusionsInDescription(locationsWithoutImage, exclusions)
            if (!filteredLocations.any()) {
                return@getCurrentLocation
            }

            val gpxGenerator = GpxGenerator()
            val gpx = gpxGenerator.generateGpxFromWikidataResult(filteredLocations)
            val fileName = getFileName(coordinates)
            val file = File(context.getExternalFilesDir(null),fileName)
            file.writeText(gpx, Charset.defaultCharset())
            val uri: Uri = FileProvider.getUriForFile(context, "com.example.WikiNeedsPhoto.fileProvider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/octet-stream")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Open test.gpx"))
        }

    }

    private fun getFileName(coordinates: Coordinates): String {
        var fileNamePrefix = DefualtGpxFileNamePrefix
        val locationName: String? = runBlocking {
            QueryService.getLocationNameFromCoordinates(coordinates)
        }

        if (locationName != null) {
            fileNamePrefix = "${locationName}_"
        }

        val fileName = "$fileNamePrefix${java.time.LocalDateTime.now()}.gpx"
        return fileName
    }
}