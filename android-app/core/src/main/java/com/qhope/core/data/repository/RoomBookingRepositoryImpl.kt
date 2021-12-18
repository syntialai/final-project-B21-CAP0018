package com.qhope.core.data.repository

import com.qhope.core.data.source.request.transaction.CreateTransactionRequest
import com.qhope.core.data.source.response.transaction.CreateTransactionResponse
import com.qhope.core.data.source.response.transaction.TransactionDetailResponse
import com.qhope.core.data.source.response.transaction.UploadReferralLetterResponse
import com.qhope.core.data.mapper.BookingMapper
import com.qhope.core.data.repository.base.FetchDataWrapper
import com.qhope.core.data.source.RoomBookingRemoteDataSource
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.booking.ReferralLetter
import com.qhope.core.domain.model.history.History
import com.qhope.core.domain.model.history.HistoryDetail
import com.qhope.core.domain.repository.RoomBookingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class RoomBookingRepositoryImpl(
  private val roomBookingRemoteDataSource: RoomBookingRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : RoomBookingRepository {

  override suspend fun getUserBookings(): Flow<ResponseWrapper<List<History>>> {
    return object : FetchDataWrapper<List<TransactionDetailResponse>, List<History>>() {
      override suspend fun fetchData(): List<TransactionDetailResponse> {
        return roomBookingRemoteDataSource.getUserBookings()
      }

      override suspend fun mapData(response: List<TransactionDetailResponse>): List<History> {
        return BookingMapper.mapToHistories(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>> {
    return object : FetchDataWrapper<TransactionDetailResponse, HistoryDetail>() {
      override suspend fun fetchData(): TransactionDetailResponse {
        return roomBookingRemoteDataSource.getUserBookingDetail(id)
      }

      override suspend fun mapData(response: TransactionDetailResponse): HistoryDetail {
        return BookingMapper.mapToHistoryDetail(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): Flow<ResponseWrapper<String?>> {
    return object : FetchDataWrapper<CreateTransactionResponse, String?>() {
      override suspend fun fetchData(): CreateTransactionResponse {
        return roomBookingRemoteDataSource.createBooking(createTransactionRequest)
      }

      override suspend fun mapData(response: CreateTransactionResponse): String? {
        return response.id
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun uploadReferralLetter(file: File): Flow<ResponseWrapper<ReferralLetter>> {
    return object : FetchDataWrapper<UploadReferralLetterResponse, ReferralLetter>() {
      override suspend fun fetchData(): UploadReferralLetterResponse {
        return roomBookingRemoteDataSource.uploadReferralLetter(file)
      }

      override suspend fun mapData(response: UploadReferralLetterResponse): ReferralLetter {
        return BookingMapper.mapToReferralLetter(response)
      }
    }.updateData().flowOn(ioDispatcher)
  }
}