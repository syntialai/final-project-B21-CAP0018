package com.bangkit.team18.core.domain.model.booking

import com.bangkit.team18.core.domain.model.hospital.RoomType
import java.util.Calendar

data class BookingDetail(

    var userId: String,

    var hospital: BookedHospital,

    var selectedRoomType: RoomType,

    var selectedDateTime: Calendar,

    var referralLetterFilePath: String
) {
  constructor(hospital: BookedHospital, selectedRoomType: RoomType) : this(
      "",
      hospital,
      selectedRoomType,
      Calendar.getInstance(),
      ""
  )
}