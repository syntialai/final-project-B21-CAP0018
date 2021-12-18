package com.qhope.core.api.source.response.hospital

data class HospitalDetailResponse(

  var id: String? = null,

  var name: String? = null,

  var image: String? = null,

  var type: String? = null,

  var description: String? = null,

  var address: HospitalAddressResponse? = null,

  var telephone: String? = null,

  var available_room_count: Int? = null,

  var room_types: List<HospitalRoomTypeResponse>? = null
)