package com.bangkit.team18.core.domain.model.hospital

import com.google.android.gms.maps.model.LatLng

data class HospitalDetail(

  val id: String,

  val name: String,

  val imagePath: String,

  val type: String,

  val description: String?,

  val address: String,

  val location: LatLng,

  val telephone: String,

  val availableRoomCount: Int
)