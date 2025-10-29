package com.example.wikineedsphoto

import LocationHelper
import QueryService
import android.R.string
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import androidx.annotation.BoolRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset


class AppSettings (
    searchRadiusKilometers : Double,
    descriptionExclusions : String
    ) : ViewModel()  {

    var searchRadiusKilometers by mutableStateOf(searchRadiusKilometers)
    var descriptionExclusions by mutableStateOf(descriptionExclusions)
    val buttonText = "Get GPX"
    val DefualtGpxFileNamePrefix = "NoPhotoLocations_.";

    fun  getGpxCommand(context: Context, coordinates: Coordinates, onFinished: (Boolean) -> Unit ) {
        viewModelScope.launch{
            withContext(Dispatchers.IO) {getGpxCommandInternal(context, coordinates, onFinished)}
        }
    }

    private suspend fun getGpxCommandInternal(context: Context,coordinates : Coordinates, onFinished: (Boolean) -> Unit ){
        val exclusions = descriptionExclusions.split("\n")
        val queryResult = QueryService.getWikiLocationsForLocation(coordinates, searchRadiusKilometers, exclusions);
        val locationsWithoutImage: List<Binding>? = queryResult?.results?.bindings
        val locationFilter = LocationFilter()
        if(locationsWithoutImage == null){
            onFinished(true)
            return
        }

        val filteredLocations =
            locationFilter.filterByHaveExclusionsInDescription(locationsWithoutImage, exclusions)
        if (!filteredLocations.any()) {
            onFinished(true)
            return
        }

        val gpxGenerator = GpxGenerator()
        val gpx = gpxGenerator.generateGpxFromWikidataResult(filteredLocations)
        val fileName = getFileName(coordinates, "Need photo")
        val file = File(context.getExternalFilesDir(null),fileName)
        file.writeText(gpx, Charset.defaultCharset())
        val uri: Uri = FileProvider.getUriForFile(context, "com.example.WikiNeedsPhoto.fileProvider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/octet-stream")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            onFinished(true)
        }

        context.startActivity(Intent.createChooser(intent, "Open test.gpx"))
    }

    private fun getFileName(coordinates: Coordinates, prefix: String): String {
        val locationName: String? = runBlocking {
            QueryService.getLocationNameFromCoordinates(coordinates)
        }
        val now = java.time.LocalDateTime.now().toString().replace(":", "-")
        return "$prefix $locationName $now.gpx"
    }
}