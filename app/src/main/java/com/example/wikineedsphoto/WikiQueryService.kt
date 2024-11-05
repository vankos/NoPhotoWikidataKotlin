import com.example.wikineedsphoto.Coordinates
import com.example.wikineedsphoto.WikidataQueryResult
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

object QueryService {
    private const val MAX_LATITUDE = 90.0
    private const val MIN_LATITUDE = -90.0
    private const val MAX_LONGITUDE = 180.0
    private const val MIN_LONGITUDE = -180.0

    fun getWikiLocationsForLocation(deviceLocation: com.example.wikineedsphoto.Coordinates, searchRadiusDegrees: Double): WikidataQueryResult? {
        val southWestCorner = Coordinates(
            latitude = addLatitude(deviceLocation.latitude, -searchRadiusDegrees),
            longitude = addLongitude(deviceLocation.longitude, -searchRadiusDegrees)
        )

        val northEastCorner = Coordinates(
            latitude = addLatitude(deviceLocation.latitude, searchRadiusDegrees),
            longitude = addLongitude(deviceLocation.longitude, searchRadiusDegrees)
        )

        val query = """
            SELECT ?q ?qLabel ?location ?image ?desc (GROUP_CONCAT(DISTINCT ?instanceOfLabel; separator=", ") AS ?instanceOfLabels) WHERE {
              SERVICE wikibase:box { 
                ?q wdt:P625 ?location . 
                bd:serviceParam wikibase:cornerSouthWest "Point(${southWestCorner.longitudeString} ${southWestCorner.latitudeString})"^^geo:wktLiteral . 
                bd:serviceParam wikibase:cornerNorthEast "Point(${northEastCorner.longitudeString} ${northEastCorner.latitudeString})"^^geo:wktLiteral 
              } 
              OPTIONAL { ?q wdt:P18 ?image } 
              OPTIONAL { ?q wdt:P31 ?instanceOf . 
                         ?instanceOf rdfs:label ?instanceOfLabel . 
                         FILTER (LANG(?instanceOfLabel) = "en") 
              }
              SERVICE wikibase:label { 
                bd:serviceParam wikibase:language "en,en,de,fr,es,it,nl,ru" . 
                ?q schema:description ?desc . 
                ?q rdfs:label ?qLabel 
              } 
            } 
            GROUP BY ?q ?qLabel ?location ?image ?desc
            LIMIT 3000
        """.trimIndent()

        val url = "https://query.wikidata.org/bigdata/namespace/wdq/sparql?query=${URLEncoder.encode(query)}&format=json"
        return try {
            var response = getWikiLocationsForLocation(url)
            if(response != null) {
                var result = jacksonObjectMapper().readValue<WikidataQueryResult>(response)
                return result
            }

            return null
        } catch (ex: Exception) {
            println("Error: ${ex.message}")
            null
        }
    }

    private fun getWikiLocationsForLocation(url: String): String? {
        return try {
                URL(url).readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocationNameFromCoordinates(coordinates: Coordinates): String? {
        val queryUrl = "https://nominatim.openstreetmap.org/reverse?lat=${coordinates.latitudeString}&lon=${coordinates.longitudeString}&format=json"
        return try {
            val jsonResponse = withContext(Dispatchers.IO) {
                 URL(queryUrl).readText()
            }

            val locationInfo: LocationInfoRequestResult = jacksonObjectMapper().readValue(jsonResponse)
            getLocationNameFromLocationInfo(locationInfo)
        } catch (ex: Exception) {
            null
        }
    }

    private fun getLocationNameFromLocationInfo(locationInfo: LocationInfoRequestResult?): String? {
        return locationInfo?.address?.suburb
            ?: locationInfo?.address?.city
            ?: locationInfo?.address?.state
            ?: locationInfo?.address?.country
            ?: locationInfo?.display_name
    }

    private fun addLatitude(firstDegree: Double, secondDegree: Double): Double {
        var sum = firstDegree + secondDegree
        if (sum > MAX_LATITUDE) {
            return sum - MAX_LATITUDE
        }
        if (sum < MIN_LATITUDE) {
            return MIN_LATITUDE - sum
        }
        return sum
    }

    private fun addLongitude(firstDegree: Double, secondDegree: Double): Double {
        var sum = firstDegree + secondDegree
        if (sum > MAX_LONGITUDE) {
            return sum - MAX_LONGITUDE
        }
        if (sum < MIN_LONGITUDE) {
            return MIN_LONGITUDE - sum
        }
        return sum
    }

    private fun URL.encodeURL(): String = this.toString().replace(" ", "%20")
}

data class Location(
    val datatype: String,
    val type: String,
    val value: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
    val tourism: String? = null,
    val road: String? = null,
    val suburb: String? = null,
    val city: String? = null,
    val state: String? = null,

    @JsonProperty("ISO3166-2-lvl4")
    val iso31662lvl4: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    val country_code: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationInfoRequestResult(
    val place_id: Int?,
    val licence: String?,
    val osm_type: String?,
    val osm_id: Long?,
    val lat: String?,
    val lon: String?,
    @JsonProperty("class")
    val className: String?,
    val type: String?,
    val place_rank: Int?,
    val importance: Double?,
    val addresstype: String? = null,
    val name: String? = null,
    val display_name: String? = null,
    val address: Address? = null,
    val boundingbox: List<String>? = null
)

