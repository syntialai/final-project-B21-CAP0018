package com.bangkit.team18.qhope.ui.booking.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.mapper.DataMapper
import com.bangkit.team18.core.domain.model.booking.BookedHospital
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import java.util.*

class BookingConfirmationViewModel(
  private val roomBookingUseCase: RoomBookingUseCase,
  authRepository: AuthRepository
) : BaseViewModelWithAuth(authRepository) {

  private var _bookingDetail = MutableLiveData<BookingDetail>()
  val bookingDetail: LiveData<BookingDetail>
    get() = _bookingDetail

  private var _isBooked = MutableLiveData<Boolean>()
  val isBooked: LiveData<Boolean>
    get() = _isBooked

  init {
    initAuthStateListener()
  }

  fun getSelectedTime() = Pair(
    _bookingDetail.value?.selectedDateTime?.get(Calendar.HOUR),
    _bookingDetail.value?.selectedDateTime?.get(Calendar.MINUTE)
  )

  fun mapToFormattedPrice(price: Double): String = DataMapper.toFormattedPrice(price)

  fun setBookingDetail(hospital: BookedHospital, selectedRoomType: RoomType) {
    _bookingDetail.value = BookingDetail(hospital, selectedRoomType)
    _bookingDetail.publishChanges()
  }

  fun setSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
    _bookingDetail.value?.selectedDateTime?.set(year, month, dayOfMonth)
    _bookingDetail.publishChanges()
  }

  fun setSelectedTime(hour: Int, minute: Int) {
    _bookingDetail.value?.selectedDateTime?.let { calendar ->
      calendar.set(Calendar.HOUR_OF_DAY, hour)
      calendar.set(Calendar.MINUTE, minute)
      calendar.clear(Calendar.SECOND)
    }
    _bookingDetail.publishChanges()
  }

  fun processBook() {
    // TODO: Add flow to check whether user is verified
    _bookingDetail.value?.let { booking ->
      launchViewModelScope({
        roomBookingUseCase.createBooking(booking).runFlow({
          _isBooked.value = it
        }, {
          _isBooked.value = false
        })
      })
    }
  }

  fun uploadReferralLetter(fileUri: Uri) {
    getUserId()?.let { id ->
      launchViewModelScope({
        roomBookingUseCase.uploadReferralLetter(id, fileUri).runFlow({
          _bookingDetail.value?.referralLetterFilePath = it
          _bookingDetail.publishChanges()
        })
      })
    }
  }
}