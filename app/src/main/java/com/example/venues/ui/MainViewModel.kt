package com.example.venues.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.venues.data.local.VenueRepository
import com.example.venues.data.local.models.Venue
import com.example.venues.data.local.models.VenueFilter
import com.example.venues.data.local.utils.LocalResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: VenueRepository
) : ViewModel() {

    sealed class VenueEvent {
        class Success(val data: List<Venue>) : VenueEvent()
        class Failure(val errorText: String) : VenueEvent()
        object Loading : VenueEvent()
        object Empty : VenueEvent()
    }

    private var venueList: List<Venue>? = null
    private val _venueEvent = MutableStateFlow<VenueEvent>(VenueEvent.Empty)
    val venueEvent: StateFlow<VenueEvent> = _venueEvent
    private var sortAscending = true
    private val _venueFilter = MutableStateFlow(VenueFilter())

    private fun findVenues() {
        if (_venueFilter.value.query.isEmpty() || _venueFilter.value.latLong.isEmpty()) {
            _venueEvent.value = VenueEvent.Failure("Failure to load venues.")
            return
        }
        viewModelScope.launch {
            _venueEvent.value = VenueEvent.Loading
            val venueResponse = repository.getVenue(
                mutableMapOf(
                    QUERY to _venueFilter.value.query,
                    LAT_LONG to _venueFilter.value.latLong,
                    RADIUS to _venueFilter.value.radius,
                    VERSION_DATE to "20220424"
                )
            )
            when (venueResponse) {
                is LocalResponse.Error -> {
                    _venueEvent.value = VenueEvent.Failure(venueResponse.message!!)
                }
                is LocalResponse.Success -> {
                    val data = venueResponse.data?.sortedBy { it.location.distance }!!
                    if (!sortAscending) data.reversed()
                    venueList = data
                    _venueEvent.value = VenueEvent.Success(data)
                }
            }
        }
    }

    fun setQueryString(query: String) {
        _venueFilter.value.query = query
        findVenues()
    }

    fun setLatLong(latLong: String) {
        _venueFilter.value.latLong = latLong
    }

    fun sortData() {
        sortAscending = !sortAscending
        venueList?.let {
            if (it.isNotEmpty()) {
                venueList = it.reversed()
                _venueEvent.value = VenueEvent.Success(it.reversed())
            }
        }
    }

    fun clearVenueList() {
        venueList?.let {
            if (it.isNotEmpty()) {
                venueList = emptyList()
                _venueEvent.value = VenueEvent.Success(emptyList())
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val VERSION_DATE = "v"
        private const val QUERY = "query"
        private const val LAT_LONG = "ll"
        private const val RADIUS = "radius"
    }
}