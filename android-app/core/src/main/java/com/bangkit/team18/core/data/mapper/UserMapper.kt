package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.user.UserResponse
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus

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
      birth_date?.seconds ?: 0L,
      verification_status,
      no_ktp,
      gender,
      place_of_birth,
      address
    )
  }

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