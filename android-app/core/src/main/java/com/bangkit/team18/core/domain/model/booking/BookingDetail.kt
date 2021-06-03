package com.bangkit.team18.core.domain.model.booking

import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.domain.model.user.User
import java.util.*

data class BookingDetail(

  var hospital: BookedHospital,

  var selectedRoomType: RoomType,

  var selectedDateTime: Calendar,

  var referralLetterUri: String,

  var referralLetterName: String,

  var user: User
) {
  constructor(hospital: BookedHospital, selectedRoomType: RoomType) : this(
    hospital,
    selectedRoomType,
    Calendar.getInstance(),
    "",
    "",
    User()
  )
}