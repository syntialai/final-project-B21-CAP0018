package com.qhope.core.data.source.impl

import com.qhope.core.api.source.request.transaction.CreateTransactionRequest
import com.qhope.core.api.source.response.transaction.CreateTransactionResponse
import com.qhope.core.api.source.response.transaction.TransactionDetailResponse
import com.qhope.core.api.source.response.transaction.UploadReferralLetterResponse
import com.qhope.core.api.source.service.TransactionService
import com.qhope.core.data.source.RoomBookingRemoteDataSource
import com.qhope.core.data.source.base.BaseRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class RoomBookingRemoteDataSourceImpl(
  private val transactionService: TransactionService
) : BaseRemoteDataSource(), RoomBookingRemoteDataSource {

  override suspend fun getUserBookings(): List<TransactionDetailResponse> {
    return transactionService.getTransactions()
  }

  override suspend fun getUserBookingDetail(id: String): TransactionDetailResponse {
    return transactionService.getTransactionDetails(id)
  }

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): CreateTransactionResponse {
    return transactionService.createTransaction(createTransactionRequest)
  }

  override suspend fun uploadReferralLetter(file: File): UploadReferralLetterResponse {
    val name = System.currentTimeMillis().toString()
    val fileBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", name, fileBody)
    return transactionService.uploadReferralLetter(filePart)
  }
}