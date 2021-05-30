package com.bangkit.team18.core.data.mapper

import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.bangkit.team18.core.domain.model.booking.BookedHospital
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.model.hospital.HospitalDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

object HospitalMapper {

  const val NAME_FIELD = "nama_rumah_sakit"

  fun mapToHospitals(responses: List<HospitalResponse>): List<Hospital> {
    return DataMapper.mapToModels(responses, ::mapToHospital)
  }

  fun mapToRoomTypes(responses: List<RoomTypeResponse>): List<RoomType> {
    return DataMapper.mapToModels(responses, ::mapToRoomType)
  }

  fun mapToHospitalDetail(response: HospitalResponse) = HospitalDetail(
    id = response.id,
    name = response.nama_rumah_sakit,
    imagePath = response.foto_rumah_sakit,
    type = response.jenis_rumah_sakit,
    location = getLatLng(response.alamat_rumah_sakit),
    address = getAddress(response),
    telephone = response.nomor_telepon,
    description = null,
    availableRoomCount = 0
  )

  fun mapToBookedHospital(hospitalDetail: HospitalDetail) = BookedHospital(
    id = hospitalDetail.id,
    address = hospitalDetail.address,
    name = hospitalDetail.name,
    imagePath = hospitalDetail.imagePath,
    type = hospitalDetail.type
  )

  private fun mapToHospital(response: HospitalResponse) = Hospital(
    id = response.id,
    name = response.nama_rumah_sakit,
    image = response.foto_rumah_sakit,
    type = response.jenis_rumah_sakit,
    location = getGeoLocation(response.alamat_rumah_sakit),
    address = getAddress(response),
    availableRoomCount = 0
  )

  private fun mapToRoomType(response: RoomTypeResponse) = RoomType(
    id = response.id,
    name = response.name,
    price = response.price,
    availableRoomCount = response.available_room
  )

  private fun getAddress(response: HospitalResponse) = arrayListOf(
    response.alamat_str,
    response.kelurahan,
    response.kecamatan,
    response.kota_administrasi,
    response.kode_pos
  ).joinToString()

  private fun getGeoLocation(geoPoint: GeoPoint) = GeoLocation(
    geoPoint.latitude,
    geoPoint.longitude
  )

  private fun getLatLng(geoPoint: GeoPoint) = LatLng(
    geoPoint.latitude,
    geoPoint.longitude
  )
}