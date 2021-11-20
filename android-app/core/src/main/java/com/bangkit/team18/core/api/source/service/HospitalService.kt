package com.bangkit.team18.core.api.source.service

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.config.ApiConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalService {

  @GET(ApiConstants.HOSPITALS)
  suspend fun getAllHospitals(): List<HospitalResponse>

  @GET(ApiConstants.HOSPITALS_ID)
  suspend fun getHospitalDetail(id: String): HospitalDetailResponse

  @GET(ApiConstants.SEARCH_HOSPITALS)
  suspend fun searchHospitals(@Query("query") query: String): List<HospitalResponse>
}