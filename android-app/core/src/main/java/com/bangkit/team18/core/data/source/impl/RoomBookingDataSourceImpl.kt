package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.data.mapper.BookingMapper
import com.bangkit.team18.core.data.source.RoomBookingDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.domain.model.booking.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class RoomBookingDataSourceImpl(db: FirebaseFirestore, private val storage: FirebaseStorage) :
    BaseRemoteDataSource(), RoomBookingDataSource {

  private val transactionCollection = db.collection(CollectionConstants.TRANSACTION_COLLECTION)

  override suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>> {
    return transactionCollection.whereEqualTo(BookingMapper.USER_ID_FIELD, userId).loadData(
        HistoryResponse::class.java)
  }

  override suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?> {
    return transactionCollection.document(id).loadData(HistoryDetailResponse::class.java)
  }

  override suspend fun createBooking(booking: Booking) {
    val bookingHashmap = BookingMapper.mapToBookingHashmap(booking)
    transactionCollection.addData(bookingHashmap)
  }

  override suspend fun uploadReferralLetter(userId: String): Flow<String> {
    TODO("Not yet implemented")
  }


}