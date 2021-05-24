package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.domain.model.home.Hospital

object HospitalMapper {

  fun mapToHospitals(responses: List<HospitalResponse>): List<Hospital> {
    return DataMapper.mapToModels(responses, ::mapToHospital)
  }

  fun mapToRoomTypes(responses: List<RoomTypeResponse>): List<RoomType> {
    return DataMapper.mapToModels(responses, ::mapToRoomType)
  }

  // TODO: Update missing items
  fun mapToHospital(response: HospitalResponse) = Hospital(
      id = response.id,
      name = response.name,
      image = "",
      type = response.type,
      location = response.address,
      address = getAddress(response),
      availableRoomCount = 0
  )

  fun mapToRoomType(response: RoomTypeResponse) = RoomType(
      id = response.id,
      name = "",
      price = DataMapper.toFormattedPrice(response.price),
      availableRoomCount = response.availableRoom
  )

  private fun getAddress(response: HospitalResponse) = arrayListOf(
      response.addressStr,
      response.subDistrict,
      response.districts,
      response.administrationCity,
      response.postalCode
  ).joinToString()
}