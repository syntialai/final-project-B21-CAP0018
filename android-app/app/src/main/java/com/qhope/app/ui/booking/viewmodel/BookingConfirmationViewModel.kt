package com.qhope.app.ui.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.source.request.transaction.CreateTransactionRequest
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.booking.BookedHospital
import com.qhope.core.domain.model.booking.BookingDetail
import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.model.user.VerificationStatus
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.MerchantUseCase
import com.qhope.core.domain.usecase.RoomBookingUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.utils.view.DataUtils.areNotEmpty
import com.qhope.core.utils.view.DataUtils.orFalse
import java.io.File
import java.util.Calendar

class BookingConfirmationViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val roomBookingUseCase: RoomBookingUseCase,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase,
  private val merchantUseCase: MerchantUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _bookingDetail = MutableLiveData<BookingDetail>()
  val bookingDetail: LiveData<BookingDetail>
    get() = _bookingDetail

  private var _transactionId = MutableLiveData<String?>()
  val transactionId: LiveData<String?>
    get() = _transactionId

  private var _userDetails = MutableLiveData<User>()

  private var _isEnableBooking = MediatorLiveData<Pair<Boolean, Boolean>>()
  val isEnableBooking: LiveData<Pair<Boolean, Boolean>>
    get() = _isEnableBooking

  private val _token = MutableLiveData<Token?>()
  val token: LiveData<Token?> get() = _token

  init {
    setupIsEnableBookingMediator()
  }

  override fun onCleared() {
    super.onCleared()
    _isEnableBooking.removeSource(_userDetails)
    _isEnableBooking.removeSource(_bookingDetail)
  }

  fun fetchUserDetails() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userDetails.value = it
        addUserData(it, it.phoneNumber)
      })
    })
  }

  fun getSelectedTime() = Pair(
    _bookingDetail.value?.selectedDateTime?.get(Calendar.HOUR),
    _bookingDetail.value?.selectedDateTime?.get(Calendar.MINUTE)
  )

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
      val request = CreateTransactionRequest(
        hospital_id = booking.hospital.id,
        room_type_id = booking.selectedRoomType.id,
        selected_date_time = booking.selectedDateTime.timeInMillis,
        referral_letter_url = booking.referralLetterUri,
        referral_letter_name = booking.referralLetterName
      )
      launchViewModelScope({
        roomBookingUseCase.createBooking(request).runFlow({
          _transactionId.value = it
        }, {
          showErrorResponse("No transactionId.")
        })
      })
    }
  }

  fun uploadReferralLetter(file: File) {
    launchViewModelScope({
      roomBookingUseCase.uploadReferralLetter(file).runFlow({ referralLetter ->
        _bookingDetail.value?.referralLetterUri = referralLetter.url
        _bookingDetail.value?.referralLetterName = file.name
        _bookingDetail.publishChanges()
      })
    })
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

  fun charge(var1: TokenRequestModel?) {
    launchViewModelScope({
      merchantUseCase.charge(var1).runFlow({
        _token.value = it
      }, {
        showErrorResponse("No token.")
      })
    })
  }
}