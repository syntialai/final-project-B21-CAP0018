package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.data.mapper.BookingMapper
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class RoomBookingRemoteDataSourceImpl(db: FirebaseFirestore, private val storage: FirebaseStorage) :
    BaseRemoteDataSource(), RoomBookingRemoteDataSource {

  private val transactionCollection = db.collection(CollectionConstants.TRANSACTION_COLLECTION)

  override suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>> {
    return transactionCollection.whereEqualTo(BookingMapper.USER_ID_FIELD, userId).loadData(
        HistoryResponse::class.java)
  }

  override suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?> {
    return transactionCollection.document(id).loadData(HistoryDetailResponse::class.java)
  }

  override suspend fun createBooking(bookingHashmap: HashMap<String, Any>) {
    transactionCollection.addData(bookingHashmap)
  }

  override suspend fun uploadReferralLetter(userId: String): Flow<String> {
    TODO("Not yet implemented")
  }


}