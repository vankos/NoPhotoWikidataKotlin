import androidx.compose.ui.text.intl.Locale
import com.example.wikineedsphoto.Coordinates
import com.example.wikineedsphoto.WikidataQueryResult
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

object QueryService {

    private val systemLanguage = java.util.Locale.getDefault().language

    fun getWikiLocationsForLocation(
        deviceLocation: com.example.wikineedsphoto.Coordinates,
        searchRadiusKilometers: Double,
        excludedCategories: List<String>): WikidataQueryResult? {
        val query = """
            SELECT ?q ?qLabel ?location ?image ?desc (GROUP_CONCAT(DISTINCT ?instanceOfLabel; separator=", ") AS ?instanceOfLabels) WHERE {
              {
                SELECT ?q ?location ?image ?desc ?autoLabel (MIN(?anyLabel) AS ?fallbackLabel) WHERE {
                  SERVICE wikibase:around { 
                    ?q wdt:P625 ?location . 
                    bd:serviceParam wikibase:center "Point(${deviceLocation.longitudeString} ${deviceLocation.latitudeString})"^^geo:wktLiteral . 
                    bd:serviceParam wikibase:radius "$searchRadiusKilometers" 
                  } 
                  OPTIONAL { ?q wdt:P18 ?image } 
                  OPTIONAL { ?q wdt:P576 ?discontinuedDate }
                  OPTIONAL { ?q wdt:P5816 ?status }
                  FILTER(!BOUND(?discontinuedDate))
                  FILTER(!BOUND(?status) || ?status != wdt:Q56556915)
                  FILTER (!BOUND(?image))
                  
                  SERVICE wikibase:label { 
                    bd:serviceParam wikibase:language "${systemLanguage},en,[AUTO_LANGUAGE]" . 
                    ?q schema:description ?desc . 
                    ?q rdfs:label ?autoLabel .
                  }
                  
                  OPTIONAL { ?q rdfs:label ?anyLabel . }
                }
                GROUP BY ?q ?location ?image ?desc ?autoLabel
              }
              
              OPTIONAL { ?q wdt:P31 ?instanceOf . 
                                     ?instanceOf rdfs:label ?instanceOfLabel . 
                                     FILTER (LANG(?instanceOfLabel) = "en") 
              }
              
              BIND(IF(STRSTARTS(?autoLabel, "Q") && BOUND(?fallbackLabel), ?fallbackLabel, ?autoLabel) AS ?qLabel)
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
        val queryUrl = "https://nominatim.openstreetmap.org/reverse?lat=${coordinates.latitudeString}&lon=${coordinates.longitudeString}&format=json&accept-language=${Locale.current}"
        return try {
            val jsonResponse = withContext(Dispatchers.IO) {
                val connection = URL(queryUrl).openConnection() as java.net.HttpURLConnection
                connection.setRequestProperty("User-Agent", "GoogleAttractionsGpx/1.0")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.inputStream.bufferedReader().use { it.readText() }
            }

            val jsonObject = JSONObject(jsonResponse)
            getLocationNameFromLocationInfo(jsonObject)
        } catch (ex: Exception) {
            null
        }
    }

    private fun getLocationNameFromLocationInfo(jsonObject: JSONObject?): String? {
        val address = jsonObject?.optJSONObject("address")
        val district = address?.optString("city_district", "")?: ""
        val town = address?.optString("town", "")?: ""
        val city = address?.optString("city", "")?: ""
        val province = address?.optString("province", "")?: ""
        val state = address?.optString("state", "")?: ""
        val region = address?.optString("region", "")?: ""
        val country = address?.optString("country", "")?: ""
        val displayName = address?.optString("display_name", "")?: ""
        val final = when {

            district.isNotBlank() -> district
            town.isNotBlank() -> town
            city.isNotBlank() -> city
            province.isNotBlank() -> province
            state.isNotBlank() -> state
            region.isNotBlank() -> region
            country.isNotBlank() -> country
            displayName.isNotBlank() -> displayName
            else -> ""
        }

        return final
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
    val city_district: String? = null,

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

