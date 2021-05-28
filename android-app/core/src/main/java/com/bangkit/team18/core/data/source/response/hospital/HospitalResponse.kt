package com.bangkit.team18.core.data.source.response.hospital

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

// TODO: Add image, state and country
data class HospitalResponse(

    @DocumentId
    val id: String,

    val nama_rumah_sakit: String,

    val alamat_rumah_sakit: GeoPoint,

    val alamat_str: String,

    val email: String,

    val foto_rumah_sakit: String,

    val jenis_rumah_sakit: String,

    val kecamatan: String,

    val kelurahan: String,

    val kode_pos: String,

    val kota_administrasi: String,

    val nomor_fax: String,

    val nomor_telepon: String,

    val telepon_humas: String,

    val website: String
) {
  constructor() : this(
      "",
      "",
      GeoPoint(0.0, 0.0),
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
  )
}
