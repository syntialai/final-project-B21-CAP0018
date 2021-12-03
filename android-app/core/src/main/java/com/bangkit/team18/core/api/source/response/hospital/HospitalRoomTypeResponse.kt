package com.bangkit.team18.core.api.source.response.hospital

data class HospitalRoomTypeResponse(

  var id: String? = null,

  var type: String? = null,

  var price: Double? = null,

  var total_room: Int? = null,

  var available_room: Int? = null,
)