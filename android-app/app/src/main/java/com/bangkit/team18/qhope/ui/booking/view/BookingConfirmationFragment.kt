package com.bangkit.team18.qhope.ui.booking.view

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bangkit.team18.core.domain.model.hospital.RoomType
import com.bangkit.team18.core.utils.view.DataUtils
import com.bangkit.team18.core.utils.view.PickerUtils
import com.bangkit.team18.core.utils.view.ViewUtils.show
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentBookingConfirmationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import java.util.*

// TODO: Add checking condition for PDF
class BookingConfirmationFragment :
  BaseFragment<FragmentBookingConfirmationBinding, BookingConfirmationViewModel>(
    FragmentBookingConfirmationBinding::inflate, BookingConfirmationViewModel::class
  ) {

  companion object {
    private const val GOOGLE_DRIVE_VIEWER = "http://drive.google.com/viewer?url="
    private const val OPEN_TIME_PICKER = "OPEN TIME PICKER"
    private const val APPLICATION_PDF_TYPE = "application/pdf"
    private const val HTML_TYPE = "text/html"
  }

  private val args: BookingConfirmationFragmentArgs by navArgs()

  override fun setupViews() {
    binding.apply {
      buttonBookingConfirmBook.setOnClickListener(this@BookingConfirmationFragment)
      buttonBookingConfirmUploadLetter.setOnClickListener(this@BookingConfirmationFragment)
      layoutBookingTimeSelected.buttonBookingEditTime.setOnClickListener(
        this@BookingConfirmationFragment
      )
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.setBookingDetail(args.bookedHospital, args.roomType)
    viewModel.bookingDetail.observe(viewLifecycleOwner, {
      it?.let { bookingDetail ->
        setRoomData(bookingDetail.hospital.name, bookingDetail.selectedRoomType)
        setSelectedDate(bookingDetail.selectedDateTime)
        setSelectedTime(bookingDetail.selectedDateTime)
      }
    })
    viewModel.isBooked.observe(viewLifecycleOwner, {
      it?.let { isBooked ->
        if (isBooked) {
          openSuccessBookBottomSheet()
        }
      }
    })

  }

  override fun onResume() {
    super.onResume()
    setupCalendarView()
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let { fileUri ->
      viewModel.uploadReferralLetter(fileUri)
    }
  }

  override fun showLoadingState(isLoading: Boolean) {
    binding.apply {
      spinKitLoadBookingConfirm.showOrRemove(isLoading)
      layoutBookingConfirm.showOrRemove(isLoading.not())
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

  private fun openPdf(pdfUrl: String) {
    val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(Uri.parse(GOOGLE_DRIVE_VIEWER + pdfUrl), HTML_TYPE)
    }
    startActivity(pdfIntent)
  }

  private fun openSuccessBookBottomSheet() {
    SuccessBookBottomSheetDialogFragment.newInstance().show(
      parentFragmentManager,
      SuccessBookBottomSheetDialogFragment.OPEN_SUCCESS_BOOK_BOTTOM_SHEET
    )
  }

  private fun setReferralLetterData(fileName: String, fileUrl: String) {
    binding.cardBookingConfirmReferralLetter.apply {
      show()
      setFileName(fileName)
      setOnClickListener {
        openPdf(fileUrl)
      }
    }
  }

  private fun setRoomData(hospitalName: String, roomType: RoomType) {
    binding.layoutBookingSelectedRoomInfo.apply {
      textViewBookingSelectedRoom.text = getString(
        R.string.selected_room_type, roomType.name,
        hospitalName
      )
      textViewBookingSelectedRoomPrice.text = viewModel.mapToFormattedPrice(roomType.price)
    }
  }

  private fun setSelectedDate(calendar: Calendar) {
    binding.calendarBookingConfirmSelectDate.date = calendar.timeInMillis
  }

  private fun setSelectedTime(calendar: Calendar) {
    binding.layoutBookingTimeSelected.apply {
      root.show()
      textViewBookingTime.text =
        DataUtils.toFormattedTime(calendar.time, DataUtils.HH_MM_A_12H_FORMAT)
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
    val uploadPdfIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
      type = APPLICATION_PDF_TYPE
    }
    intentLauncher.launch(uploadPdfIntent)
  }
}