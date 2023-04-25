package com.example.venues.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.venues.R
import com.example.venues.base.hasLocationPermission
import com.example.venues.databinding.ActivityMainBinding
import com.example.venues.ui.adapter.VenuesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var venueAdapter: VenuesAdapter? = null
    private var locationManager: LocationManager? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            Log.d(TAG, ": ")
            if (it.values.firstOrNull { it.not() } == null) {
                checkLocationProvider()
            } else {
                showPermissionView(true)
            }
        }

    private fun checkLocationProvider() {
        Log.d(TAG, "checkLocationProvider: ")
        locationManager?.let {
            val isGpsEnabled = it.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                showPermissionView(true)
            } else {
                showPermissionView(false)
                requestLocationUpdate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkUserPermission()
        setUpListener()
        setUpObserver()
        setSearchListener()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        Log.d(TAG, "requestLocationUpdate: ")
        locationManager?.let {
            it.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300L, 200.0F, this)
            it.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300L, 200.0F, this)
        }
    }
    private fun setSearchListener() {
        Log.d(TAG, "setSearchListener: ")
        binding.mainSearch.setOnCloseListener {
            viewModel.clearVenueList()
            true
        }
        binding.mainSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    Log.d(TAG, "onQueryTextSubmit: ")
                    viewModel.setQueryString(query)
                }
                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    viewModel.clearVenueList()
                }
                return false
            }
        })
    }

    private fun checkUserPermission() {
        Log.d(TAG, "checkUserPermission: ")
        if (!applicationContext.hasLocationPermission(permissions)) {
            requestPermissionLauncher.launch(permissions.distinct().toTypedArray())
            return
        }
        checkLocationProvider()
    }

    private fun showPermissionView(b: Boolean) {
        Log.d(TAG, "showPermissionView: $b")
        val permissionViewVisibility = if (b) View.VISIBLE else View.GONE
        val nonPermissionViewVisibility = if (b) View.GONE else View.VISIBLE
        binding.mainSearch.visibility = nonPermissionViewVisibility
        binding.mainFilter.visibility = nonPermissionViewVisibility
        binding.emptyVenueDetails.visibility = nonPermissionViewVisibility
        binding.permissionRequestText.visibility = permissionViewVisibility
        binding.retryPermissionButton.visibility = permissionViewVisibility
        binding.openSettingsButton.visibility = permissionViewVisibility
    }


    private fun setUpListener() {
        binding.run {
            mainFilter.setOnClickListener {
                viewModel.sortData()
            }
            retryPermissionButton.setOnClickListener {
                checkUserPermission()
            }
            openSettingsButton.setOnClickListener {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.venueEvent.collect { event ->
                    Log.d(TAG, "setUpObserver: $event")
                    when (event) {
                        is MainViewModel.VenueEvent.Failure -> {
                            binding.errorMessage.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.venueList.visibility = View.GONE
                        }
                        is MainViewModel.VenueEvent.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.emptyVenueDetails.visibility = View.GONE
                            binding.errorMessage.visibility = View.GONE
                        }
                        is MainViewModel.VenueEvent.Success -> {
                            Log.d(TAG, "setUpObserver: ${event.data}")
                            binding.progressBar.visibility = View.GONE
                            binding.emptyVenueDetails.visibility = View.GONE
                            binding.venueList.visibility = View.VISIBLE
                            if (venueAdapter == null) {
                                venueAdapter =
                                    VenuesAdapter(this@MainActivity, event.data.toMutableList())
                                binding.venueList.adapter = venueAdapter
                            } else {
                                venueAdapter?.run {
                                    clear()
                                    if (event.data.isNotEmpty()) {
                                        addAll(event.data.toMutableList())
                                    } else {
                                        binding.emptyVenueDetails.visibility = View.VISIBLE
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onLocationChanged(it: Location) {
        Log.d(TAG, "requestLocationUpdate: NETWORK ${it.latitude},${it.longitude}")
        viewModel.setLatLong("${it.latitude},${it.longitude}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        locationManager?.removeUpdates(this)
        locationManager?.removeUpdates(this)
        locationManager = null
        _binding = null
    }

    companion object {
        private const val TAG = "MainActivity"
        val permissions = arrayOf(
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        )
    }
}