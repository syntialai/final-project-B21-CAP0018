package com.bangkit.team18.core.domain.usecase.interactor

import com.bangkit.team18.core.domain.model.booking.Booking
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase

class RoomBookingInteractor(private val roomBookingRepository: RoomBookingRepository) :
    RoomBookingUseCase {

  override suspend fun getUserBookings(userId: String) = roomBookingRepository.getUserBookings(
      userId)

  override suspend fun getUserBookingDetail(
      id: String) = roomBookingRepository.getUserBookingDetail(id)

  override suspend fun createBooking(booking: Booking) = roomBookingRepository.createBooking(
      booking)

  override suspend fun uploadReferralLetter(
      userId: String) = roomBookingRepository.uploadReferralLetter(userId)
}