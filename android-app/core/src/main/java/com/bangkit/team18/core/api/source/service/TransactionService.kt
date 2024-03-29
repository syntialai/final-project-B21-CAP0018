package com.bangkit.team18.core.api.source.service

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.api.source.response.transaction.CreateTransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionDetailResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.config.ApiConstants
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TransactionService {

  @GET(ApiConstants.TRANSACTIONS)
  suspend fun getTransactions(): List<TransactionResponse>

  @GET(ApiConstants.TRANSACTIONS_ID)
  suspend fun getTransactionDetails(@Path("id") id: String): TransactionDetailResponse

  @POST(ApiConstants.TRANSACTIONS)
  suspend fun createTransaction(
    @Body createTransactionRequest: CreateTransactionRequest
  ): CreateTransactionResponse

  @Multipart
  @POST(ApiConstants.UPLOAD_REFERRAL_LETTER)
  suspend fun uploadReferralLetter(
    @Part file: MultipartBody.Part
  ): UploadReferralLetterResponse
}