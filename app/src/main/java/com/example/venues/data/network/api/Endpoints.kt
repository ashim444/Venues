package com.example.venues.data.network.api

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface Endpoints {

    @GET(ServiceType.VENUE_SEARCH)
    suspend fun venueSearch(@QueryMap params: Map<String, String>)

}