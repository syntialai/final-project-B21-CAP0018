package com.bangkit.team18.core.domain.repository

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.model.hospital.HospitalDetail
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {

  suspend fun getHospitals(): Flow<ResponseWrapper<List<Hospital>>>

  suspend fun getHospitalDetail(id: String): Flow<ResponseWrapper<HospitalDetail>>

  suspend fun searchHospitals(query: String): Flow<ResponseWrapper<List<Hospital>>>
}