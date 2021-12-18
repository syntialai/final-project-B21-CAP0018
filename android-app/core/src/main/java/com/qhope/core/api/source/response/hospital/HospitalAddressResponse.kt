package com.qhope.core.api.source.response.hospital

data class HospitalAddressResponse(

  var geopoint: HospitalLocationResponse? = null,

  var description: String? = null
)
