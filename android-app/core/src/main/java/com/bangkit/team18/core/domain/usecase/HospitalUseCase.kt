package com.bangkit.team18.core.domain.usecase

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.domain.model.home.Hospital
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.flow.Flow

interface HospitalUseCase {

  suspend fun getNearbyHospitals(location: GeoLocation): Flow<ResponseWrapper<List<Hospital>>>

  suspend fun getHospitalDetail(id: String): Flow<ResponseWrapper<Hospital>>

  suspend fun getHospitalRoomTypes(id: String): Flow<ResponseWrapper<List<RoomType>>>
}