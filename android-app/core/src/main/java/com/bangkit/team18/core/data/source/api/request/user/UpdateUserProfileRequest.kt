package com.bangkit.team18.core.data.source.api.request.user

import com.bangkit.team18.core.domain.model.user.GenderType

data class UpdateUserProfileRequest(

  var name: String? = null,

  var phone_number: String? = null,

  var birth_date: Long? = null,

  var gender: GenderType? = null
)
