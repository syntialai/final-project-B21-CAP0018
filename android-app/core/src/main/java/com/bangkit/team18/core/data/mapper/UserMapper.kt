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
  const val KTP_URL_FIELD = "ktp_url"
  const val SELFIE_URL_FIELD = "selfie_url"
  const val ADDRESS_FIELD = "address"
  const val PLACE_OF_BIRTH_FIELD = "place_of_birth"

  fun UserResponse.mapToUser(): User {
    return User(
      id,
      name,
      phone_number,
      image_url,
      birth_date,
      verification_status,
      no_ktp,
      gender,
      place_of_birth,
      address
    )
  }
}