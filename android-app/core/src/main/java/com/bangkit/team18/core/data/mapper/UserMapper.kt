package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus

object UserMapper {

  fun mapToUser(userResponse: com.bangkit.team18.core.api.source.response.user.UserResponse): User {
    return User(
      userResponse.id.orEmpty(),
      userResponse.name.orEmpty(),
      userResponse.phone_number.orEmpty(),
      userResponse.image_url.orEmpty(),
      userResponse.birth_date,
      userResponse.verification_status?.let {
        VerificationStatus.valueOf(it)
      } ?: VerificationStatus.NOT_UPLOAD,
      userResponse.no_ktp.orEmpty(),
      userResponse.gender?.let {
        GenderType.valueOf(it)
      } ?: GenderType.MALE,
      userResponse.place_of_birth.orEmpty(),
      userResponse.address.orEmpty()
    )
  }
}