package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.domain.model.home.Hospital
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.GeoPoint

object HospitalMapper {

  const val NAME_FIELD = "nama_rumah_sakit"

  fun mapToHospitals(responses: List<HospitalResponse>): List<Hospital> {
    return DataMapper.mapToModels(responses, ::mapToHospital)
  }

  fun mapToRoomTypes(responses: List<RoomTypeResponse>): List<RoomType> {
    return DataMapper.mapToModels(responses, ::mapToRoomType)
  }

  // TODO: Update missing items
  fun mapToHospital(response: HospitalResponse) = Hospital(
      id = response.id,
      name = response.nama_rumah_sakit,
      image = "",
      type = response.jenis_rumah_sakit,
      location = getGeoLocation(response.alamat_rumah_sakit),
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
      response.alamat_str,
      response.kelurahan,
      response.kecamatan,
      response.kota_administrasi,
      response.kode_pos
  ).joinToString()

  private fun getGeoLocation(geoPoint: GeoPoint) = GeoLocation(geoPoint.latitude,
      geoPoint.longitude)
}