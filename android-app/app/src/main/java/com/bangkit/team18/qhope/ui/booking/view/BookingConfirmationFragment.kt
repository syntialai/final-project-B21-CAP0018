package com.bangkit.team18.qhope.ui.booking.view

import android.content.Intent
import android.net.Uri
import android.view.View
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.utils.view.DataUtils
import com.bangkit.team18.core.utils.view.PickerUtils
import com.bangkit.team18.core.utils.view.ViewUtils.show
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentBookingConfirmationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import java.util.Calendar

class BookingConfirmationFragment :
    BaseFragment<FragmentBookingConfirmationBinding>(FragmentBookingConfirmationBinding::inflate) {

  companion object {
    private const val GOOGLE_DRIVE_VIEWER = "http://drive.google.com/viewer?url="
    private const val OPEN_TIME_PICKER = "OPEN TIME PICKER"
    private const val APPLICATION_PDF_TYPE = "application/pdf"
    private const val HTML_TYPE = "text/html"
  }

  private val timePicker by lazy {
    PickerUtils.getTimePicker(R.string.select_check_in_time_label).apply {
      addOnPositiveButtonClickListener {
        // TODO: Call viewmodel to update time
      }
    }
  }

  override fun setupViews() {
    binding.apply {
      buttonBookingConfirmBook.setOnClickListener(this@BookingConfirmationFragment)
      buttonBookingConfirmUploadLetter.setOnClickListener(this@BookingConfirmationFragment)
      layoutBookingTimeSelected.buttonBookingEditTime.setOnClickListener(
          this@BookingConfirmationFragment)
    }
  }

  override fun onResume() {
    super.onResume()
    setupCalendarView()
  }

  override fun onIntentResult(data: Intent?) {
    data?.data?.let {
      val filePath = it
    }
  }

  override fun onClick(view: View?) {
    binding.apply {
      when (view) {
        buttonBookingConfirmBook -> { /* TODO: Call view model to confirm book */
        }
        buttonBookingConfirmUploadLetter -> uploadPdf()
        layoutBookingTimeSelected.buttonBookingEditTime -> timePicker.show(parentFragmentManager,
            OPEN_TIME_PICKER)
      }
    }
  }

  private fun openPdf(pdfUrl: String) {
    val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(Uri.parse(GOOGLE_DRIVE_VIEWER + pdfUrl), HTML_TYPE)
    }
    startActivity(pdfIntent)
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
      textViewBookingSelectedRoom.text = getString(R.string.selected_room_type, roomType.name,
          hospitalName)
      textViewBookingSelectedRoomPrice.text = roomType.price
    }
  }

  private fun setSelectedTime(time: String) {
    binding.layoutBookingTimeSelected.textViewBookingTime.text = time
  }

  private fun setupCalendarView() {
    val timestamp = DataUtils.getCurrentAnd3DaysLaterTimestamp()
    binding.calendarBookingConfirmSelectDate.apply {
      minDate = timestamp.first
      maxDate = timestamp.second
      setOnDateChangeListener { _, year, month, dayOfMonth ->
        val selectedDate = Calendar.getInstance().set(year, month, dayOfMonth)
        // TODO: Call view model to set selected date
        timePicker.show(parentFragmentManager, OPEN_TIME_PICKER)
      }
    }
  }

  private fun showLoadingState(isLoading: Boolean) {
    binding.apply {
      spinKitLoadBookingConfirm.showOrRemove(isLoading)
      layoutBookingConfirm.showOrRemove(isLoading.not())
    }
  }

  private fun uploadPdf() {
    val uploadPdfIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
      type = APPLICATION_PDF_TYPE
    }
    intentLauncher.launch(uploadPdfIntent)
  }
}