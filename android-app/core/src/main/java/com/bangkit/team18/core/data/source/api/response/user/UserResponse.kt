package com.bangkit.team18.core.data.source.api.response.user

import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.VerificationStatus

data class UserResponse(

  var id: String? = null,

  var name: String? = null,

  var phone_number: String? = null,

  var image_url: String? = null,

  var birth_date: Long? = null,

  var verification_status: VerificationStatus? = null,

  var no_ktp: String? = null,

  var gender: GenderType? = null,

  var place_of_birth: String? = null,

  var address: String? = null
)