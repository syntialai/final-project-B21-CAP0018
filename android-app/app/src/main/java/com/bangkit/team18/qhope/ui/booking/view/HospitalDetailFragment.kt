package com.bangkit.team18.qhope.ui.booking.view

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.booking.HospitalDetail
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.utils.view.ViewUtils.loadImage
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentHospitalDetailBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.booking.viewmodel.HospitalDetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip

class HospitalDetailFragment : BaseFragment<FragmentHospitalDetailBinding, HospitalDetailViewModel>(
    FragmentHospitalDetailBinding::inflate, HospitalDetailViewModel::class), OnMapReadyCallback {

  companion object {
    private const val DEFAULT_ZOOM = 15f
  }

  private var googleMap: GoogleMap? = null

  override fun setupViews() {
    setupGoogleMaps()
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.initializeId(arguments?.getString(RoomBookingActivity.ID).orEmpty())
    viewModel.fetchHospitalDetails()

    viewModel.hospital.observe(viewLifecycleOwner, {
      it?.let { data ->
        setHospitalData(data)
      }
    })
    viewModel.hospitalRoomTypes.observe(viewLifecycleOwner, {
      it?.let { roomTypes ->
        setupRoomAvailability(roomTypes.size)
        setupTypeData(roomTypes)
      }
    })
  }

  override fun onMapReady(map: GoogleMap) {
    googleMap = map
    viewModel.hospital.value?.let { hospital ->
      updateLocation(hospital.location, hospital.name)
    }
  }

  override fun showLoadingState(isLoading: Boolean) {
    binding.apply {
      spinKitLoadHospitalDetail.showOrRemove(isLoading)
      layoutHospitalDetail.showOrRemove(isLoading.not())
      layoutCheckoutHospital.showOrRemove(isLoading.not())
    }
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        buttonHospitalDetailBack -> { /* TODO: Call onBackPressed */ }
        buttonHospitalDetailBook -> processBook()
      }
    }
  }

  private fun getTypeChip(index: Int, text: String): Chip {
    return (layoutInflater.inflate(R.layout.layout_hospital_detail_chip_type,
        binding.chipGroupHospitalDetailRoomType, false) as Chip).apply {
      this.id = index
      this.text = text
      this.isChecked = index == 0
    }
  }

  private fun processBook() {
    // TODO: Get data and go to booking confirmation
    findNavController().navigate(
        HospitalDetailFragmentDirections.actionHospitalDetailFragmentToBookingConfirmationFragment())
  }

  private fun setupGoogleMaps() {
    val mapFragment = childFragmentManager.findFragmentById(
        R.id.fragment_hospital_detail_location_maps) as SupportMapFragment
    lifecycleScope.launchWhenResumed {
      mapFragment.getMapAsync(this@HospitalDetailFragment)
    }
  }

  private fun setHospitalData(hospital: HospitalDetail) {
    binding.apply {
      buttonHospitalDetailBook.isEnabled = hospital.availableRoomCount > 0

      imageViewHospitalDetail.loadImage(mContext, hospital.image,
          R.drawable.drawable_hospital_placeholder)

      textViewHospitalDetailName.text = hospital.name
      textViewHospitalDetailType.text = hospital.type
      textViewHospitalDetailDescription.text = hospital.description
      textViewHospitalDetailLocation.text = hospital.address
      textViewHospitalDetailPhone.text = hospital.telephone

      textViewHospitalDetailDescription.showOrRemove(hospital.description.isNullOrBlank().not())
      groupHospitalDetailPhone.showOrRemove(hospital.telephone.isNotBlank())
    }
    updateLocation(hospital.location, hospital.name)
  }

  private fun setupPriceByType(roomType: RoomType) {
    // TODO: call viewmodel to update checked roomtype
    binding.textViewHospitalDetailPrice.text = roomType.price
  }

  private fun setupRoomAvailability(availableRoomCount: Int) {
    val isAvailable = availableRoomCount > 0
    binding.apply {
      chipHospitalItemRoomAvailable.showOrRemove(isAvailable)
      chipHospitalItemRoomNotAvailable.showOrRemove(isAvailable.not())
      buttonHospitalDetailBook.isEnabled = isAvailable
    }
  }

  private fun setupTypeData(roomTypes: List<RoomType>) {
    binding.chipGroupHospitalDetailRoomType.apply {
      removeAllViews()
      roomTypes.forEachIndexed { index, roomType ->
        addView(getTypeChip(index, roomType.name))
      }
      setOnCheckedChangeListener { _, checkedId ->
        setupPriceByType(roomTypes[checkedId])
        setupRoomAvailability(roomTypes[checkedId].availableRoomCount)
      }
    }
  }

  private fun updateLocation(location: LatLng, hospitalName: String) {
    googleMap?.let { map ->
      map.addMarker(MarkerOptions().position(location).title(hospitalName))
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
    }
  }
}