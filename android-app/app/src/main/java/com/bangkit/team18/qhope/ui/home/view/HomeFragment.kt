package com.bangkit.team18.qhope.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import androidx.appcompat.widget.SearchView
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentHomeBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.home.adapter.HomeAdapter
import com.bangkit.team18.qhope.ui.home.adapter.HospitalItemCallback
import com.bangkit.team18.qhope.utils.view.ViewUtils.showOrRemove
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    LocationListener, HospitalItemCallback {

  private val homeAdapter by lazy {
    HomeAdapter(this)
  }

  private lateinit var locationManager: LocationManager

  override fun setupViews() {
    locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    setupRecyclerView()
    setupSearchView()
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        viewYourLocationListener -> checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      }
    }
  }

  override fun onClickListener(id: String) {
    // TODO: Go to hospital details
  }

  override fun onBookHospitalButtonClick(id: String) {
    // TODO: Go to book transaction
  }

  override fun onLocationChanged(location: Location) {
    // TODO: Change recommendation by location latitude and longitude
    launchJob {
      val addresses = Geocoder(mContext, Locale.getDefault()).getFromLocation(location.latitude,
          location.longitude, 1)
      binding.textViewYourLocation.text = addresses[0].getAddressLine(0)
    }
  }

  @SuppressLint("MissingPermission")
  override fun onPermissionGrantedChange(isGranted: Boolean) {
    if (isGranted) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    } else {
      showErrorToast(R.string.failed_to_get_location_message)
    }
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