package com.bangkit.team18.core.api.source.request.user

import com.bangkit.team18.core.domain.model.user.GenderType

data class UpdateUserProfileRequest(

  var name: String? = null,

  var date_of_birth: Long? = null,

  var gender: GenderType? = null,

  var phone_number: String? = null)
