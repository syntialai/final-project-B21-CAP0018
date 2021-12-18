package com.qhope.core.data.mapper

import com.google.android.gms.maps.model.LatLng
import com.qhope.core.api.source.response.hospital.HospitalDetailResponse
import com.qhope.core.api.source.response.hospital.HospitalResponse
import com.qhope.core.api.source.response.hospital.HospitalRoomTypeResponse
import com.qhope.core.domain.model.booking.BookedHospital
import com.qhope.core.domain.model.home.Hospital
import com.qhope.core.domain.model.hospital.HospitalDetail
import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.utils.view.DataUtils.orHyphen

object HospitalMapper {

  fun mapToHospitalDetail(response: HospitalDetailResponse) =
    HospitalDetail(
      id = response.id.orEmpty(),
      name = response.name.orEmpty(),
      imagePath = response.image.orEmpty(),
      type = response.type.orEmpty(),
      location = LatLng(
        response.address?.geopoint?.lat?.toDoubleOrNull() ?: 0.0,
        response.address?.geopoint?.lng?.toDoubleOrNull() ?: 0.0
      ),
      address = response.address?.description.orHyphen(),
      telephone = response.telephone.orEmpty(),
      description = response.description.orEmpty(),
      availableRoomCount = response.available_room_count ?: 0,
      roomTypes = mapToRoomTypes(response.room_types.orEmpty())
    )

  private fun mapToRoomTypes(hospitalRoomTypeResponses: List<HospitalRoomTypeResponse>): List<RoomType> =
    hospitalRoomTypeResponses.map { response ->
      RoomType(
        id = response.id.orEmpty(),
        name = response.type.orEmpty(),
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
        address = response.address?.description.orEmpty(),
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