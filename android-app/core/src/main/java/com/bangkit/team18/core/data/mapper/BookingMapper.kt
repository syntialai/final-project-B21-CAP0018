package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.google.firebase.Timestamp
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
  const val USER_ID_FIELD = "user_id"

  fun mapToBookingHashmap(bookingDetail: BookingDetail): HashMap<String, Any> {
    return hashMapOf(
      BOOKED_AT_FIELD to Timestamp.now(),
      STATUS_FIELD to HistoryStatus.ON_GOING.name,
      HOSPITAL_ID_FIELD to bookingDetail.hospital.id,
      HOSPITAL_NAME_FIELD to bookingDetail.hospital.name,
      HOSPITAL_ADDRESS_FIELD to bookingDetail.hospital.address,
      HOSPITAL_IMAGE_PATH_FIELD to bookingDetail.hospital.imagePath,
      HOSPITAL_TYPE_FIELD to bookingDetail.hospital.type,
      CHECK_IN_AT_FIELD to bookingDetail.selectedDateTime.time,
      ROOM_TYPE_FIELD to bookingDetail.selectedRoomType.name,
      ROOM_COST_PER_DAY_FIELD to bookingDetail.selectedRoomType.price,
      USER_ID_FIELD to bookingDetail.userId
    )
  }

  fun mapToHistories(responses: List<HistoryResponse>): List<History> {
    return DataMapper.mapToModels(responses, BookingMapper::mapToHistory)
  }

  // TODO: Add user and PDF data
  fun mapToHistoryDetail(response: HistoryDetailResponse) = HistoryDetail(
    id = response.id,
    hospitalId = response.hospital_id,
    hospitalImagePath = response.hospital_image_path,
    hospitalName = response.hospital_name,
    startDate = response.check_in_at.toString(),
    endDate = response.check_out_at.toString(),
    nightCount = getNightCount(response.check_in_at, response.check_out_at),
    status = HistoryStatus.valueOf(response.status),
    bookedAt = response.booked_at.toString(),
    hospitalAddress = response.hospital_address,
    hospitalType = response.hospital_type,
    roomCostPerDay = DataMapper.toFormattedPrice(response.room_cost_per_day),
    referralLetterFileName = "",
    referralLetterFilePath = ""
  )

  private fun mapToHistory(response: HistoryResponse) = History(
    id = response.id,
    hospitalImagePath = response.hospital_image_path,
    hospitalName = response.hospital_name,
    createdAt = response.booked_at.toString(),
    nightCount = getNightCount(response.check_in_at, response.check_out_at),
    status = HistoryStatus.valueOf(response.status)
  )

  private fun getNightCount(startDate: Timestamp, endDate: Timestamp?): Int? {
    return endDate?.let {
      TimeUnit.SECONDS.toDays(it.seconds - startDate.seconds).toInt()
    }
  }
}