package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.domain.model.booking.Booking
import kotlinx.coroutines.flow.Flow

interface RoomBookingDataSource {

  suspend fun getUserBookings(userId: String): Flow<List<HistoryResponse>>

  suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?>

  suspend fun createBooking(booking: Booking)

  suspend fun uploadReferralLetter(userId: String): Flow<String>
}