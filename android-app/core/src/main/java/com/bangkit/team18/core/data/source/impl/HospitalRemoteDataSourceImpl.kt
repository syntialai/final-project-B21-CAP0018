package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.response.hospital.HospitalDetailResponse
import com.bangkit.team18.core.api.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.api.source.service.HospitalService
import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class HospitalRemoteDataSourceImpl(
  db: FirebaseFirestore,
  private val hospitalService: HospitalService
) : BaseRemoteDataSource(), HospitalRemoteDataSource {

  private val hospitalCollections = db.collection(CollectionConstants.HOSPITAL_COLLECTION)

  override suspend fun getHospitals(): Flow<List<HospitalResponse>> {
    return hospitalService.getAllHospitals().loadAsFlow()
  }

  override suspend fun getHospitalDetail(id: String): Flow<HospitalDetailResponse> {
    return hospitalService.getHospitalDetail(id).loadAsFlow()
  }

  override suspend fun searchHospitals(query: String): Flow<List<HospitalResponse>> {
    return hospitalService.searchHospitals(query).loadAsFlow()
  }
}