package com.bangkit.team18.core.data.source.response.hospital

import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class HospitalResponse(

    @DocumentId
    val id: String,

    @PropertyName("nama_rumah_sakit")
    val name: String,

    @PropertyName("alamat_rumah_sakit")
    val address: GeoLocation,

    @PropertyName("alamat_str")
    val addressStr: String,

    @PropertyName("email")
    val email: String,

    @PropertyName("jenis_rumah_sakit")
    val type: String,

    @PropertyName("kecamatan")
    val districts: String,

    @PropertyName("kelurahan")
    val subDistrict: String,

    @PropertyName("kode_pos")
    val postalCode: String,

    @PropertyName("kota_administrasi")
    val administrationCity: String,

    @PropertyName("nomor_fax")
    val faxNumber: String,

    @PropertyName("nomor_telepon")
    val telephoneNumber: String,

    @PropertyName("telepon_humas")
    val publicRelationPhoneNumber: String,

    @PropertyName("website")
    val website: String
) {
  constructor() : this(
      "",
      "",
      GeoLocation(0.0, 0.0),
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
