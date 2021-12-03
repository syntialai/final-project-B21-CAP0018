package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.api.source.response.transaction.CreateTransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionDetailResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.api.source.service.TransactionService
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class RoomBookingRemoteDataSourceImpl(
  private val transactionService: TransactionService
) : BaseRemoteDataSource(), RoomBookingRemoteDataSource {

  override suspend fun getUserBookings(): List<TransactionResponse> {
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
    val fileBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", file.name.orEmpty(), fileBody)
    return transactionService.uploadReferralLetter(filePart)
  }
}