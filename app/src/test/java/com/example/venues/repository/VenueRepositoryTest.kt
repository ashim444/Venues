package com.example.venues.repository

import com.example.venues.data.local.VenueRepository
import com.example.venues.data.local.models.*
import com.example.venues.data.local.repo.MainVenueRepository
import com.example.venues.data.network.api.Endpoints
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

class VenueRepositoryTest {
    @Mock
    private lateinit var api: Endpoints


    private var clientData = ClientData("", "")

    private lateinit var venueRepository: VenueRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        venueRepository = MainVenueRepository(
            api,
            clientData
        )
    }

    @Test
    fun getVenueSuccessResponse_200() {
        runBlocking {
            val venueResponse = VenueResponse(
                meta = Meta(200, ""),
                response = Response(
                    emptyList()
                )
            )
            val response = Response.success(venueResponse)
            Mockito.`when`(
                api.venueSearch(
                    hashMapOf(
                        MainVenueRepository.CLIENT_SECRET to "",
                        MainVenueRepository.CLIENT_ID to ""
                    )
                )
            ).thenReturn(response)
            val actualResponse = venueRepository.getVenue(hashMapOf())

            Assert.assertTrue(actualResponse.data?.isEmpty() == true)
            Assert.assertTrue(actualResponse.message == null)
            Assert.assertTrue(actualResponse.errorCode == null)
        }
    }

    @Test
    fun getVenueSuccessResponse_not_200() {
        runBlocking {
            val venueResponse = VenueResponse(
                meta = Meta(300, "")
            )
            val response = Response.success(venueResponse)
            Mockito.`when`(
                api.venueSearch(
                    hashMapOf(
                        MainVenueRepository.CLIENT_SECRET to "",
                        MainVenueRepository.CLIENT_ID to ""
                    )
                )
            ).thenReturn(response)
            val actualResponse = venueRepository.getVenue(hashMapOf())

            Assert.assertTrue(actualResponse.data == null)
            Assert.assertTrue(actualResponse.message != null)
            Assert.assertTrue(actualResponse.errorCode == 300)
        }
    }

    @Test
    fun getVenueSuccessResponse_ok_VenueList() {
        runBlocking {
            val venue = getVenue()
            val venueResponse = VenueResponse(
                meta = Meta(200, ""),
                response = Response(
                    listOf(venue)
                )
            )
            val response = Response.success(venueResponse)
            Mockito.`when`(
                api.venueSearch(
                    hashMapOf(
                        MainVenueRepository.CLIENT_SECRET to "",
                        MainVenueRepository.CLIENT_ID to ""
                    )
                )
            ).thenReturn(response)
            val actualResponse = venueRepository.getVenue(hashMapOf())

            Assert.assertTrue(actualResponse.data !== null)
            Assert.assertTrue(actualResponse.data?.size == 1)
            Assert.assertTrue(actualResponse.data?.first() == venue)
        }
    }

    @Test
    fun getVenueSuccessResponse_ok_VenueBigList() {
        runBlocking {
            val list = listOf(getVenue(), getVenue(), getVenue())
            val venueResponse = VenueResponse(
                meta = Meta(200, ""),
                response = Response(
                    list
                )
            )
            val response = Response.success(venueResponse)
            Mockito.`when`(
                api.venueSearch(
                    hashMapOf(
                        MainVenueRepository.CLIENT_SECRET to "",
                        MainVenueRepository.CLIENT_ID to ""
                    )
                )
            ).thenReturn(response)
            val actualResponse = venueRepository.getVenue(hashMapOf())

            Assert.assertTrue(actualResponse.data !== null)
            Assert.assertTrue(actualResponse.data?.size == 3)
            Assert.assertTrue(actualResponse.data == list)
        }
    }

    private fun getVenue(): Venue = Venue("123", Location("", 1), "")
}