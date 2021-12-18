package com.qhope.core.domain.model.booking

import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.domain.model.user.User
import java.util.Calendar

data class BookingDetail(

  var hospital: BookedHospital,

  var selectedRoomType: RoomType,

  var selectedDateTime: Calendar,

  var referralLetterUri: String,

  var referralLetterName: String,

  var user: User,

  var userPhoneNumber: String
) {
  constructor(hospital: BookedHospital, selectedRoomType: RoomType) : this(
    hospital,
    selectedRoomType,
    Calendar.getInstance(),
    "",
    "",
    User(),
    ""
  )
}