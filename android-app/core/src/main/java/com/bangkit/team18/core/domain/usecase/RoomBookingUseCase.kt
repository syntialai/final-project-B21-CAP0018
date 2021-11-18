package com.bangkit.team18.core.domain.usecase

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.ReferralLetter
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RoomBookingUseCase {

  suspend fun getUserBookings(): Flow<ResponseWrapper<List<History>>>

  suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>>

  suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): Flow<ResponseWrapper<Boolean>>

  suspend fun uploadReferralLetter(file: File): Flow<ResponseWrapper<ReferralLetter>>
}