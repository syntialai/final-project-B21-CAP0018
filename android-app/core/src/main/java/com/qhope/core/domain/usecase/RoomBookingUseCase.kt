package com.qhope.core.domain.usecase

import com.qhope.core.data.source.request.transaction.CreateTransactionRequest
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.booking.ReferralLetter
import com.qhope.core.domain.model.history.History
import com.qhope.core.domain.model.history.HistoryDetail
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RoomBookingUseCase {

  suspend fun getUserBookings(): Flow<ResponseWrapper<List<History>>>

  suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>>

  suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest
  ): Flow<ResponseWrapper<String?>>

  suspend fun uploadReferralLetter(file: File): Flow<ResponseWrapper<ReferralLetter>>
}