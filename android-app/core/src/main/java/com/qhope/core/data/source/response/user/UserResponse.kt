package com.qhope.core.data.source.response.user

data class UserResponse(

  var id: String? = null,

  var name: String? = null,

  var phone_number: String? = null,

  var photo_url: String? = null,

  var birth_date: Long? = null,

  var verification_status: String? = null,

  var nik: String? = null,

  var gender: String? = null,

  var birth_place: String? = null,

  var address: String? = null,

  var ktp_address: String? = null,

  var blood_type: String? = null,

  var district: String? = null,

  var village: String? = null,

  var city: String? = null,

  var neighborhood: String? = null,

  var hamlet: String? = null,

  var religion: String? = null
)