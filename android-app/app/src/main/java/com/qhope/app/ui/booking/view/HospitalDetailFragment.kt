package com.qhope.app.ui.booking.view

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.qhope.app.R
import com.qhope.app.databinding.FragmentHospitalDetailBinding
import com.qhope.app.ui.base.view.BaseFragment
import com.qhope.app.ui.booking.viewmodel.HospitalDetailViewModel
import com.qhope.core.domain.model.hospital.HospitalDetail
import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.utils.view.DataUtils.isNotNull
import com.qhope.core.utils.view.ViewUtils.showOrRemove

class HospitalDetailFragment : BaseFragment<FragmentHospitalDetailBinding, HospitalDetailViewModel>(
  FragmentHospitalDetailBinding::inflate, HospitalDetailViewModel::class
), OnMapReadyCallback {

  companion object {
    private const val DEFAULT_ZOOM = 16f
  }

  private val args: HospitalDetailFragmentArgs by navArgs()

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

    viewModel.initializeId(args.id)
    viewModel.fetchHospitalDetails()

    viewModel.hospital.observe(viewLifecycleOwner, {
      it?.let { data ->
        setHospitalData(data)
        setupRoomAvailability(data.availableRoomCount)
        setupTypeData(data.roomTypes)
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
        buttonHospitalDetailBack -> findNavController().navigateUp()
        buttonHospitalDetailBook -> processBook()
        else -> Unit
      }
    }
  }

  private fun getTypeChip(index: Int, text: String): Chip {
    return (layoutInflater.inflate(
      R.layout.layout_hospital_detail_chip_type,
      binding.chipGroupHospitalDetailRoomType, false
    ) as Chip).apply {
      this.id = index
      this.text = text
      this.isChecked = index == 0
    }
  }

  private fun processBook() {
    if (viewModel.getBookedHospital().isNotNull() && viewModel.getSelectedRoomType().isNotNull()) {
      findNavController().navigate(
        HospitalDetailFragmentDirections.actionHospitalDetailFragmentToBookingConfirmationFragment(
          viewModel.getBookedHospital()!!, viewModel.getSelectedRoomType()!!
        )
      )
    }
  }

  private fun setupGoogleMaps() {
    val mapFragment = childFragmentManager.findFragmentById(
      R.id.fragment_hospital_detail_location_maps
    ) as SupportMapFragment
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
    context?.let { context ->
      Glide.with(context)
        .load(imagePath)
        .placeholder(R.drawable.drawable_hospital_placeholder)
        .into(binding.imageViewHospitalDetail)
    }
  }

  private fun setupPriceByType(roomType: RoomType) {
    viewModel.selectRoomType(roomType)
    binding.textViewHospitalDetailPrice.text = viewModel.mapToFormattedPrice(roomType.price)
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

  override fun onDestroyView() {
    super.onDestroyView()
    googleMap = null
  }
}