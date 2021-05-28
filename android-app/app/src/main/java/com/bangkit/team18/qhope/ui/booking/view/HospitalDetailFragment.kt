package com.bangkit.team18.qhope.ui.booking.view

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bangkit.team18.core.domain.model.booking.HospitalDetail
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.utils.view.ViewUtils.loadImageFromStorage
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
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.android.inject

class HospitalDetailFragment : BaseFragment<FragmentHospitalDetailBinding, HospitalDetailViewModel>(
    FragmentHospitalDetailBinding::inflate, HospitalDetailViewModel::class), OnMapReadyCallback {

  companion object {
    private const val DEFAULT_ZOOM = 16f
  }

  private val storage: FirebaseStorage by inject()

  private var googleMap: GoogleMap? = null

  override fun setupViews() {
    binding.apply {
      buttonHospitalDetailBack.setOnClickListener(this@HospitalDetailFragment)
      buttonHospitalDetailBook.setOnClickListener(this@HospitalDetailFragment)
    }
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
        buttonHospitalDetailBack -> activity?.finish()
        buttonHospitalDetailBook -> processBook()
        else -> Unit
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
    viewModel.getSelectedRoomType()?.let { selectedRoomType ->
      findNavController().navigate(
          HospitalDetailFragmentDirections.actionHospitalDetailFragmentToBookingConfirmationFragment(
              viewModel.getId(), selectedRoomType))
    }
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

      textViewHospitalDetailName.text = hospital.name
      textViewHospitalDetailType.text = hospital.type
      textViewHospitalDetailDescription.text = hospital.description
      textViewHospitalDetailLocation.text = hospital.address
      textViewHospitalDetailPhone.text = hospital.telephone

      textViewHospitalDetailDescription.showOrRemove(hospital.description.isNullOrBlank().not())
      groupHospitalDetailPhone.showOrRemove(hospital.telephone.isNotBlank())
    }
    setHospitalImage(hospital.imagePath)
    updateLocation(hospital.location, hospital.name)
  }

  private fun setHospitalImage(imagePath: String) {
    binding.imageViewHospitalDetail.loadImageFromStorage(mContext, storage.getReference(imagePath),
        R.drawable.drawable_hospital_placeholder)
  }

  private fun setupPriceByType(roomType: RoomType) {
    viewModel.selectRoomType(roomType)
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
      setOnCheckedChangeListener { _, checkedId ->
        setupPriceByType(roomTypes[checkedId])
        setupRoomAvailability(roomTypes[checkedId].availableRoomCount)
      }
      roomTypes.forEachIndexed { index, roomType ->
        addView(getTypeChip(index, roomType.name))
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