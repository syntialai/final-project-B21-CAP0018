package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.mapper.HospitalMapper
import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.model.hospital.HospitalDetail
import com.bangkit.team18.core.domain.repository.HospitalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class HospitalRepositoryImpl(
  private val hospitalRemoteDataSource: HospitalRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : HospitalRepository {

  override suspend fun getHospitals(): Flow<ResponseWrapper<List<Hospital>>> {
    return object : FetchDataWrapper<List<HospitalResponse>, List<Hospital>>() {
      override suspend fun fetchData(): Flow<List<HospitalResponse>> {
        return hospitalRemoteDataSource.getHospitals()
      }

      override suspend fun mapData(response: List<HospitalResponse>): List<Hospital> {
        return HospitalMapper.mapToHospitals(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getHospitalDetail(id: String): Flow<ResponseWrapper<HospitalDetail>> {
    return object : FetchDataWrapper<HospitalDetailResponse, HospitalDetail>() {
      override suspend fun fetchData(): Flow<HospitalDetailResponse> {
        return hospitalRemoteDataSource.getHospitalDetail(id)
      }

      override suspend fun mapData(response: HospitalDetailResponse): HospitalDetail {
        return HospitalMapper.mapToHospitalDetail(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun searchHospitals(query: String): Flow<ResponseWrapper<List<Hospital>>> {
    return object : FetchDataWrapper<List<HospitalResponse>, List<Hospital>>() {
      override suspend fun fetchData(): Flow<List<HospitalResponse>> {
        return hospitalRemoteDataSource.searchHospitals(query)
      }

      override suspend fun mapData(response: List<HospitalResponse>): List<Hospital> {
        return HospitalMapper.mapToHospitals(response)
      }
    }.getData().flowOn(ioDispatcher)
  }
}