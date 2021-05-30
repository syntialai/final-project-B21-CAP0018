package com.bangkit.team18.core.domain.usecase

import android.net.Uri
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import kotlinx.coroutines.flow.Flow

interface RoomBookingUseCase {

  suspend fun getUserBookings(userId: String): Flow<ResponseWrapper<List<History>>>

  suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>>

  suspend fun createBooking(bookingDetail: BookingDetail): Flow<ResponseWrapper<Boolean>>

  suspend fun uploadReferralLetter(userId: String, fileUri: Uri): Flow<ResponseWrapper<String>>
}