package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.repository.HospitalRepository
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class HospitalRepositoryImpl(private val hospitalRemoteDataSource: HospitalRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher) : HospitalRepository {

  override suspend fun getNearbyHospitals(
      location: GeoLocation): Flow<ResponseWrapper<List<Hospital>>> {
    return object : FetchDataWrapper<List<HospitalResponse>, List<Hospital>>() {
      override fun fetchData(): Flow<List<HospitalResponse>> {
        return hospitalRemoteDataSource.getNearbyHospitals(location)
      }

      override fun mapData(response: List<HospitalResponse>): List<Hospital> {
        TODO("Not yet implemented")
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getHospitalDetail(id: String): Flow<ResponseWrapper<Hospital>> {
    return object : FetchDataWrapper<HospitalResponse, Hospital>() {
      override fun fetchData(): Flow<HospitalResponse?> {
        return hospitalRemoteDataSource.getHospitalDetail(id)
      }

      override fun mapData(response: HospitalResponse): Hospital {
        TODO("Not yet implemented")
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getHospitalRoomTypes(id: String): Flow<ResponseWrapper<List<RoomType>>> {
    return object : FetchDataWrapper<List<RoomTypeResponse>, List<RoomType>>() {
      override fun fetchData(): Flow<List<RoomTypeResponse>> {
        return hospitalRemoteDataSource.getHospitalRoomTypes(id)
      }

      override fun mapData(response: List<RoomTypeResponse>): List<RoomType> {
        TODO("Not yet implemented")
      }
    }.getData().flowOn(ioDispatcher)
  }
}