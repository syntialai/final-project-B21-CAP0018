package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.api.source.response.transaction.CreateTransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RoomBookingRemoteDataSource {

  suspend fun getUserBookings(): List<TransactionResponse>

  suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?>

  suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): CreateTransactionResponse

  suspend fun uploadReferralLetter(file: File): UploadReferralLetterResponse
}