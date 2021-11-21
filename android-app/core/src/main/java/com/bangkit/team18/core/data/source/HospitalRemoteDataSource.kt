package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse

interface HospitalRemoteDataSource {

  suspend fun getHospitals(): List<HospitalResponse>

  suspend fun getHospitalDetail(id: String): HospitalDetailResponse

  suspend fun searchHospitals(query: String): List<HospitalResponse>
}