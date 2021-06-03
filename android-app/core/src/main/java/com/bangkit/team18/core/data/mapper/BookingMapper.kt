package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.data.source.response.history.RoomTypeHistoryResponse
import com.bangkit.team18.core.data.source.response.history.UserHistoryResponse
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.history.*
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.utils.view.DataUtils
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
  const val REFERRAL_LETTER_NAME = "referral_letter_name"
  const val REFERRAL_LETTER_URL = "referral_letter_url"
  const val USER_ID_FIELD = "user_id"
  const val USER_DATA_FIELD = "user_data"

  fun mapToBookingHashmap(bookingDetail: BookingDetail): HashMap<String, Any> {
    return hashMapOf(
      BOOKED_AT_FIELD to Timestamp.now(),
      STATUS_FIELD to HistoryStatus.ON_GOING.name,
      HOSPITAL_ID_FIELD to bookingDetail.hospital.id,
      HOSPITAL_NAME_FIELD to bookingDetail.hospital.name,
      HOSPITAL_ADDRESS_FIELD to bookingDetail.hospital.address,
      HOSPITAL_IMAGE_PATH_FIELD to bookingDetail.hospital.imagePath,
      HOSPITAL_TYPE_FIELD to bookingDetail.hospital.type,
      REFERRAL_LETTER_NAME to bookingDetail.referralLetterName,
      REFERRAL_LETTER_URL to bookingDetail.referralLetterUri,
      CHECK_IN_AT_FIELD to bookingDetail.selectedDateTime.time,
      ROOM_TYPE_FIELD to mapToRoomTypeData(bookingDetail.selectedRoomType),
      ROOM_COST_PER_DAY_FIELD to bookingDetail.selectedRoomType.price,
      USER_ID_FIELD to bookingDetail.user.id,
      USER_DATA_FIELD to mapToUserData(bookingDetail.user)
    )
  }

  fun mapToHistories(responses: List<HistoryResponse>): List<History> {
    return DataMapper.mapToModels(responses, BookingMapper::mapToHistory)
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

  private fun mapToRoomTypeData(roomType: RoomType) = hashMapOf<String, Any>(
    HospitalMapper.ROOM_DATA_ID_FIELD to roomType.id,
    HospitalMapper.ROOM_DATA_NAME_FIELD to roomType.name
  )

  private fun mapToUserData(user: User) = hashMapOf<String, Any?>(
    UserMapper.NAME_FIELD to user.name,
    UserMapper.BIRTH_DATE_FIELD to (user.birthDate ?: Timestamp.now()),
    UserMapper.PHONE_NUMBER_FIELD to user.phoneNumber,
    UserMapper.NO_KTP_FIELD to user.ktpNumber,
    UserMapper.GENDER_FIELD to user.gender?.name,
    UserMapper.PLACE_OF_BIRTH_FIELD to user.placeOfBirth
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

  private fun mapToHistory(response: HistoryResponse) = History(
    id = response.id,
    hospitalImagePath = response.hospital_image_path,
    hospitalName = response.hospital_name,
    createdAt = DataUtils.toFormattedDateTime(response.booked_at.toDate(), DataUtils.MMMM_D_YYYY),
    nightCount = getNightCount(response.check_in_at, response.check_out_at),
    status = HistoryStatus.valueOf(response.status)
  )

  private fun getNightCount(startDate: Timestamp, endDate: Timestamp?): Int? {
    return endDate?.let {
      TimeUnit.SECONDS.toDays(it.seconds - startDate.seconds).toInt()
    }
  }
}