package com.example.venues.data.local

import com.example.venues.data.local.models.Venue
import com.example.venues.data.local.utils.LocalResponse

interface VenueRepository {

    suspend fun getVenue(params: MutableMap<String, String>): LocalResponse<List<Venue>>

}