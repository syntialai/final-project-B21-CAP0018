package com.qhope.core.data.source.response.hospital

data class HospitalRoomTypeResponse(

  var id: String? = null,

  var type: String? = null,

  var price: Double? = null,

  var total_room: Int? = null,

  var available_room: Int? = null,
)