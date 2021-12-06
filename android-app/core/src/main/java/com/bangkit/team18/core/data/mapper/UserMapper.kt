package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

object UserMapper {

  fun mapToUser(userResponse: com.bangkit.team18.core.api.source.response.user.UserResponse): User {
    return User(
      userResponse.id.orEmpty(),
      userResponse.name.orEmpty(),
      userResponse.phone_number.orEmpty(),
      userResponse.photo_url.orEmpty(),
      userResponse.birth_date,
      userResponse.verification_status?.let {
        VerificationStatus.valueOf(it)
      } ?: VerificationStatus.NOT_UPLOAD,
      userResponse.nik.orEmpty(),
      userResponse.gender?.let {
        GenderType.valueOf(it)
      } ?: GenderType.MALE,
      userResponse.birth_place.orEmpty(),
      userResponse.address ?: userResponse.ktp_address.orEmpty(),
      userResponse.ktp_address.orEmpty(),
      userResponse.blood_type.orEmpty(),
      userResponse.district.orEmpty(),
      userResponse.village.orEmpty(),
      userResponse.city.orEmpty(),
      userResponse.neighborhood.orEmpty(),
      userResponse.hamlet.orEmpty(),
      userResponse.religion.orEmpty()
    )
  }

  fun constructUpdateUserRequest(
    userProfileRequest: UpdateUserProfileRequest,
    image: File?): Map<String, RequestBody> {
    val imageFileBody = image?.asRequestBody("image/*".toMediaTypeOrNull())
    val requestMap = hashMapOf<String, RequestBody>()
    requestMap.addIfNotNull("photo", imageFileBody)
    requestMap.addIfNotNull("name", getTextRequestBody(userProfileRequest.name))
    requestMap.addIfNotNull(
      "birth_date",
      getTextRequestBody(userProfileRequest.birth_date.toString())
    )
    requestMap.addIfNotNull("gender", getTextRequestBody(userProfileRequest.gender))
    requestMap.addIfNotNull("phone_number", getTextRequestBody(userProfileRequest.phone_number))
    requestMap.addIfNotNull("birth_place", getTextRequestBody(userProfileRequest.birth_place))
    requestMap.addIfNotNull("address", getTextRequestBody(userProfileRequest.address))
    Timber.d("requestMap: $requestMap")
    return requestMap.toMap()
  }

  private fun HashMap<String, RequestBody>.addIfNotNull(key: String, value: RequestBody?) {
    if (value != null) {
      set(key, value)
    }
  }

  private fun getTextRequestBody(value: String?): RequestBody? {
    return value?.toRequestBody("text/plain".toMediaTypeOrNull());
  }
}