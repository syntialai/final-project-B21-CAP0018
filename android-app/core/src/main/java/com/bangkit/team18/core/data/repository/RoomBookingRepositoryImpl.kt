package com.bangkit.team18.core.data.repository

import android.net.Uri
import com.bangkit.team18.core.data.mapper.BookingMapper
import com.bangkit.team18.core.data.repository.base.FetchDataWrapper
import com.bangkit.team18.core.data.repository.base.UpdateDataWrapper
import com.bangkit.team18.core.data.source.RoomBookingRemoteDataSource
import com.bangkit.team18.core.data.source.response.history.HistoryDetailResponse
import com.bangkit.team18.core.data.source.response.history.HistoryResponse
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class RoomBookingRepositoryImpl(
  private val roomBookingRemoteDataSource: RoomBookingRemoteDataSource,
  private val ioDispatcher: CoroutineDispatcher
) : RoomBookingRepository {

  override suspend fun getUserBookings(userId: String): Flow<ResponseWrapper<List<History>>> {
    return object : FetchDataWrapper<List<HistoryResponse>, List<History>>() {
      override suspend fun fetchData(): Flow<List<HistoryResponse>?> {
        return roomBookingRemoteDataSource.getUserBookings(userId)
      }

      override suspend fun mapData(response: List<HistoryResponse>): List<History> {
        return BookingMapper.mapToHistories(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun getUserBookingDetail(id: String): Flow<ResponseWrapper<HistoryDetail>> {
    return object : FetchDataWrapper<HistoryDetailResponse, HistoryDetail>() {
      override suspend fun fetchData(): Flow<HistoryDetailResponse?> {
        return roomBookingRemoteDataSource.getUserBookingDetail(id)
      }

      override suspend fun mapData(response: HistoryDetailResponse): HistoryDetail {
        return BookingMapper.mapToHistoryDetail(response)
      }
    }.getData().flowOn(ioDispatcher)
  }

  override suspend fun createBooking(bookingDetail: BookingDetail): Flow<ResponseWrapper<Boolean>> {
    val bookingHashmap = BookingMapper.mapToBookingHashmap(bookingDetail)
    return object : UpdateDataWrapper<Unit>() {
      override suspend fun doUpdate() {
        roomBookingRemoteDataSource.createBooking(bookingHashmap)
      }
    }.updateData().flowOn(ioDispatcher)
  }

  override suspend fun uploadReferralLetter(
    userId: String,
    fileUri: Uri,
    fileName: String
  ): Flow<ResponseWrapper<Uri>> {
    return roomBookingRemoteDataSource.uploadReferralLetter(userId, fileUri, fileName)
  }
}