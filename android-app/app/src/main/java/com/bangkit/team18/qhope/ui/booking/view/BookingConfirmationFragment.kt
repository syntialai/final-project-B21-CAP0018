package com.bangkit.team18.qhope.ui.booking.view

import android.content.Context
import android.content.Intent
import android.os.FileUtils
import android.view.View
import androidx.core.net.toFile
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bangkit.team18.core.data.mapper.DataMapper
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.utils.view.DataUtils
import com.bangkit.team18.core.utils.view.FileUtil
import com.bangkit.team18.core.utils.view.PickerUtils
import com.bangkit.team18.core.utils.view.ViewUtils.show
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentBookingConfirmationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.booking.callback.RouteToCallback
import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import com.bangkit.team18.qhope.ui.history.view.HistoryFragmentDirections
import com.bangkit.team18.qhope.ui.home.view.HomeFragmentDirections
import com.bangkit.team18.qhope.ui.payment.MidtransPayment
import com.bangkit.team18.qhope.ui.payment.PaymentStatusListener
import com.bangkit.team18.qhope.ui.widget.callback.OnBannerActionButtonClickListener
import com.bangkit.team18.qhope.utils.Router
import timber.log.Timber
import java.io.File
import java.util.*

class BookingConfirmationFragment :
  BaseFragment<FragmentBookingConfirmationBinding, BookingConfirmationViewModel>(
    FragmentBookingConfirmationBinding::inflate, BookingConfirmationViewModel::class
  ), OnBannerActionButtonClickListener, RouteToCallback, PaymentStatusListener {

  companion object {
    private const val OPEN_TIME_PICKER = "OPEN TIME PICKER"
    private const val APPLICATION_PDF_TYPE = "application/pdf"
  }

  private var midtransPayment: MidtransPayment? = null

  private val args: BookingConfirmationFragmentArgs by navArgs()

  override fun onAttach(context: Context) {
    super.onAttach(context)
    midtransPayment = MidtransPayment(context, this)
  }

  override fun setupViews() {
    binding.apply {
      buttonBookingConfirmBook.setOnClickListener(this@BookingConfirmationFragment)
      buttonBookingConfirmUploadLetter.setOnClickListener(this@BookingConfirmationFragment)
      layoutBookingTimeSelected.buttonBookingEditTime.setOnClickListener(
        this@BookingConfirmationFragment
      )
      bannerInfoUpdateProfile.setActionButtonOnClickListener(this@BookingConfirmationFragment)
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.setBookingDetail(args.bookedHospital, args.roomType)
    viewModel.fetchUserDetails()
    viewModel.bookingDetail.observe(viewLifecycleOwner, {
      it?.let { bookingDetail ->
        setRoomData(bookingDetail.hospital.name, bookingDetail.selectedRoomType)
        setSelectedDate(bookingDetail.selectedDateTime)
        setSelectedTime(bookingDetail.selectedDateTime)
        setReferralLetterData(bookingDetail.referralLetterName, bookingDetail.referralLetterUri)
      }
    })
    viewModel.isBooked.observe(viewLifecycleOwner, {
      it?.let { isBooked ->
        if (isBooked) {
          midtransPayment?.setupPayments()
        }
      }
    })
    viewModel.isEnableBooking.observe(viewLifecycleOwner, { isEnableBooking ->
      enableProcessBooking(isEnableBooking.first, isEnableBooking.second)
    })
  }

  override fun onResume() {
    super.onResume()
    setupCalendarView()
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let { fileUri ->
      fileUri.path?.let { filePath ->
        viewModel.uploadReferralLetter(File(filePath))
      }
    }
  }

  override fun onClick(view: View?) {
    binding.apply {
      when (view) {
        buttonBookingConfirmBook -> viewModel.processBook()
        buttonBookingConfirmUploadLetter -> uploadPdf()
        layoutBookingTimeSelected.buttonBookingEditTime -> showTimePicker()
      }
    }
  }

  override fun onBannerButtonClicked() {
    Router.goToIdVerification(mContext, false)
  }

  override fun goToHome() {
    findNavController().navigate(HomeFragmentDirections.actionGlobalHomeFragment())
  }

  override fun goToHistory() {
    findNavController().navigate(HistoryFragmentDirections.actionGlobalHistoryFragment())
  }

  private fun enableProcessBooking(isVerified: Boolean, hasUploadedLetter: Boolean) {
    binding.apply {
      bannerInfoUpdateProfile.showOrRemove(isVerified.not())
      buttonBookingConfirmBook.isEnabled = isVerified.and(hasUploadedLetter)
    }
  }

  private fun openSuccessBookBottomSheet() {
    SuccessBookBottomSheetDialogFragment.newInstance(this).show(
      parentFragmentManager,
      SuccessBookBottomSheetDialogFragment.OPEN_SUCCESS_BOOK_BOTTOM_SHEET
    )
  }

  private fun setReferralLetterData(fileName: String, fileUrl: String) {
    if (listOf(fileName, fileUrl).all { it.isNotBlank() }) {
      binding.cardBookingConfirmReferralLetter.apply {
        show()
        setFileName(fileName)
        setOnClick {
          Router.openPdfFile(mContext, fileUrl)
        }
      }
    }
  }

  private fun setRoomData(hospitalName: String, roomType: RoomType) {
    binding.layoutBookingSelectedRoomInfo.apply {
      textViewBookingSelectedRoom.text = getString(
        R.string.selected_room_type, roomType.name,
        hospitalName
      )
      textViewBookingSelectedRoomPrice.text = DataMapper.toFormattedPrice(roomType.price)
    }
  }

  private fun setSelectedDate(calendar: Calendar) {
    binding.calendarBookingConfirmSelectDate.date = calendar.timeInMillis
  }

  private fun setSelectedTime(calendar: Calendar) {
    binding.layoutBookingTimeSelected.apply {
      root.show()
      textViewBookingTime.text =
        DataUtils.toFormattedDateTime(calendar.time, DataUtils.HH_MM_A_12H_FORMAT)
    }
  }

  private fun setupCalendarView() {
    val timestamp = DataUtils.getCurrentAnd3DaysLaterTimestamp()
    binding.calendarBookingConfirmSelectDate.apply {
      minDate = timestamp.first
      maxDate = timestamp.second
      setOnDateChangeListener { _, year, month, dayOfMonth ->
        viewModel.setSelectedDate(year, month, dayOfMonth)
        showTimePicker()
      }
    }
  }

  private fun showTimePicker() {
    val timeSelected = viewModel.getSelectedTime()
    val timePicker = PickerUtils.getTimePicker(
      R.string.select_check_in_time_label,
      timeSelected.first, timeSelected.second
    )
    timePicker.addOnPositiveButtonClickListener {
      viewModel.setSelectedTime(timePicker.hour, timePicker.minute)
    }
    if (timePicker.isVisible.not()) {
      timePicker.show(parentFragmentManager, OPEN_TIME_PICKER)
    }
  }

  private fun uploadPdf() {
    val uploadPdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = APPLICATION_PDF_TYPE
    }
    intentLauncher.launch(uploadPdfIntent)
  }

  override fun onPaymentSuccess(transactionId: String) {
    openSuccessBookBottomSheet()
  }

  override fun onPaymentPending(transactionId: String) {
    // TODO
  }

  override fun onPaymentFailed(statusMessage: String) {
    showErrorToast(statusMessage, R.string.unknown_error_message)
  }

  override fun onPaymentCancelled() {
    showErrorToast(null, R.string.payment_cancelled_message)
    goToHome()
  }
}