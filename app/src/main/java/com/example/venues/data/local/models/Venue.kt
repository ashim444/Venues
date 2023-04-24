package com.example.venues.data.local.models

import com.example.venues.data.local.models.Location

data class Venue(
    val id: String,
    val location: Location,
    val name: String
)