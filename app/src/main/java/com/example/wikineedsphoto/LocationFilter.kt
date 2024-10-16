package com.example.wikineedsphoto

class LocationFilter {
    fun filterByHaveExclusionsInDescription(
        locations: Iterable<Binding>,
        excludedDescriptionWords: Array<String>
    ): List<Binding> {
        val filteredLocations = mutableListOf<Binding>()
        for (location in locations) {
            if (location.desc?.value == null) {
                filteredLocations.add(location)
                continue
            }

            if (locationShouldBeExcluded(location, excludedDescriptionWords)) {
                continue
            }

            filteredLocations.add(location)
        }
        return filteredLocations
    }

    fun filterByDoesntHaveImage(locations: List<Binding>): List<Binding> {
        val filteredLocations = mutableListOf<Binding>()
        for (location in locations) {
            if (location.image != null) {
                continue
            }
            filteredLocations.add(location)
        }
        return filteredLocations
    }

    private fun locationShouldBeExcluded(location: Binding, excludedDescriptionWords: Array<String>): Boolean {
        for (exclusion in excludedDescriptionWords) {
            if (location.desc?.value?.contains(exclusion, ignoreCase = true) == true) {
                return true
            }
        }
        return false
    }

}