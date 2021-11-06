package com.bangkit.team18.core.data.source.api.response.hospital

data class HospitalResponse(
  
  var id: String? = null,

  var name: String? = null,

  var image: String? = null,

  var type: String? = null,

  var address: String? = null,

  var available_room_count: Int? = null
)