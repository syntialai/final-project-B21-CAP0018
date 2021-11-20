package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse
import kotlinx.coroutines.flow.Flow

interface HospitalRemoteDataSource {

  suspend fun getHospitals(): Flow<List<HospitalResponse>>

  suspend fun getHospitalDetail(id: String): Flow<HospitalDetailResponse>

  suspend fun searchHospitals(query: String): Flow<List<HospitalResponse>>
}