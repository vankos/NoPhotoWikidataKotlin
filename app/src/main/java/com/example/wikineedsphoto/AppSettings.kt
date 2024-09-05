package com.example.wikineedsphoto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppSettings (
    searchRadiusDegrees : Float,
    descriptionExclusions : String
    ) {
    var searchRadiusDegrees by mutableStateOf(searchRadiusDegrees)
    var descriptionExclusions by mutableStateOf(descriptionExclusions)
    val isNotBusy = true
    val buttonText = "Get GPX"

    fun getGpxCommand() {
        // Logic to get GPX
    }
}