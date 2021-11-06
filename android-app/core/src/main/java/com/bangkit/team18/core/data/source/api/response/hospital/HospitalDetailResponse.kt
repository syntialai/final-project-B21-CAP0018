package com.bangkit.team18.core.data.source.api.response.hospital

data class HospitalDetailResponse(

  var id: String? = null,

  var name: String? = null,

  var image: String? = null,

  var type: String? = null,

  var description: String? = null,

  var address: String? = null,

  var location: HospitalLocationResponse? = null,

  var telephone: String? = null,

  var available_room_count: Int? = null,

  var room_types: List<HospitalRoomTypeResponse>? = null
)