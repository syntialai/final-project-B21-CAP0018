package com.qhope.core.data.mapper

import com.google.firebase.Timestamp
import com.qhope.core.api.source.response.transaction.TransactionDetailResponse
import com.qhope.core.api.source.response.transaction.TransactionUserResponse
import com.qhope.core.api.source.response.transaction.UploadReferralLetterResponse
import com.qhope.core.domain.model.booking.ReferralLetter
import com.qhope.core.domain.model.history.History
import com.qhope.core.domain.model.history.HistoryDetail
import com.qhope.core.domain.model.history.HistoryStatus
import com.qhope.core.domain.model.history.RoomTypeHistory
import com.qhope.core.domain.model.history.UserHistory
import com.qhope.core.utils.view.DataUtils
import com.qhope.core.utils.view.DataUtils.orHyphen
import com.qhope.core.utils.view.DataUtils.orZero
import com.qhope.core.utils.view.DateUtils.toDateString
import java.util.Date
import java.util.concurrent.TimeUnit

object BookingMapper {

  fun mapToReferralLetter(uploadReferralLetterResponse: UploadReferralLetterResponse) =
    ReferralLetter(
      name = uploadReferralLetterResponse.name.orEmpty(),
      url = uploadReferralLetterResponse.url.orEmpty()
    )

  fun mapToHistories(responses: List<TransactionDetailResponse>): List<History> {
    return responses.map { response ->
      History(
        id = response.id.orEmpty(),
        hospitalImagePath = response.hospital?.image.orEmpty(),
        hospitalName = response.hospital?.name.orEmpty(),
        createdAt = DataUtils.toFormattedDateTime(
          Date((response.created_at ?: 0) * 1000),
          DataUtils.MMMM_D_YYYY
        ),
        nightCount = 1,
        status = response.status?.let { HistoryStatus.valueOf(it) } ?: HistoryStatus.ONGOING
      )
    }
  }

  fun mapToHistoryDetail(response: TransactionDetailResponse) = HistoryDetail(
    id = response.id.orEmpty(),
    hospitalId = response.hospital?.id.orEmpty(),
    hospitalImagePath = response.hospital?.image.orEmpty(),
    hospitalName = response.hospital?.name.orEmpty(),
    startDate = response.created_at?.toDateString(DataUtils.MMMM_D_YYYY_HH_MM_A, true).orHyphen(),
    endDate = "",
    nightCount = 1,
    status = response.status?.let {
      HistoryStatus.valueOf(it)
    } ?: HistoryStatus.ONGOING,
    bookedAt = response.selected_date?.toDateString(DataUtils.MMMM_D_YYYY_HH_MM_A, true).orHyphen(),
    hospitalAddress = response.hospital?.address.orHyphen(),
    hospitalType = response.hospital?.type.orEmpty(),
    roomType = mapToRoomTypeHistory(response.room_type),
    roomCostPerDay = DataMapper.toFormattedPrice(response.total.orZero()),
    referralLetterFileName = response.referral_letter?.name.orHyphen(),
    referralLetterFileUrl = response.referral_letter?.url.orEmpty(),
    user = mapToUserHistory(response.user ?: TransactionUserResponse())
  )

  private fun mapToRoomTypeHistory(response: String?) = RoomTypeHistory(
    id = "",
    name = response.orEmpty()
  )

  private fun mapToUserHistory(response: TransactionUserResponse) = UserHistory(
    ktpNumber = response.nik.orHyphen(),
    name = response.name.orHyphen(),
    birthDate = response.birth_date.orZero().toDateString(DataUtils.MMMM_D_YYYY),
    gender = response.gender.orHyphen(),
    phoneNumber = response.phone_number.orHyphen()
  )

  private fun getNightCount(startDate: Timestamp, endDate: Timestamp?): Int? {
    return endDate?.let {
      TimeUnit.SECONDS.toDays(it.seconds - startDate.seconds).toInt()
    }
  }
}