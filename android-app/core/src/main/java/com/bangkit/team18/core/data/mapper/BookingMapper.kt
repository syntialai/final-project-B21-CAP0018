package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.RoomTypeHistoryResponse
import com.bangkit.team18.core.data.source.response.history.UserHistoryResponse
import com.bangkit.team18.core.domain.model.booking.ReferralLetter
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.bangkit.team18.core.domain.model.history.RoomTypeHistory
import com.bangkit.team18.core.domain.model.history.UserHistory
import com.bangkit.team18.core.utils.view.DataUtils
import com.google.firebase.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit

object BookingMapper {

  const val BOOKED_AT_FIELD = "booked_at"
  const val STATUS_FIELD = "status"
  const val HOSPITAL_ID_FIELD = "hospital_id"
  const val HOSPITAL_NAME_FIELD = "hospital_name"
  const val HOSPITAL_IMAGE_PATH_FIELD = "hospital_image_path"
  const val HOSPITAL_ADDRESS_FIELD = "hospital_address"
  const val HOSPITAL_TYPE_FIELD = "hospital_type"
  const val CHECK_IN_AT_FIELD = "check_in_at"
  const val CHECK_OUT_AT_FIELD = "check_out_at"
  const val ROOM_TYPE_FIELD = "room_type"
  const val ROOM_COST_PER_DAY_FIELD = "room_cost_per_day"
  const val REFERRAL_LETTER_NAME = "referral_letter_name"
  const val REFERRAL_LETTER_URL = "referral_letter_url"
  const val USER_ID_FIELD = "user_id"
  const val USER_DATA_FIELD = "user_data"

  fun mapToReferralLetter(uploadReferralLetterResponse: UploadReferralLetterResponse) =
    ReferralLetter(
      name = uploadReferralLetterResponse.name.orEmpty(),
      url = uploadReferralLetterResponse.url.orEmpty()
    )

  fun mapToHistories(responses: List<TransactionResponse>): List<History> {
    return responses.map { response ->
      History(
        id = response.id.orEmpty(),
        hospitalImagePath = response.hospital_image.orEmpty(),
        hospitalName = response.hospital_name.orEmpty(),
        createdAt = DataUtils.toFormattedDateTime(
          Date(response.created_at ?: 0),
          DataUtils.MMMM_D_YYYY
        ),
        nightCount = response.night_count ?: 1,
        status = response.status?.let { HistoryStatus.valueOf(it) } ?: HistoryStatus.ON_GOING
      )
    }
  }

  fun mapToHistoryDetail(response: HistoryDetailResponse) = HistoryDetail(
    id = response.id,
    hospitalId = response.hospital_id,
    hospitalImagePath = response.hospital_image_path,
    hospitalName = response.hospital_name,
    startDate = DataUtils.toFormattedDateTime(
      response.check_in_at.toDate(),
      DataUtils.MMMM_D_YYYY_HH_MM_A
    ),
    endDate = response.check_out_at?.toDate()?.let {
      DataUtils.toFormattedDateTime(it, DataUtils.MMMM_D_YYYY_HH_MM_A)
    },
    nightCount = getNightCount(response.check_in_at, response.check_out_at),
    status = HistoryStatus.valueOf(response.status),
    bookedAt = DataUtils.toFormattedDateTime(
      response.booked_at.toDate(),
      DataUtils.MMMM_D_YYYY_HH_MM_A
    ),
    hospitalAddress = response.hospital_address,
    hospitalType = response.hospital_type,
    roomType = mapToRoomTypeHistory(response.room_type),
    roomCostPerDay = DataMapper.toFormattedPrice(response.room_cost_per_day),
    referralLetterFileName = response.referral_letter_name,
    referralLetterFileUrl = response.referral_letter_url,
    user = mapToUserHistory(response.user_data)
  )

  private fun mapToRoomTypeHistory(response: RoomTypeHistoryResponse) = RoomTypeHistory(
    id = response.id,
    name = response.name
  )

  private fun mapToUserHistory(response: UserHistoryResponse) = UserHistory(
    ktpNumber = response.no_ktp,
    name = response.name,
    birthDate = "${response.place_of_birth}, ${
      DataUtils.toFormattedDateTime(
        response.birth_date.toDate(),
        DataUtils.MMMM_D_YYYY
      )
    }",
    gender = response.gender,
    phoneNumber = response.phone_number
  )

  private fun getNightCount(startDate: Timestamp, endDate: Timestamp?): Int? {
    return endDate?.let {
      TimeUnit.SECONDS.toDays(it.seconds - startDate.seconds).toInt()
    }
  }
}