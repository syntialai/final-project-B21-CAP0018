package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.api.source.response.transaction.CreateTransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.api.source.service.TransactionService
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import com.bangkit.team18.core.data.source.config.CollectionConstants
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class RoomBookingRemoteDataSourceImpl(
  db: FirebaseFirestore,
  private val transactionService: TransactionService
) : BaseRemoteDataSource(), RoomBookingRemoteDataSource {

  private val transactionCollection = db.collection(CollectionConstants.TRANSACTION_COLLECTION)

  override suspend fun getUserBookings(): Flow<List<TransactionResponse>> {
    return transactionService.getTransactions().loadAsFlow()
  }

  override suspend fun getUserBookingDetail(id: String): Flow<HistoryDetailResponse?> {
    return transactionCollection.document(id).loadData(HistoryDetailResponse::class.java)
  }

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): Flow<CreateTransactionResponse> {
    return transactionService.createTransaction(createTransactionRequest).loadAsFlow()
  }

  override suspend fun uploadReferralLetter(file: File): Flow<UploadReferralLetterResponse> {
    val fileBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", file.name.orEmpty(), fileBody)
    return transactionService.uploadReferralLetter(filePart).loadAsFlow()
  }
}