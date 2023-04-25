package com.example.venues.data.local.repo

import com.example.venues.data.local.VenueRepository
import com.example.venues.data.local.models.ClientData
import com.example.venues.data.local.models.Venue
import com.example.venues.data.local.models.VenueResponse
import com.example.venues.data.local.utils.LocalResponse
import com.example.venues.data.network.api.Endpoints
import javax.inject.Inject

class MainVenueRepository @Inject constructor(
    private val api: Endpoints,
    private val clientData: ClientData
) : VenueRepository {


    override suspend fun getVenue(params: MutableMap<String, String>) : LocalResponse<List<Venue>> {
        return try {
            val signedParams = signRequest(params)
            val response = api.venueSearch(signedParams)
            val result = response.body()
            if(response.isSuccessful && result != null){
                LocalResponse.Success(result.response.venues)
            }else{
                LocalResponse.Error(response.message() ?: "Error", response.code())
            }
        }catch (e: Exception){
            LocalResponse.Error(e.message ?: "Failure To make request.", -1)
        }
    }

    private fun signRequest(params: MutableMap<String, String>): HashMap<String, String> {
        params[CLIENT_SECRET] = clientData.clientSecret
        params[CLIENT_ID] = clientData.clientId
        return params as HashMap<String, String>
    }



    companion object{
        private const val TAG = "MainVenueRepository"
        private const val CLIENT_ID = "client_id"
        private const val CLIENT_SECRET = "client_secret"
    }
}