package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.flow.Flow

interface HospitalRemoteDataSource {

  fun getNearbyHospitals(location: GeoLocation): Flow<List<HospitalResponse>>

  fun getHospitalDetail(id: String): Flow<HospitalResponse?>

  fun getHospitalRoomTypes(id: String): Flow<List<RoomTypeResponse>>

  fun searchHospitals(query: String): Flow<List<HospitalResponse>>
}