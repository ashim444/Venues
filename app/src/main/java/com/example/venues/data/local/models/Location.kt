package com.example.venues.data.local.models

data class Location(
    val address: String,
    val cc: String,
    val city: String,
    val country: String,
    val crossStreet: String,
    val distance: Int,
    val lat: Double,
    val lng: Double,
    val postalCode: String,
    val state: String
)