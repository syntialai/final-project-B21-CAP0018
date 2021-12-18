package com.qhope.core.data.source.service

import com.qhope.core.data.source.request.transaction.CreateTransactionRequest
import com.qhope.core.data.source.response.transaction.CreateTransactionResponse
import com.qhope.core.data.source.response.transaction.TransactionDetailResponse
import com.qhope.core.data.source.response.transaction.UploadReferralLetterResponse
import com.qhope.core.config.ApiConstants
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TransactionService {

  @GET(ApiConstants.TRANSACTIONS)
  suspend fun getTransactions(): List<TransactionDetailResponse>

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