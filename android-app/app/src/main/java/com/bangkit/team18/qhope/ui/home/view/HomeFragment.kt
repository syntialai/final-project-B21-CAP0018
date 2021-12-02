package com.bangkit.team18.qhope.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.utils.location.LocationManager
import com.bangkit.team18.core.utils.view.DataUtils.orFalse
import com.bangkit.team18.core.utils.view.ViewUtils.hide
import com.bangkit.team18.core.utils.view.ViewUtils.remove
import com.bangkit.team18.core.utils.view.ViewUtils.show
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
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
  FragmentHomeBinding::inflate,
  HomeViewModel::class
), HomeHospitalItemCallback {

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
    showLoadingState(true)
    showLocationLoadingState(true)
    getLocation()
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.fetchHospitals()
    viewModel.fetchUserData()
    viewModel.hospitals.observe(viewLifecycleOwner, { hospitals ->
      homeAdapter.submitList(hospitals)
    })
    viewModel.userData.observe(viewLifecycleOwner, { userData ->
      setUserName(userData.name)
      userData.verificationStatus?.let {
        setVerificationStatus(it)
      }
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
    findNavController().navigate(
      HomeFragmentDirections.actionHomeFragmentToHospitalDetailFragment(
        id
      )
    )
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
    binding.apply {
      spinKitLoadHome.showOrRemove(isLoading)
      layoutVerificationStatus.root.showOrRemove(isLoading.not())
      textViewOurHospitalsLabel.showOrRemove(isLoading.not())
      recyclerViewHospitals.showOrRemove(isLoading.not())
    }
  }

  private fun getLocation() {
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  }

  private fun getVerificationButtonText(status: VerificationStatus) = getString(when (status) {
    VerificationStatus.ACCEPTED -> R.string.verification_status_accepted_button_label
    VerificationStatus.VERIFIED -> R.string.verification_status_verified_button_label
    VerificationStatus.REJECTED -> R.string.verification_status_not_verified_button_label
    else -> R.string.verification_status_not_upload_button_label
  })

  private fun getVerificationDrawable(status: VerificationStatus) = when (status) {
    VerificationStatus.ACCEPTED,
    VerificationStatus.VERIFIED -> R.drawable.drawable_verification_status_verified
    VerificationStatus.REJECTED -> R.drawable.drawable_verification_status_not_verified
    else -> R.drawable.drawable_verification_status_not_upload
  }

  private fun getVerificationDescription(status: VerificationStatus) = getString(when (status) {
    VerificationStatus.ACCEPTED -> R.string.verification_status_accepted_description
    VerificationStatus.VERIFIED -> R.string.verification_status_verified_description
    VerificationStatus.REJECTED -> R.string.verification_status_not_verified_description
    else -> R.string.verification_status_not_upload_description
  })

  private fun getVerificationTitle(status: VerificationStatus) = getString(when (status) {
    VerificationStatus.NOT_UPLOAD -> R.string.verification_status_not_upload_title
    VerificationStatus.UPLOADED -> R.string.verification_status_not_upload_title
    else -> R.string.verification_status_uploaded_title
  })

  private fun setLocation(location: Location) {
    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
      val addresses = Geocoder(mContext, Locale.getDefault()).getFromLocation(
        location.latitude,
        location.longitude, 1
      )
      binding.textViewYourLocation.text = addresses[0].getAddressLine(0)
      showLocationLoadingState(false)
    }
  }

  private fun setUserName(name: String) {
    binding.apply {
      groupHello.show()
      textViewNameLabel.text = name
    }
  }

  private fun setVerificationStatus(status: VerificationStatus) {
    binding.layoutVerificationStatus.apply {
      buttonVerificationStatus.text = getVerificationButtonText(status)
      imageViewVerificationStatus.setImageResource(getVerificationDrawable(status))
      textViewVerificationStatusDescription.text = getVerificationDescription(status)
      textViewVerificationStatusTitle.text = getVerificationTitle(status)

      when (status) {
        VerificationStatus.ACCEPTED -> {
          chipVerificationStatusNotVerified.remove()
          chipVerificationStatusVerified.show()
          chipVerificationStatusVerified.text = mContext.getString(R.string.accepted_label)
          buttonVerificationStatus.setOnClickListener {
            Router.goToIdentityConfirmation(mContext)
          }
        }
        VerificationStatus.VERIFIED -> {
          chipVerificationStatusNotVerified.remove()
          chipVerificationStatusVerified.show()
          chipVerificationStatusVerified.text = mContext.getString(R.string.verified_label)
          buttonVerificationStatus.setOnClickListener {
            // TODO
          }
        }
        VerificationStatus.REJECTED -> {
          chipVerificationStatusNotVerified.show()
          chipVerificationStatusVerified.hide()
          buttonVerificationStatus.setOnClickListener {
            Router.goToVerificationResult(mContext)
          }
        }
        else -> {
          chipVerificationStatusNotVerified.remove()
          chipVerificationStatusVerified.remove()
          buttonVerificationStatus.setOnClickListener {
            Router.goToIdVerification(mContext)
          }
        }
      }
    }
  }

  private fun setupRecyclerView() {
    binding.recyclerViewHospitals.apply {
      adapter = homeAdapter
      setHasFixedSize(false)
    }
  }

  private fun showLocationLoadingState(isLoading: Boolean) {
    binding.apply {
      spinKitLoadYourLocation.showOrRemove(isLoading)
      if (isLoading) {
        textViewYourLocation.hide()
      } else {
        textViewYourLocation.show()
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
              if (it.isEmpty()) {
                showSearchResults(false)
              } else {
                viewModel.searchHospital(it)
              }
            }
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
      recyclerViewHospitals.showOrRemove(show.not())
      recyclerViewHospitalSearchResults.showOrRemove(show)
    }
  }
}