package com.bangkit.team18.qhope.ui.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.api.source.request.transaction.CreateTransactionRequest
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.booking.BookedHospital
import com.bangkit.team18.core.domain.model.booking.BookingDetail
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.MerchantUseCase
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.core.utils.view.DataUtils.areNotEmpty
import com.bangkit.team18.core.utils.view.DataUtils.orFalse
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.midtrans.sdk.corekit.models.TokenRequestModel
import com.midtrans.sdk.corekit.models.snap.Token
import timber.log.Timber
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

  private var _isBooked = MutableLiveData<Boolean>()
  val isBooked: LiveData<Boolean>
    get() = _isBooked

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
        referral_letter_name = booking.referralLetterName,
        payment_type = ""
      )
      Timber.d("Ceker ${request.room_type_id}")
//      launchViewModelScope({
//        roomBookingUseCase.createBooking(request).runFlow({
//          _isBooked.value = it
//        }, {
//          _isBooked.value = false
//        })
//      })
    }

    //bypass to _isBooked
    _isBooked.value = true
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