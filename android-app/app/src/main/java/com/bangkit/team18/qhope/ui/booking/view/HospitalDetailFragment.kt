package com.bangkit.team18.qhope.ui.booking.view

import android.view.View
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentHospitalDetailBinding
import com.bangkit.team18.qhope.model.booking.HospitalDetail
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.utils.view.DataUtils.orZero
import com.bangkit.team18.qhope.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.utils.view.ViewUtils.showOrRemove
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HospitalDetailFragment :
    BaseFragment<FragmentHospitalDetailBinding>(FragmentHospitalDetailBinding::inflate),
    OnMapReadyCallback {

  companion object {
    private const val DEFAULT_ZOOM = 15f
  }

  private var googleMap: GoogleMap? = null

  override fun setupViews() {
    setupGoogleMaps()
  }

  override fun onMapReady(map: GoogleMap) {
    googleMap = map
    // TODO: Update location and get data from viewmodel
//    updateLocation()
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        buttonHospitalDetailBack -> { /* TODO: Call onBackPressed */ }
        buttonHospitalDetailBook -> processBook()
      }
    }
  }

  private fun processBook() {
    // TODO: Get data and go to booking confirmation
    findNavController().navigate(
        HospitalDetailFragmentDirections.actionHospitalDetailFragmentToBookingConfirmationFragment())
  }

  private fun setupGoogleMaps() {
    val mapFragment = parentFragmentManager.findFragmentById(
        R.id.fragment_hospital_detail_location_maps) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  private fun setHospitalData(hospital: HospitalDetail) {
    binding.apply {
      buttonHospitalDetailBook.isEnabled = hospital.availableRoomCount?.orZero()!! > 0

      imageViewHospitalDetail.loadImage(mContext, hospital.image.orEmpty(),
          R.drawable.drawable_hospital_placeholder)

      textViewHospitalDetailName.text = hospital.name
      textViewHospitalDetailType.text = hospital.type
      textViewHospitalDetailDescription.text = hospital.description
      textViewHospitalDetailLocation.text = hospital.address
      textViewHospitalDetailPhone.text = hospital.telephone

      textViewHospitalDetailDescription.showOrRemove(hospital.description.isNullOrBlank().not())
      groupHospitalDetailPhone.showOrRemove(hospital.telephone.isNullOrBlank().not())
    }
    hospital.location?.let { location ->
      updateLocation(location, hospital.name.orEmpty())
    }
  }

  private fun showLoadingState(isLoading: Boolean) {
    binding.apply {
      spinKitLoadHospitalDetail.showOrRemove(isLoading)
      layoutHospitalDetail.showOrRemove(isLoading.not())
    }
  }

  private fun updateLocation(location: LatLng, hospitalName: String) {
    googleMap?.let { map ->
      map.addMarker(MarkerOptions().position(location).title(hospitalName))
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
    }
  }
}