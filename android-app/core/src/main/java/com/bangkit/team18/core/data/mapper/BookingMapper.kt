package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.domain.model.booking.Booking
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

object BookingMapper {

  const val HOSPITAL_ID_FIELD = "hospital_id"
  const val USER_ID_FIELD = "user_id"

  fun mapToBookingHashmap(booking: Booking): HashMap<String, Any> {
    return hashMapOf(
        HOSPITAL_ID_FIELD to booking.hospitalId,
        USER_ID_FIELD to booking.userId
    )
  }

  fun mapToHistories(responses: List<HistoryResponse>): List<History> {
    return DataMapper.mapToModels(responses, BookingMapper::mapToHistory)
  }

  fun mapToHistoryDetail(response: HistoryDetailResponse) = HistoryDetail(
      id = response.id,
      hospitalImagePath = response.hospitalImagePath,
      hospitalName = response.hospitalName,
      startDate = response.startDate.toString(),
      endDate = response.endDate.toString(),
      nightCount = getNightCount(response.startDate, response.endDate),
      status = HistoryStatus.valueOf(response.status)
  )

  private fun mapToHistory(response: HistoryResponse) = History(
      id = response.id,
      hospitalImagePath = response.hospitalImagePath,
      hospitalName = response.hospitalName,
      createdAt = response.startDate.toString(),
      nightCount = getNightCount(response.startDate, response.endDate),
      status = HistoryStatus.valueOf(response.status)
  )

  private fun getNightCount(startDate: Timestamp, endDate: Timestamp?): Int? {
    return endDate?.let {
      TimeUnit.SECONDS.toDays(it.seconds - startDate.seconds).toInt()
    }
  }
}