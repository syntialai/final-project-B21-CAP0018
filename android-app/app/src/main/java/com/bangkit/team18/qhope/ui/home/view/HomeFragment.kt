package com.bangkit.team18.qhope.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
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
import com.bangkit.team18.qhope.utils.Router
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate,
    HomeViewModel::class), HomeHospitalItemCallback {

  companion object {
    fun newInstance() = HomeFragment()
  }

  private val storage: FirebaseStorage by inject()

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

  override fun setupObserver() {
    super.setupObserver()

    viewModel.nearbyHospitals.observe(viewLifecycleOwner, { nearbyHospitals ->
      showSearchResults(false)
      homeAdapter.submitList(nearbyHospitals)
    })
    viewModel.searchHospitalResults.observe(viewLifecycleOwner, { searchResults ->
      showSearchResults(true)
      homeAdapter.submitList(searchResults)
    })
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
    Router.goToHospitalDetails(mContext, id)
  }

  override fun getStorageRef(imagePath: String): StorageReference {
    return storage.getReference(imagePath)
  }

  @SuppressLint("MissingPermission")
  override fun onPermissionGrantedChange(isGranted: Boolean) {
    if (isGranted) {
      locationManager.startUpdateLocation()
    } else {
      showErrorToast(defaultMessageId = R.string.failed_to_get_location_message)
    }
  }

  override fun showLoadingState(isLoading: Boolean) {
    binding.spinKitLoadHome.showOrRemove(isLoading)
  }

  private fun getLocation() {
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  }

  private fun setLocation(location: Location) {
    viewModel.fetchNearbyHospitals(location.latitude, location.longitude)
    // TODO: Uncomment this when use real device
//    val addresses = Geocoder(mContext, Locale.getDefault()).getFromLocation(location.latitude,
//        location.longitude, 1)
//    binding.textViewYourLocation.text = addresses[0].getAddressLine(0)
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
          viewModel.searchHospital(query.orEmpty())
          clearFocus()
          return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let {
            launchJob {
              viewModel.searchHospital(it)
            }
          } ?: run {
            showSearchResults(false)
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