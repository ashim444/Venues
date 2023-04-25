package com.example.venues.data.local.models

data class VenueFilter(
    var query : String = "",
    var latLong : String = "",
    var radius : String = "5000",//default value incase if unavailable
    var versionDate: String = "20230424"//default value incase if unavailable
)
