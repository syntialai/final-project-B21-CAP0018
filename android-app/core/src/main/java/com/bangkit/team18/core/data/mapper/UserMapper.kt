package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.domain.model.user.User

object UserMapper {

  const val NAME_FIELD = "name"
  const val PHONE_NUMBER_FIELD = "phone_number"
  const val IMAGE_URL_FIELD = "image_url"
  const val BIRTH_DATE_FIELD = "birth_date"
  const val VERIFICATION_STATUS_FIELD = "verification_status"
  const val NO_KTP_FIELD = "no_ktp"
  const val GENDER_FIELD = "gender"

  fun UserResponse.mapToUser(): User {
    return User(
      id,
      name,
      phone_number,
      image_url,
      birth_date,
      verification_status
    )
  }

  fun User.mapToUserResponse(): UserResponse {
    return UserResponse(
      id,
      name,
      phoneNumber,
      imageUrl,
      birthDate,
      verificationStatus
    )
  }
}