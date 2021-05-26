package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.data.source.HospitalRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.hospital.HospitalResponse
import com.bangkit.team18.core.data.source.response.hospital.RoomTypeResponse
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class HospitalRemoteDataSourceImpl(private val db: FirebaseFirestore) : BaseRemoteDataSource(),
    HospitalRemoteDataSource {

  private val hospitalCollections = db.collection(CollectionConstants.HOSPITAL_COLLECTION)

  override fun getNearbyHospitals(location: GeoLocation): Flow<List<HospitalResponse>> {
    return hospitalCollections.loadNearby(HospitalResponse::class.java, location)
  }

  override fun getHospitalDetail(id: String): Flow<HospitalResponse?> {
    return hospitalCollections.document(id).loadData(HospitalResponse::class.java)
  }

  override fun getHospitalRoomTypes(id: String): Flow<List<RoomTypeResponse>> {
    return hospitalCollections.document(id).collection(
        CollectionConstants.ROOM_COLLECTION).loadData(RoomTypeResponse::class.java)
  }
}