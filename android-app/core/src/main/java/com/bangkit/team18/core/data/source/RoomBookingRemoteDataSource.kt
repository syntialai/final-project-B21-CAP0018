package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import kotlinx.coroutines.flow.Flow

interface RoomBookingRemoteDataSource {

  suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>>

  suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?>

  suspend fun createBooking(bookingHashmap: HashMap<String, Any>)

  suspend fun uploadReferralLetter(userId: String): Flow<String>
}