package com.bangkit.team18.core.domain.repository

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.model.hospital.HospitalDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {

  suspend fun getNearbyHospitals(location: GeoLocation): Flow<ResponseWrapper<List<Hospital>>>

  suspend fun getHospitalDetail(id: String): Flow<ResponseWrapper<HospitalDetail>>

  suspend fun getHospitalRoomTypes(id: String): Flow<ResponseWrapper<List<RoomType>>>

  suspend fun searchHospitals(query: String): Flow<ResponseWrapper<List<Hospital>>>
}