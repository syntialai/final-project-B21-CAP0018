package com.qhope.core.domain.usecase.interactor

import com.qhope.core.data.source.request.transaction.CreateTransactionRequest
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.booking.ReferralLetter
import com.qhope.core.domain.repository.RoomBookingRepository
import com.qhope.core.domain.usecase.RoomBookingUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class RoomBookingInteractor(private val roomBookingRepository: RoomBookingRepository) :
  RoomBookingUseCase {

  override suspend fun getUserBookings() = roomBookingRepository.getUserBookings()

  override suspend fun getUserBookingDetail(
    id: String
  ) = roomBookingRepository.getUserBookingDetail(id)

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest
  ): Flow<ResponseWrapper<String?>> {
    return roomBookingRepository.createBooking(createTransactionRequest)
  }

  override suspend fun uploadReferralLetter(file: File): Flow<ResponseWrapper<ReferralLetter>> =
    roomBookingRepository.uploadReferralLetter(file)
}