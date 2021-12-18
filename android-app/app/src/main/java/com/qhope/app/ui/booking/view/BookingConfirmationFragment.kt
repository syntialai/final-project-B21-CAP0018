package com.qhope.app.ui.booking.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.SdkUtil
import com.qhope.app.R
import com.qhope.app.databinding.FragmentBookingConfirmationBinding
import com.qhope.app.ui.base.view.BaseFragment
import com.qhope.app.ui.booking.callback.RouteToCallback
import com.qhope.app.ui.booking.viewmodel.BookingConfirmationViewModel
import com.qhope.app.ui.history.view.HistoryFragmentDirections
import com.qhope.app.ui.home.view.HomeFragmentDirections
import com.qhope.app.ui.payment.MidtransPayment
import com.qhope.app.ui.payment.PaymentStatusListener
import com.qhope.app.ui.widget.callback.OnBannerActionButtonClickListener
import com.qhope.app.utils.Router
import com.qhope.core.data.mapper.DataMapper
import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.utils.view.DataUtils
import com.qhope.core.utils.view.FileUtil
import com.qhope.core.utils.view.PickerUtils
import com.qhope.core.utils.view.ViewUtils.show
import com.qhope.core.utils.view.ViewUtils.showOrRemove
import kotlinx.coroutines.launch
import timber.log.Timber
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
    midtransPayment?.setupPayments()
    viewModel.transactionId.observe(viewLifecycleOwner, {
      it?.let { transactionId ->
        val mSdk = MidtransSDK.getInstance()
        mSdk.transactionRequest = midtransPayment?.initTransactionRequest(transactionId)
        viewModel.charge(SdkUtil.getSnapTokenRequestModel(mSdk.transactionRequest))
      }
    })
    viewModel.token.observe(viewLifecycleOwner, {
      it?.tokenId?.let { checkoutToken ->
        val mSdk = MidtransSDK.getInstance()
        mSdk.startPaymentUiFlow(context, checkoutToken)
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
      grantUriPermission(fileUri)
      viewLifecycleOwner.lifecycleScope.launch {
        FileUtil.getTemporaryFile(requireContext(), fileUri)
          ?.let { viewModel.uploadReferralLetter(it) }
      }
    }
  }

  private fun grantUriPermission(uri: Uri) {
    activity?.grantUriPermission(
      activity?.packageName.orEmpty(),
      uri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
    activity?.contentResolver?.takePersistableUriPermission(
      uri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
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
    try {
      SuccessBookBottomSheetDialogFragment.newInstance(this).show(
        parentFragmentManager,
        SuccessBookBottomSheetDialogFragment.OPEN_SUCCESS_BOOK_BOTTOM_SHEET
      )
    } catch (exception: Exception) {
      // No implementation needed
    }
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
      addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    intentLauncher.launch(uploadPdfIntent)
  }

  override fun onPaymentSuccess(transactionId: String) {
    openSuccessBookBottomSheet()
  }

  override fun onPaymentPending(transactionId: String) {
    Timber.d(transactionId)
  }

  override fun onPaymentFailed(statusMessage: String) {
    showErrorToast(statusMessage, R.string.unknown_error_message)
  }

  override fun onPaymentCancelled() {
    showErrorToast(null, R.string.payment_cancelled_message)
    goToHome()
  }
}