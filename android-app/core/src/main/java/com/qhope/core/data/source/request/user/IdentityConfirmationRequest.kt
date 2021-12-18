package com.qhope.core.data.source.request.user

data class IdentityConfirmationRequest(

  var nik: String? = null,

  var name: String? = null,

  var date_of_birth: Long? = null,

  var ktp_address: String? = null,

  var blood_type: String? = null,

  var birth_place: String? = null,

  var district: String? = null,

  var village: String? = null,

  var city: String? = null,

  var neighborhood: String? = null,

  var hamlet: String? = null,

  var gender: String? = null,

  var religion: String? = null
)