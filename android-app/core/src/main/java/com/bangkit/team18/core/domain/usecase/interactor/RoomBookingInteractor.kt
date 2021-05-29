package com.bangkit.team18.core.domain.usecase.interactor

import android.net.Uri
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.repository.RoomBookingRepository
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase

class RoomBookingInteractor(private val roomBookingRepository: RoomBookingRepository) :
    RoomBookingUseCase {

  override suspend fun getUserBookings(userId: String) = roomBookingRepository.getUserBookings(
      userId)

  override suspend fun getUserBookingDetail(
      id: String) = roomBookingRepository.getUserBookingDetail(id)

  override suspend fun createBooking(
      bookingDetail: BookingDetail) = roomBookingRepository.createBooking(bookingDetail)

  override suspend fun uploadReferralLetter(userId: String,
      fileUri: Uri) = roomBookingRepository.uploadReferralLetter(userId, fileUri)
}