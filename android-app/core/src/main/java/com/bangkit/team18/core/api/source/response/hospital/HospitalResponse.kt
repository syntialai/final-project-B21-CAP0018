package com.bangkit.team18.core.api.source.response.hospital

data class HospitalResponse(
  
  var id: String? = null,

  var name: String? = null,

  var image: String? = null,

  var type: String? = null,

  var address: HospitalAddressResponse? = null,

  var available_room_count: Int? = null
)