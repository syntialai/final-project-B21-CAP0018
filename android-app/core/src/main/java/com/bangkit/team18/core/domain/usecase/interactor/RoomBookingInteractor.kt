package com.bangkit.team18.core.domain.usecase.interactor

import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.booking.ReferralLetter
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class RoomBookingInteractor(private val roomBookingRepository: RoomBookingRepository) :
  RoomBookingUseCase {

  override suspend fun getUserBookings(userId: String) = roomBookingRepository.getUserBookings(
    userId
  )

  override suspend fun getUserBookingDetail(
    id: String
  ) = roomBookingRepository.getUserBookingDetail(id)

  override suspend fun createBooking(
    createTransactionRequest: CreateTransactionRequest): Flow<ResponseWrapper<Boolean>> {
    return roomBookingRepository.createBooking(createTransactionRequest)
  }

  override suspend fun uploadReferralLetter(file: File): Flow<ResponseWrapper<ReferralLetter>> =
    roomBookingRepository.uploadReferralLetter(file)
}