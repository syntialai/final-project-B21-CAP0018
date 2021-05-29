package com.bangkit.team18.core.domain.usecase

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.Booking
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import kotlinx.coroutines.flow.Flow

interface RoomBookingUseCase {

  suspend fun getUserBookings(userId: String): Flow<ResponseWrapper<List<History>>>

  suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>>

  suspend fun createBooking(booking: Booking): Flow<ResponseWrapper<Boolean>>

  suspend fun uploadReferralLetter(userId: String): Flow<ResponseWrapper<String>>
}