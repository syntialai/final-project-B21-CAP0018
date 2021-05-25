package com.bangkit.team18.qhope.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.view.View
import android.widget.SearchView
import com.bangkit.team18.core.utils.location.LocationManager
import com.bangkit.team18.core.utils.view.DataUtils.orFalse
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentHomeBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.home.adapter.HomeAdapter
import com.bangkit.team18.qhope.ui.home.adapter.HomeHospitalItemCallback
import com.bangkit.team18.qhope.ui.home.viewmodel.HomeViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate,
    HomeViewModel::class), HomeHospitalItemCallback {

  companion object {
    fun newInstance() = HomeFragment()
  }

  private val homeAdapter by lazy {
    HomeAdapter(this)
  }

  private val locationManager by lazy {
    LocationManager(mContext, object : LocationCallback() {

      override fun onLocationResult(result: LocationResult) {
        setLocation(result.lastLocation)
      }
    })
  }

  override fun setupViews() {
    setupRecyclerView()
    setupSearchView()
    getLocation()
  }

  override fun onPause() {
    super.onPause()
    locationManager.stopUpdateLocation()
  }

  override fun onResume() {
    super.onResume()
    if (locationManager.receivingLocationUpdates.orFalse().not()) {
      locationManager.startUpdateLocation()
    }
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        viewYourLocationListener -> getLocation()
      }
    }
  }

  override fun onClickListener(id: String) {
    // TODO: Go to hospital details
  }

  override fun onBookHospitalButtonClick(id: String) {
    // TODO: Go to book transaction
  }

  @SuppressLint("MissingPermission")
  override fun onPermissionGrantedChange(isGranted: Boolean) {
    if (isGranted) {
      locationManager.startUpdateLocation()
    } else {
      showErrorToast(defaultMessageId = R.string.failed_to_get_location_message)
    }
  }

  private fun getLocation() {
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  }

  private fun setLocation(location: Location) {
    // TODO: Change recommendation by location latitude and longitude
    val addresses = Geocoder(mContext, Locale.getDefault()).getFromLocation(location.latitude,
        location.longitude, 1)
    binding.textViewYourLocation.text = addresses[0].getAddressLine(0)
  }

  private fun setupRecyclerView() {
    binding.apply {
      with(recyclerViewRecommendedHospitals) {
        adapter = homeAdapter
        setHasFixedSize(false)
      }
      with(recyclerViewHospitalSearchResults) {
        adapter = homeAdapter
        setHasFixedSize(false)
      }
    }
  }

  private fun setupSearchView() {
    binding.searchViewHome.apply {
      setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
          // TODO: Do search
          showSearchResults(true)
          clearFocus()
          return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          launchJob {
            // TODO: Do search
            showSearchResults(true)
          }
          return false
        }
      })
      setOnCloseListener {
        showSearchResults(false)
        clearFocus()
        true
      }
    }
  }

  private fun showSearchResults(show: Boolean) {
    with(binding) {
      recyclerViewRecommendedHospitals.showOrRemove(show.not())
      recyclerViewHospitalSearchResults.showOrRemove(show)
    }
  }
}