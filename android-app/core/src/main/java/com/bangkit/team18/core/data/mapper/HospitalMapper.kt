package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalRoomTypeResponse
import com.bangkit.team18.core.domain.model.booking.BookedHospital
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.model.hospital.HospitalDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.google.android.gms.maps.model.LatLng

object HospitalMapper {

  const val NAME_FIELD = "nama_rumah_sakit"
  const val ROOM_DATA_ID_FIELD = "id"
  const val ROOM_DATA_NAME_FIELD = "name"

  fun mapToHospitalDetail(response: HospitalDetailResponse) =
    HospitalDetail(
      id = response.id.orEmpty(),
      name = response.name.orEmpty(),
      imagePath = response.image.orEmpty(),
      type = response.type.orEmpty(),
      location = LatLng(
        response.location?.lat?.toDoubleOrNull() ?: 0.0,
        response.location?.lng?.toDoubleOrNull() ?: 0.0
      ),
      address = response.address.orEmpty(),
      telephone = response.telephone.orEmpty(),
      description = response.description.orEmpty(),
      availableRoomCount = response.available_room_count ?: 0,
      roomTypes = mapToRoomTypes(response.room_types.orEmpty())
    )

  private fun mapToRoomTypes(hospitalRoomTypeResponses: List<HospitalRoomTypeResponse>): List<RoomType> =
    hospitalRoomTypeResponses.map { response ->
      RoomType(
        id = response.id.orEmpty(),
        name = response.name.orEmpty(),
        price = response.price ?: 0.0,
        availableRoomCount = response.available_room ?: 0
      )
    }

  fun mapToHospitals(responses: List<HospitalResponse>): List<Hospital> {
    return responses.map { response ->
      Hospital(
        id = response.id.orEmpty(),
        name = response.name.orEmpty(),
        image = response.image.orEmpty(),
        type = response.type.orEmpty(),
        address = response.address.orEmpty(),
        availableRoomCount = response.available_room_count ?: 0
      )
    }
  }

  fun mapToBookedHospital(hospitalDetail: HospitalDetail) = BookedHospital(
    id = hospitalDetail.id,
    address = hospitalDetail.address,
    name = hospitalDetail.name,
    imagePath = hospitalDetail.imagePath,
    type = hospitalDetail.type
  )
}