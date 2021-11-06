package com.bangkit.team18.core.data.source.api.service

import com.bangkit.team18.core.data.source.api.config.ApiConstants
import com.bangkit.team18.core.data.source.api.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.data.source.api.response.transaction.CreateTransactionResponse
import com.bangkit.team18.core.data.source.api.response.transaction.TransactionDetailResponse
import com.bangkit.team18.core.data.source.api.response.transaction.TransactionResponse
import com.bangkit.team18.core.data.source.api.response.transaction.UploadReferralLetterResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TransactionService {

  @GET(ApiConstants.TRANSACTIONS)
  suspend fun getTransactions(): List<TransactionResponse>

  @GET(ApiConstants.TRANSACTIONS_ID)
  suspend fun getTransactionDetails(id: String): TransactionDetailResponse

  @POST(ApiConstants.TRANSACTIONS)
  suspend fun createTransaction(
    createTransactionRequest: CreateTransactionRequest
  ): CreateTransactionResponse

  @Multipart
  @POST(ApiConstants.UPLOAD_REFERRAL_LETTER)
  suspend fun uploadReferralLetter(
    @Part referral_letter: MultipartBody.Part
  ): UploadReferralLetterResponse
}