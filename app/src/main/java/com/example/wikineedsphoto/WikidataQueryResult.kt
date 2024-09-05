package com.example.wikineedsphoto

data class Binding(
    @JsonProperty("q")
    var link: WikimediaLink,
    var location: Location,
    var desc: Desc?,
    var qLabel: QLabel,
    var image: Image?,
    var commonscat: Commonscat?
)

data class Commonscat(
    var type: String,
    var value: String
)

data class Desc(
    @JsonProperty("xml:lang")
    var xmllang: String,
    var type: String,
    var value: String
)

data class Head(
    var vars: List<String>
)

data class Image(
    var type: String,
    var value: String
)

data class Location(
    var datatype: String,
    var type: String,
    var value: String
)

data class WikimediaLink(
    var type: String,
    var value: String
)

data class QLabel(
    @JsonProperty("xml:lang")
    var xmllang: String,
    var type: String,
    var value: String
)

data class Results(
    var bindings: List<Binding>
)

data class WikidataQueryResult(
    var head: Head,
    var results: Results
)
