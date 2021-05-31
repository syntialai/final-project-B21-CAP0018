package com.bangkit.team18.core.data.source.impl

import android.net.Uri
import com.bangkit.team18.core.data.mapper.BookingMapper
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

@ExperimentalCoroutinesApi
class RoomBookingRemoteDataSourceImpl(
  db: FirebaseFirestore,
  private val storage: FirebaseStorage
) : BaseRemoteDataSource(), RoomBookingRemoteDataSource {

  private val transactionCollection = db.collection(CollectionConstants.TRANSACTION_COLLECTION)

  override suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>> {
    return transactionCollection.whereEqualTo(BookingMapper.USER_ID_FIELD, userId).loadData(
      HistoryResponse::class.java
    )
  }

  override suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?> {
    return transactionCollection.document(id).loadData(HistoryDetailResponse::class.java)
  }

  override suspend fun createBooking(bookingHashmap: HashMap<String, Any>) {
    transactionCollection.addData(bookingHashmap)
  }

  override suspend fun uploadReferralLetter(
    userId: String,
    fileUri: Uri,
    fileName: String?
  ): Flow<ResponseWrapper<Uri>> {
    return storage.reference.child(
      "${CollectionConstants.USER_DATA_PATH}/${userId}/${CollectionConstants.REFERRAL_LETTER_PATH}/${
        fileName ?: Timestamp.now()
      }"
    ).addAndGetFileUrl(
      fileUri, "application/pdf"
    ).catch { exception ->
      emit(
        (if (exception is IOException) {
          ResponseWrapper.NetworkError()
        } else {
          ResponseWrapper.Error(exception.message)
        })
      )
    }
  }
}