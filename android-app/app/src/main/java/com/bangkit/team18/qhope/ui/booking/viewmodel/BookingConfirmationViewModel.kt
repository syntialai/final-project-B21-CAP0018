package com.bangkit.team18.qhope.ui.booking.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.mapper.DataMapper
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.booking.BookedHospital
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.core.utils.view.DataUtils.areNotEmpty
import com.bangkit.team18.core.utils.view.DataUtils.orFalse
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.Timestamp
import java.util.Calendar

class BookingConfirmationViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val roomBookingUseCase: RoomBookingUseCase,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _bookingDetail = MutableLiveData<BookingDetail>()
  val bookingDetail: LiveData<BookingDetail>
    get() = _bookingDetail

  private var _isBooked = MutableLiveData<Boolean>()
  val isBooked: LiveData<Boolean>
    get() = _isBooked

  private var _userDetails = MutableLiveData<User>()

  private var _isEnableBooking = MediatorLiveData<Pair<Boolean, Boolean>>()
  val isEnableBooking: LiveData<Pair<Boolean, Boolean>>
    get() = _isEnableBooking

  init {
    initAuthStateListener()
    setupIsEnableBookingMediator()
  }

  override fun onCleared() {
    super.onCleared()
    _isEnableBooking.removeSource(_userDetails)
    _isEnableBooking.removeSource(_bookingDetail)
  }

  fun fetchUserDetails(userId: String, userPhoneNumber: String) {
    launchViewModelScope({
      userUseCase.getUser(userId).runFlow({ userData ->
        userData?.let {
          _userDetails.value = userData
          addUserData(userData, userPhoneNumber)
        }
      }, ::logOut)
    })
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
      val fileName = Timestamp.now().seconds.toString()
      launchViewModelScope({
        roomBookingUseCase.uploadReferralLetter(id, fileUri, fileName).runFlow({
          _bookingDetail.value?.referralLetterUri = it.toString()
          _bookingDetail.value?.referralLetterName = fileName
          _bookingDetail.publishChanges()
        })
      })
    }
  }

  private fun addUserData(userDetails: User, phoneNumber: String) {
    _bookingDetail.value?.user = userDetails
    _bookingDetail.value?.userPhoneNumber = phoneNumber
  }

  private fun getIsVerified(status: VerificationStatus?) = status == VerificationStatus.VERIFIED

  private fun setupIsEnableBookingMediator() {
    _isEnableBooking.addSource(_userDetails) {
      _isEnableBooking.postValue(
        Pair(
          getIsVerified(it.verificationStatus),
          _isEnableBooking.value?.second.orFalse()
        )
      )
    }
    _isEnableBooking.addSource(_bookingDetail) {
      _isEnableBooking.postValue(
        Pair(
          _isEnableBooking.value?.first.orFalse(),
          areNotEmpty(it.referralLetterName, it.referralLetterUri)
        )
      )
    }
  }
}