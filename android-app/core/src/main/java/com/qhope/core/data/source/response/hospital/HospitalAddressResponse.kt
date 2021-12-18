package com.qhope.core.data.source.response.hospital

data class HospitalAddressResponse(

  var geopoint: HospitalLocationResponse? = null,

  var description: String? = null
)
