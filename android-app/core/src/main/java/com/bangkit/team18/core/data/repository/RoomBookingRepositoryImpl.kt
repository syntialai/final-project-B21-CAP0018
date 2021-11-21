package com.bangkit.team18.core.data.repository

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.api.source.response.transaction.TransactionResponse
import com.bangkit.team18.core.api.source.response.transaction.UploadReferralLetterResponse
import com.bangkit.team18.core.data.mapper.BookingMapper
import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.repository.base.UpdateDataWrapper
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.ReferralLetter
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class RoomBookingRepositoryImpl(
  private val roomBookingRemoteDataSource: RoomBookingRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : RoomBookingRepository {

  override suspend fun getUserBookings(): Flow<ResponseWrapper<List<History>>> {
    return object : FetchDataWrapper<List<TransactionResponse>, List<History>>() {
      override suspend fun fetchData(): List<TransactionResponse> {
        return roomBookingRemoteDataSource.getUserBookings()
      }

      override suspend fun mapData(response: List<TransactionResponse>): List<History> {
        return BookingMapper.mapToHistories(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>> {
    return object : FetchDataWrapper<HistoryDetailResponse, HistoryDetail>() {
      override suspend fun fetchData(): HistoryDetailResponse {
        return roomBookingRemoteDataSource.getUserBookingDetail(id)
      }

      override suspend fun mapData(response: HistoryDetailResponse): HistoryDetail {
        return BookingMapper.mapToHistoryDetail(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): Flow<ResponseWrapper<Boolean>> {
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        roomBookingRemoteDataSource.createBooking(createTransactionRequest)
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