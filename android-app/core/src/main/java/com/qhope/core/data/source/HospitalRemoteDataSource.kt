package com.qhope.core.data.source

import com.qhope.core.data.source.response.hospital.HospitalDetailResponse
import com.qhope.core.data.source.response.hospital.HospitalResponse

interface HospitalRemoteDataSource {

  suspend fun getHospitals(): List<HospitalResponse>

  suspend fun getHospitalDetail(id: String): HospitalDetailResponse

  suspend fun searchHospitals(query: String): List<HospitalResponse>
}