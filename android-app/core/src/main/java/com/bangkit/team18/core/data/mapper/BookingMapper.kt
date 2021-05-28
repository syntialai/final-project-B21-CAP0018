package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.domain.model.booking.Booking

object BookingMapper {

  const val HOSPITAL_ID_FIELD = "hospital_id"
  const val USER_ID_FIELD = "user_id"

  fun mapToBookingHashmap(booking: Booking): HashMap<String, Any> {
    return hashMapOf(
        HOSPITAL_ID_FIELD to booking.hospitalId,
        USER_ID_FIELD to booking.userId
    )
  }
}