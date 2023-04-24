package com.example.venues.data.network.api

import com.example.venues.data.local.models.VenueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface Endpoints {

    @GET(ServiceType.VENUE_SEARCH)
    suspend fun venueSearch(@QueryMap params: Map<String, String>): Response<VenueResponse>

}