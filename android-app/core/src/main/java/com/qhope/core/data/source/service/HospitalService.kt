package com.qhope.core.data.source.service

import com.qhope.core.data.source.response.hospital.HospitalDetailResponse
import com.qhope.core.data.source.response.hospital.HospitalResponse
import com.qhope.core.config.ApiConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HospitalService {

  @GET(ApiConstants.HOSPITALS)
  suspend fun getAllHospitals(): List<HospitalResponse>

  @GET(ApiConstants.HOSPITALS_ID)
  suspend fun getHospitalDetail(@Path("id") id: String): HospitalDetailResponse

  @GET(ApiConstants.SEARCH_HOSPITALS)
  suspend fun searchHospitals(@Query("query") query: String): List<HospitalResponse>
}