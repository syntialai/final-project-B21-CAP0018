package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.flow.Flow

interface HospitalRemoteDataSource {

  suspend fun getNearbyHospitals(location: GeoLocation): Flow<List<HospitalResponse>>

  suspend fun getHospitalDetail(id: String): Flow<HospitalResponse?>

  suspend fun getHospitalRoomTypes(id: String): Flow<List<RoomTypeResponse>>

  suspend fun searchHospitals(query: String): Flow<List<HospitalResponse>>
}