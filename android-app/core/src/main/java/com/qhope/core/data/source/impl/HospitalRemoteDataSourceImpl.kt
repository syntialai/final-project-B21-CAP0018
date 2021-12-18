package com.qhope.core.data.source.impl

import com.qhope.core.api.source.response.hospital.HospitalDetailResponse
import com.qhope.core.api.source.response.hospital.HospitalResponse
import com.qhope.core.api.source.service.HospitalService
import com.qhope.core.data.source.HospitalRemoteDataSource
import com.qhope.core.data.source.base.BaseRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HospitalRemoteDataSourceImpl(
  private val hospitalService: HospitalService
) : BaseRemoteDataSource(), HospitalRemoteDataSource {

  override suspend fun getHospitals(): List<HospitalResponse> {
    return hospitalService.getAllHospitals()
  }

  override suspend fun getHospitalDetail(id: String): HospitalDetailResponse {
    return hospitalService.getHospitalDetail(id)
  }

  override suspend fun searchHospitals(query: String): List<HospitalResponse> {
    return hospitalService.searchHospitals(query)
  }
}