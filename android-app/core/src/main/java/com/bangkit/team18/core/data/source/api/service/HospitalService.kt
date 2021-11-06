package com.bangkit.team18.core.data.source.api.service

import com.bangkit.team18.core.data.source.api.config.ApiConstants
import com.bangkit.team18.core.data.source.api.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.data.source.api.response.hospital.HospitalResponse
import retrofit2.http.GET

interface HospitalService {

  @GET(ApiConstants.HOSPITALS)
  suspend fun getAllHospitals(): List<HospitalResponse>

  @GET(ApiConstants.HOSPITALS_ID)
  suspend fun getHospitalDetail(id: String): HospitalDetailResponse
}