package com.qhope.core.data.source

import com.qhope.core.api.source.request.transaction.CreateTransactionRequest
import com.qhope.core.api.source.response.transaction.CreateTransactionResponse
import com.qhope.core.api.source.response.transaction.TransactionDetailResponse
import com.qhope.core.api.source.response.transaction.UploadReferralLetterResponse
import java.io.File

interface RoomBookingRemoteDataSource {

  suspend fun getUserBookings(): List<TransactionDetailResponse>

  suspend fun getUserBookingDetail(id: String): TransactionDetailResponse

  suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): CreateTransactionResponse

  suspend fun uploadReferralLetter(file: File): UploadReferralLetterResponse
}