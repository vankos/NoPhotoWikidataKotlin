package com.example.wikineedsphoto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.SerialName

@JsonIgnoreProperties(ignoreUnknown = true)
data class Binding(
    var q: WikimediaLink?,
    var location: Location?,
    var desc: Desc?,
    var qLabel: QLabel?,
    var image: Image?,
    var commonscat: Commonscat?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Commonscat(
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Desc(
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Head(
    var vars: List<String>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    var datatype: String?,
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikimediaLink(
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class QLabel(
    var type: String?,
    var value: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Results(
    var bindings: List<Binding>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WikidataQueryResult(
    var head: Head?,
    var results: Results?
)
