package com.bangkit.team18.core.data.source

import android.net.Uri
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface RoomBookingRemoteDataSource {

  suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>>

  suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?>

  suspend fun createBooking(bookingHashmap: HashMap<String, Any>)

  suspend fun uploadReferralLetter(
    userId: String,
    fileUri: Uri,
    fileName: String? = null
  ): Flow<ResponseWrapper<Uri>>
}